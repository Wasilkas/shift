package ftc.shift.sample.repositories;

import ftc.shift.sample.models.Question;
import ftc.shift.sample.models.QuestionList;
import ftc.shift.sample.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Реализиция, хранящая все данные в БД
 */
@Repository
@ConditionalOnProperty(name = "use.database", havingValue = "true")
public class DatabaseQuestionRepository implements QuestionRepository {
    private NamedParameterJdbcTemplate jdbcTemplate;
    private QuestionExtractor questionExtractor;

    @Autowired
    public DatabaseQuestionRepository(NamedParameterJdbcTemplate jdbcTemplate,
                                  QuestionExtractor questionExtractor) {
        this.jdbcTemplate = jdbcTemplate;
        this.questionExtractor = questionExtractor;
    }

    @PostConstruct
    public void initialize() {
        // Подразумевается, что H2 работает в in-memory режиме и таблицы необходимо создавать при каждом старте приложения
        // SQL запросы для создания таблиц
        String createGenerateQuestionIdSequenceSql = "create sequence QUESTION_ID_GENERATOR";

        String createQuestionTableSql = "create table QUESTIONS (" +
                "QUESTION_ID  VARCHAR(64) default QUESTION_ID_GENERATOR.nextval," +
                "TEXT     VARCHAR(64)," +
                "CORRECT_ANSWER  VARCHAR(64)," +
                "AUTHOR    VARCHAR(64)," +
                "SUBJECT  VARCHAR(64)," +
                ");";

        jdbcTemplate.update(createGenerateQuestionIdSequenceSql, new MapSqlParameterSource());
        jdbcTemplate.update(createQuestionTableSql, new MapSqlParameterSource());

        // Заполним таблицы тестовыми данными
        // createQuestion("UserA", new Question("1", "Название 1", "Автор Авторович", 12,
        //         Arrays.asList("Фантастика", "Драма", "Нуар")));

        // createQuestion("UserA", new Question("2", "Название 2", "Автор Писателевич", 48,
        //         Collections.singletonList("Детектив")));

        // createQuestion("UserB", new Question("3", "Название 3", "Писатель Авторович", 24,
        //         Collections.singletonList("Киберпанк")));
    }

    @Override
    public QuestionList getAllQuestions(String userId, String subject, String page, String order) {
        String sql = "select QUESTION_ID, TEXT, CORRECT_ANSWER, AUTHOR, SUBJECT" +
                " from QUESTIONS";

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (subject != null && userId != null) {
            sql += " where SUBJECT = :subject and AUTHOR = :userId";

            params.addValue("userId", userId)
                    .addValue("subject", subject);
        }
        else {
            if (subject != null) {
                sql += " where SUBJECT = :subject";

                params.addValue("subject", subject);
            }
            else {
                if (userId != null) {
                    sql += " where AUTHOR = :userId";

                    params.addValue("userId", userId);
                }
            }
        }

        List<Question> questions = jdbcTemplate.query(sql, params, questionExtractor);

        if (questions == null) {
            return new QuestionList(new ArrayList<Question>(), 0);
        }

        if (order.equals("1")) {
            questions.sort(Comparator.comparing(q -> Integer.parseInt(q.getId())));
        }
        else {
            questions.sort(Comparator.comparing(q -> -Integer.parseInt(q.getId())));
        }

        if (page.equals("0")) {
            return new QuestionList(questions, 0);
        }

        int listSize = questions.size();
        final int QUESTION_AMOUNT = 10;
        int intPage = Integer.parseInt(page);

        int lastIndex = (questions.size() - 1) % QUESTION_AMOUNT;
        questions = (questions.size() <= intPage * QUESTION_AMOUNT) ?
                questions.subList((intPage - 1) * QUESTION_AMOUNT, (intPage - 1) * QUESTION_AMOUNT + lastIndex + 1) :
                questions.subList((intPage - 1) * QUESTION_AMOUNT, (intPage - 1) * QUESTION_AMOUNT + QUESTION_AMOUNT);

        return listSize % QUESTION_AMOUNT > 0 ? new QuestionList(questions, listSize / QUESTION_AMOUNT + 1) :
                new QuestionList(questions, listSize / QUESTION_AMOUNT);
    }

    @Override
    public QuestionList getTestQuestions(String subject, String questionsCount) {
        return null;
    }

    @Override
    public Question fetchQuestion(String questionId) {
        String sql = "select USER_ID, QUESTIONS.QUESTION_ID, NAME, AUTHOR, PAGES, GENRE " +
                "from QUESTIONS, GENRES " +
                "where QUESTIONS.QUESTION_ID = GENRES.QUESTION_ID and QUESTIONS.QUESTION_ID=:questionId and QUESTIONS.USER_ID=:userId";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("questionId", questionId);

        List<Question> questions = jdbcTemplate.query(sql, params, questionExtractor);

        if (questions.isEmpty()) {
            return null;
        }

        return questions.get(0);
    }

    @Override
    public void deleteQuestion(String userId, String questionId) {
        String deleteGenresSql = "delete from GENRES where QUESTION_ID=:questionId";
        String deleteQuestionSql = "delete from QUESTIONS where QUESTION_ID=:questionId";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("questionId", questionId);

        jdbcTemplate.update(deleteGenresSql, params);
        jdbcTemplate.update(deleteQuestionSql, params);
    }

    @Override
    public Question createQuestion(String userId, Question Question) {
        // Добавляем книгу
        String insertQuestionSql = "insert into QUESTIONS (USER_ID, NAME, AUTHOR, PAGES) values (:userId, :name, :author, :pages)";

        // (!) При этом мы не указываем значения для столбца QUESTION_ID.
        // Он будет сгенерирован автоматически на стороне БД
        MapSqlParameterSource questionParams = new MapSqlParameterSource();

        // Класс, который позволит получить сгенерированный questionId
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(insertQuestionSql, questionParams, generatedKeyHolder);

        String questionId = generatedKeyHolder.getKeys().get("QUESTION_ID").toString();

        return Question;
    }

    @Override
    public Question updateQuestion(String userId, String questionId, Question Question) {
        // 1) Обновляем информацию о книге
        String updateQuestionSql = "update QUESTIONS " +
                "set USER_ID=:userId, " +
                "NAME=:name, " +
                "AUTHOR=:author, " +
                "PAGES=:pages " +
                "where QUESTION_ID=:questionId";

        MapSqlParameterSource questionParams = new MapSqlParameterSource()
                .addValue("questionId", questionId)
                .addValue("userId", userId);

        jdbcTemplate.update(updateQuestionSql, questionParams);

        // 2) Удаляем старые жанры
        String deleteGenresSql = "delete from GENRES where QUESTION_ID=:questionId";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("questionId", questionId);

        jdbcTemplate.update(deleteGenresSql, params);


        return Question;
    }
}
