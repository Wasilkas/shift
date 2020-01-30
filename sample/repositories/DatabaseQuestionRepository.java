package ftc.shift.sample.repositories;

import ftc.shift.sample.models.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
                "AUTHOR    VARCHAR(64)" +
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
    public Collection<Question> getAllQuestions() {
        String sql = "select QUESTIONS.QUESTION_ID, TEXT, CORRECT_ANSWER, AUTHOR, SUBJECT";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId);

        return jdbcTemplate.query(sql, params, questionExtractor);
    }

    @Override
    public Question fetchQuestion(String userId, String questionId) {
        String sql = "select USER_ID, QUESTIONS.QUESTION_ID, NAME, AUTHOR, PAGES, GENRE " +
                "from QUESTIONS, GENRES " +
                "where QUESTIONS.QUESTION_ID = GENRES.QUESTION_ID and QUESTIONS.QUESTION_ID=:questionId and QUESTIONS.USER_ID=:userId";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
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
        MapSqlParameterSource questionParams = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("name", Question.getName())
                .addValue("author", Question.getAuthor())
                .addValue("pages", Question.getPages());

        // Класс, который позволит получить сгенерированный questionId
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(insertQuestionSql, questionParams, generatedKeyHolder);

        String questionId = generatedKeyHolder.getKeys().get("QUESTION_ID").toString();
        Question.setId(questionId);

        for (String genre : Question.getGenre()) {
            String insertGenreSql = "insert into GENRES (QUESTION_ID, GENRE) values (:questionId, :genre)";

            // Он будет сгенерирован автоматически на стороне БД
            MapSqlParameterSource genreParams = new MapSqlParameterSource()
                    .addValue("questionId", questionId)
                    .addValue("genre", genre);

            jdbcTemplate.update(insertGenreSql, genreParams);
        }

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
                .addValue("userId", userId)
                .addValue("name", Question.getName())
                .addValue("author", Question.getAuthor())
                .addValue("pages", Question.getPages());

        jdbcTemplate.update(updateQuestionSql, questionParams);

        // 2) Удаляем старые жанры
        String deleteGenresSql = "delete from GENRES where QUESTION_ID=:questionId";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("questionId", questionId);

        jdbcTemplate.update(deleteGenresSql, params);

        // 3) Добавляем новые жанры
        for (String genre : Question.getGenre()) {
            String insertGenreSql = "insert into GENRES (QUESTION_ID, GENRE) values (:questionId, :genre)";

            // Он будет сгенерирован автоматически на стороне БД
            MapSqlParameterSource genreParams = new MapSqlParameterSource()
                    .addValue("questionId", questionId)
                    .addValue("genre", genre);

            jdbcTemplate.update(insertGenreSql, genreParams);
        }

        return Question;
    }
}
