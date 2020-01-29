package ftc.shift.sample.repositories;

import ftc.shift.sample.exception.NotFoundException;
import ftc.shift.sample.models.Question;
import jdk.nashorn.internal.ir.IdentNode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализиция, хранящая все данные в памяти приложения
 */
@Repository
//@ConditionalOnProperty(name = "use.database", havingValue = "false")
public class InMemoryQuestionRepository implements QuestionRepository {
    /**
     * Ключ - имя пользователя, значение - все книги, которые есть у пользователя
     */
    private List<Question> questionCache = new ArrayList<Question>();
    private Integer localId;

    public InMemoryQuestionRepository() {
        // Заполним репозиторий тестовыми данными
        // В тестовых данных существует всего 3 пользователя: UserA / UserB / UserC

        questionCache.add(new Question("2", "Хто я?", "артем", "UserA", "филасафия"));
        questionCache.add(new Question("5", "Хто ты?qwq", "антон", "UserB", "филасафия"));
        questionCache.add(new Question("6", "Хто ты?333", "антон", "UserA", "матан"));
        questionCache.add(new Question("3", "Хто ты?adf", "антон", "UserC", "матан"));
        questionCache.add(new Question("4", "Хто ты?faf", "антон", "UserE", "история"));
        questionCache.add(new Question("7", "Хто ты?asd", "антон", "UserF", "филасафия"));
        questionCache.add(new Question("8", "Хто ты?dasd", "антон", "UserF", "история"));
        questionCache.add(new Question("9", "Хто ты?asd", "антон", "UserE", "история"));
        questionCache.add(new Question("10", "Хто ты?s", "антон", "UserB", "история"));
        questionCache.add(new Question("11", "Хто тыdc?", "антон", "UserB", "филасафия"));
        questionCache.add(new Question("1", "Хто тыr?", "антон", "UserB", "филасафия"));
        localId = questionCache.size();
    }

    @Override
    public Question fetchQuestion(String questionId) {
        for (Question q : questionCache)
        {
            if (q.getId().equals(questionId))
                 return q;
        }
        throw new RuntimeException();
    }

     @Override
     public Question updateQuestion(String userId, String questionId, Question question) {
        List<Question> userQuestions = questionCache.stream()
                .filter(i -> i.getAuthor().equals(userId))
                .collect(Collectors.toList());

        if (userQuestions.size() == 0)
            throw new NotFoundException();

        for (Question q : userQuestions)
        {
            if (q.getId().equals(questionId)) {
                q.setText(question.getText());
                q.setCorrectAnswer(question.getCorrectAnswer());
                q.setSubject(question.getSubject());
                return q;
            }
        }

        throw new NotFoundException();
     }

     @Override
     public void deleteQuestion(String userId, String questionId) {
         questionCache.removeIf(q -> q.getAuthor().equals(userId) && q.getId().equals(questionId));
     }

    @Override
    public Question createQuestion(String userId, Question question) {

        question.setId(String.valueOf(++localId));
        question.setAuthor(userId);
        questionCache.add(question);
        return question;
    }

    @Override
    public Collection<Question> getAllQuestions(String userId, String subject, String page, String order) {

        int intPage = Integer.parseInt(page);

        if (order.equals("1"))
            questionCache.sort(Comparator.comparing(q -> Integer.parseInt(q.getId())));
        else
            questionCache.sort(Comparator.comparing(q -> -Integer.parseInt(q.getId())));

        List<Question> userQuestions = questionCache;
        int lastIndex = (userQuestions.size() - 1) % 10;

        if (userId != null && subject != null)
            userQuestions = userQuestions.stream().filter(i -> i.getAuthor().equals(userId)
                    && i.getSubject().equals(subject)).collect(Collectors.toList());
        else
            if (userId != null)
                userQuestions = userQuestions.stream().filter(i -> i.getAuthor().equals(userId)).collect(Collectors.toList());
            else
                if (subject != null)
                    userQuestions = userQuestions.stream().filter(i -> i.getSubject().equals(subject)).collect(Collectors.toList());

        userQuestions = (userQuestions.size() <= intPage * 10) ?
                userQuestions.subList((intPage - 1) * 10, (intPage - 1) * 10 + lastIndex + 1) :
                userQuestions.subList((intPage - 1) * 10, (intPage - 1) * 10 + 10);

        return userQuestions;
    }
}
