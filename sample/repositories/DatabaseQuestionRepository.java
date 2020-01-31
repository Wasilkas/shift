package ftc.shift.sample.repositories;

import ftc.shift.sample.exception.AccessDeniedException;
import ftc.shift.sample.exception.NotFoundException;
import ftc.shift.sample.models.Question;
import ftc.shift.sample.models.QuestionList;
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
    public static final int QUESTION_AMOUNT = 10;

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
                "TEXT     VARCHAR(128)," +
                "CORRECT_ANSWER  VARCHAR(64)," +
                "AUTHOR    VARCHAR(64)," +
                "SUBJECT  VARCHAR(64)," +
                ");";

        jdbcTemplate.update(createGenerateQuestionIdSequenceSql, new MapSqlParameterSource());
        jdbcTemplate.update(createQuestionTableSql, new MapSqlParameterSource());

        createQuestion("Антон", new Question("1", "Почему люди думают, что их многочисленные селфи кому-то интересны?", "Разве не интересны:с", "Антон", "Философия"));
        createQuestion("Антон", new Question("2", "Ответ на главный вопрос со вселенной", "42", "Антон", "Философия"));
        createQuestion("Артём", new Question("3", "Как долго человек сможет прожить, питаясь только Сникерсом и водой?", "Долго...(относительно)", "Артём", "Философия"));
        createQuestion("Женя", new Question("4", "В комнате 4 угла. В каждом углу сидела кошка, напротив каждой кошки - 3 кошки. Сколько кошек находилось в комнате?", "4", "Женя", "Математика"));
        createQuestion("Женя", new Question("5", "Докажите Малую теорему Ферма", "??????", "Женя", "Математика"));
        createQuestion("Женя", new Question("6", "Год основания ЦФТ", "1991", "Женя", "Математика")); // ВОПРОС ПО ИСТОРИИ
        createQuestion("Артём", new Question("7", "Основатель Санкт-Петербурга", "Пётр Великий", "Артём", "История"));
        createQuestion("Артём", new Question("8", "Сколько бит в одном байте?", "8", "Артём", "Информатика"));
        createQuestion("Антон", new Question("9", "Как назывались китайские счёты в XVI веке?", "Суаньпань", "Антон", "Информатика"));
        createQuestion("Женя", new Question("10", "Можно ли вытаскивать флешку из компуктера небезопасно?", "Лучше не рисковать...", "Женя", "Информатика"));
        createQuestion("Женя", new Question("11", "Какой алгоритм на графах позволяет найти кратчайший путь от заданной до всех остальных?", "Алгоритм Дейкстры", "Женя", "Информатика"));
        //createQuestion("Артём", new Question("10", "Kotlin или Java?", "Java!!1!1!!1!1", "Артём", "Информатика"));

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
        int intPage = Integer.parseInt(page);

        int lastIndex = (listSize - 1) % QUESTION_AMOUNT;
        questions = (listSize <= intPage * QUESTION_AMOUNT) ?
                questions.subList((intPage - 1) * QUESTION_AMOUNT, (intPage - 1) * QUESTION_AMOUNT + lastIndex + 1) :
                questions.subList((intPage - 1) * QUESTION_AMOUNT, (intPage - 1) * QUESTION_AMOUNT + QUESTION_AMOUNT);

        return listSize % QUESTION_AMOUNT > 0 ? new QuestionList(questions, listSize / QUESTION_AMOUNT + 1) :
                new QuestionList(questions, listSize / QUESTION_AMOUNT);
    }

    @Override
    public QuestionList getTestQuestions(String subject, String questionsCount) {
        String sql = "select QUESTION_ID, TEXT, CORRECT_ANSWER, AUTHOR, SUBJECT" +
                " from QUESTIONS " +
                "where SUBJECT=:subject";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("subject", subject);

        List<Question> questions = jdbcTemplate.query(sql, params, questionExtractor);

        if (questions != null && questions.size() >= Integer.parseInt(questionsCount)) {
            Random rand = new Random();
            while (questions.size() != Integer.parseInt(questionsCount)) {
                questions.remove(rand.nextInt(questions.size()));
            }

            return new QuestionList(questions, 1);
        }
        throw new NotFoundException();
    }

    @Override
    public Question fetchQuestion(String questionId) {
        String sql = "select QUESTION_ID, TEXT, CORRECT_ANSWER, AUTHOR, SUBJECT" +
                " from QUESTIONS" +
                " where QUESTION_ID = :questionId";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("questionId", questionId);

        List<Question> questions = jdbcTemplate.query(sql, params, questionExtractor);

        if (questions == null || questions.size() == 0) {
            throw new NotFoundException();
        }

        return questions.get(0);
    }

    @Override
    public void deleteQuestion(String userId, String questionId) {
        String deleteQuestionSql = "delete from QUESTIONS where QUESTION_ID=:questionId and AUTHOR=:userId";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("questionId", questionId)
                .addValue("userId", userId);

        jdbcTemplate.update(deleteQuestionSql, params);
    }

    @Override
    public Question createQuestion(String userId, Question question) {
        // Добавляем книгу
        String insertQuestionSql = "insert into QUESTIONS (TEXT, CORRECT_ANSWER, AUTHOR, SUBJECT) " +
                "values (:text, :correctAnswer, :author, :subject)";

        // (!) При этом мы не указываем значения для столбца QUESTION_ID.
        // Он будет сгенерирован автоматически на стороне БД
        MapSqlParameterSource questionParams = new MapSqlParameterSource()
                .addValue("text", question.getText())
                .addValue("correctAnswer", question.getCorrectAnswer())
                .addValue("author", userId)
                .addValue("subject", question.getSubject());

        // Класс, который позволит получить сгенерированный questionId
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(insertQuestionSql, questionParams, generatedKeyHolder);

        String questionId = Objects.requireNonNull(generatedKeyHolder.getKeys()).get("QUESTION_ID").toString();

        return new Question(questionId,
                question.getText(),
                question.getCorrectAnswer(),
                userId,
                question.getSubject());
    }

    @Override
    public Question updateQuestion(String userId, String questionId, Question question) {
        String fetchSql = "select QUESTION_ID, TEXT, CORRECT_ANSWER, AUTHOR, SUBJECT " +
                "from QUESTIONS " +
                "where QUESTION_ID=:questionId";

        MapSqlParameterSource checkQuestionParams = new MapSqlParameterSource()
                .addValue("questionId", questionId);

        List<Question> checkingQuestions = jdbcTemplate.query(fetchSql, checkQuestionParams, questionExtractor);

        if (checkingQuestions == null){
            throw new NotFoundException();
        }

        Question checkingQuestion = checkingQuestions.get(0);

        if (checkingQuestion.getAuthor().equals(userId)) {
            // 1) Обновляем информацию о вопросе
            String updateQuestionSql = "update QUESTIONS " +
                    "set TEXT=:text, " +
                    "CORRECT_ANSWER=:correctAnswer, " +
                    "SUBJECT=:subject " +
                    "where QUESTION_ID=:questionId " +
                    "and AUTHOR=:userId";

            MapSqlParameterSource questionParams = new MapSqlParameterSource()
                    .addValue("text", question.getText())
                    .addValue("correctAnswer", question.getCorrectAnswer())
                    .addValue("subject", question.getSubject())
                    .addValue("questionId", questionId)
                    .addValue("userId", userId);

            jdbcTemplate.update(updateQuestionSql, questionParams);

            return new Question(questionId,
                    question.getText(),
                    question.getCorrectAnswer(),
                    question.getAuthor(),
                    question.getSubject());
        }
        throw new AccessDeniedException();
    }
}
