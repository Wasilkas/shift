package ftc.shift.sample.repositories;

import ftc.shift.sample.exception.NotFoundException;
import ftc.shift.sample.models.Question;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.List;

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

    public InMemoryQuestionRepository() {
        // Заполним репозиторий тестовыми данными
        // В тестовых данных существует всего 3 пользователя: UserA / UserB / UserC

        questionCache.add(new Question("1", "Хто я?", "артем", "UserA", "филасафия"));
        questionCache.add(new Question("2", "Хто ты?", "антон", "UserA", "филасафия"));
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
        List<Question> userQuestions = questionCache.stream().filter(i -> i.getAuthor().equals(userId)).collect(Collectors.toList());

        if (userQuestions.size() == 0)
            throw new NotFoundException();

        for (Question q : userQuestions)
        {
            if (q.getId().equals(questionId)) {
                q.setId(questionId);
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
        question.setId(String.valueOf(System.currentTimeMillis()));
        question.setAuthor(userId);
        questionCache.add(question);
        return question;
    }

    @Override
    public Collection<Question> getAllQuestions() {
        return questionCache;
    }
}
