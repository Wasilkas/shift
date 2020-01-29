package ftc.shift.sample.repositories;

import ftc.shift.sample.exception.NotFoundException;
import ftc.shift.sample.models.Question;
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
        questionCache.add(new Question("1", "Хто ты?", "антон", "UserB", "филасафия"));
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

        // Плохой способ генерирования случайных идентификаторов, использовать только для примеров
        question.setId(String.valueOf(++localId));
        question.setAuthor(userId);
        questionCache.add(question);
        return question;
    }

    @Override
    public Collection<Question> getAllQuestions(String userId, String subject) {

        questionCache.sort(Comparator.comparing(Question::getId));
        List<Question> userQuestions = questionCache;

        if (userId != null && subject != null)
            userQuestions = userQuestions.stream().filter(i -> i.getAuthor().equals(userId)
                    && i.getSubject().equals(subject)).collect(Collectors.toList());
        else
            if (userId != null)
                userQuestions = userQuestions.stream().filter(i -> i.getAuthor().equals(userId)).collect(Collectors.toList());
            else
                if (subject != null)
                    userQuestions = userQuestions.stream().filter(i -> i.getSubject().equals(subject)).collect(Collectors.toList());

        return userQuestions;
    }
}
