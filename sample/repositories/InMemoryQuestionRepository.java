package ftc.shift.sample.repositories;

import ftc.shift.sample.exception.AccessDeniedException;
import ftc.shift.sample.exception.NotFoundException;
import ftc.shift.sample.models.Question;
import ftc.shift.sample.models.QuestionList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name="use.database", havingValue = "false")
public class InMemoryQuestionRepository implements QuestionRepository {
    // Список всех книг
    private List<Question> questionCache = new ArrayList<>();
    private int localId = 0;

    @Autowired
    public InMemoryQuestionRepository() {
        questionCache.add(new Question(String.valueOf(++localId), "Хто я?", "артем", "UserA", "филасафия"));
        questionCache.add(new Question(String.valueOf(++localId), "Хто ты?qwq", "антон", "UserB", "филасафия"));
        questionCache.add(new Question(String.valueOf(++localId), "Хто ты?333", "антон", "UserA", "матан"));
        questionCache.add(new Question(String.valueOf(++localId), "Хто ты?adf", "антон", "UserC", "матан"));
        questionCache.add(new Question(String.valueOf(++localId), "Хто ты?faf", "антон", "UserE", "история"));
        questionCache.add(new Question(String.valueOf(++localId), "Хто ты?asd", "антон", "UserF", "филасафия"));
        questionCache.add(new Question(String.valueOf(++localId), "Хто ты?dasd", "антон", "UserF", "история"));
        questionCache.add(new Question(String.valueOf(++localId), "Хто ты?asd", "антон", "UserE", "история"));
        questionCache.add(new Question(String.valueOf(++localId), "Хто ты?s", "антон", "UserB", "история"));
        questionCache.add(new Question(String.valueOf(++localId), "Хто тыdc?", "антон", "UserB", "филасафия"));
        questionCache.add(new Question(String.valueOf(++localId), "Хто тыr?", "антон", "UserB", "филасафия"));
        localId = questionCache.size();
    }

    @Override
    public Question fetchQuestion(String questionId) {
        for (Question q : questionCache) {
            if (q.getId().equals(questionId)) {
                return q;
            }
        }
        // Вопрос не найден
        throw new NotFoundException();
    }

     @Override
     public Question updateQuestion(String userId, String questionId, Question question) {

        // Список вопросов пуст
        if (questionCache.size() == 0) {
            throw new NotFoundException();
        }

        for (Question q : questionCache) {
            if (q.getId().equals(questionId)) {
                if (q.getAuthor().equals(userId)) {
                    q = new Question(questionId,
                            question.getText(),
                            question.getCorrectAnswer(),
                            userId,
                            question.getSubject());
                    return q;
                }
                else {
                    //
                    throw new AccessDeniedException();
                }
            }
        }
        // Вопрос не найден
        throw new NotFoundException();
     }

     @Override
     public void deleteQuestion(String userId, String questionId) {
        // Поиск вопроса по идентификатору
        List<Question> questions = questionCache.stream()
                .filter(q -> q.getId().equals(questionId))
                .collect(Collectors.toList());

        if (questions.size() == 0) {
            // Вопрос не найден
            throw new NotFoundException();
        }

        if (!questionCache.removeIf(q ->  q.getId().equals(questionId) && q.getAuthor().equals(userId))) {
            //
            throw new AccessDeniedException();
        }
     }

    @Override
    public Question createQuestion(String userId, Question question) {

        Question localQuestion = new Question(String.valueOf(++localId),
                question.getText(),
                question.getCorrectAnswer(),
                userId,
                question.getSubject());

        questionCache.add(localQuestion);

        return localQuestion;
    }

    @Override
    public QuestionList getAllQuestions(String userId, String subject, String page, String order) {
        // Количество вопросов на странице
        final int QUESTION_AMOUNT = 10;
        // Номер текущей страницы
        int intPage = Integer.parseInt(page);

        // Сортировка по возрастанию/убыванию порядкого номера идентификатора
        if (order.equals("1")) {
            questionCache.sort(Comparator.comparing(q -> Integer.parseInt(q.getId())));
        }
        else {
            questionCache.sort(Comparator.comparing(q -> -Integer.parseInt(q.getId())));
        }

        List<Question> questions = questionCache;

        // Фильтрация по Автору и/или Предмету вопроса
        if (userId != null && subject != null) {
            questions = questions.stream()
                    .filter(i -> i.getAuthor().equals(userId) && i.getSubject().equals(subject))
                    .collect(Collectors.toList());
        }
        else {
            if (userId != null) {
                questions = questions.stream()
                        .filter(i -> i.getAuthor().equals(userId))
                        .collect(Collectors.toList());
            }
            else {
                if (subject != null) {
                    questions = questions.stream()
                            .filter(i -> i.getSubject().equals(subject))
                            .collect(Collectors.toList());
                }
            }
        }

        // Для андроида: возвращаем все вопросы
        if (page.equals("0")) {
            return new QuestionList(questions, 0);
        }

        // Количество вопросов подходящих по фильтру
        int listSize = questions.size();

        // Индекс - 1 последнего в десятке вопроов
        int lastIndex = listSize % QUESTION_AMOUNT;
        /*
         * Пример: всего 35 вопросов listSize
         *         страница номер 4  page
         *         Значит на странице 4 отобразится 5 вопросов:
         *         от 31 до 35
         *
         * Если количество вопросов меньше номера текущей страницы, умноженного 10,
         *         то возвращаем список от (номера - 1) до индекса последнего вопроса
         *         иначе возвращаем список от (номера - 1) до 10
         *
         */
        questions = (listSize <= intPage * QUESTION_AMOUNT) ?
                questions.subList((intPage - 1) * QUESTION_AMOUNT, (intPage - 1) * QUESTION_AMOUNT + lastIndex) :
                questions.subList((intPage - 1) * QUESTION_AMOUNT, (intPage - 1) * QUESTION_AMOUNT + QUESTION_AMOUNT);
        /*
         *
         * Корректная отправка размера всех вопросов (подходящих под фильтр)
         *
         */
        return listSize % QUESTION_AMOUNT > 0 ? new QuestionList(questions, listSize / QUESTION_AMOUNT + 1) :
                new QuestionList(questions, listSize / QUESTION_AMOUNT);
    }

    @Override
    public QuestionList getTestQuestions(String subject, String questionsCount) {
        List<Question> questions = questionCache.stream()
                .filter(i -> i.getSubject().equals(subject))
                .collect(Collectors.toList());

        Random rand = new Random();

        if (Integer.parseInt(questionsCount) > questions.size()) {
            // Вопросы не найдены
            throw new NotFoundException();
        }

        while (questions.size() != Integer.parseInt(questionsCount)) {
            questions.remove(rand.nextInt(questions.size()));
        }

        return new QuestionList(questions, 1);
    }
}
