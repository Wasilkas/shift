package ftc.shift.sample.repositories;

import ftc.shift.sample.exception.AccessDeniedException;
import ftc.shift.sample.exception.NotFoundException;
import ftc.shift.sample.models.Question;
import jdk.nashorn.internal.ir.IdentNode;
import org.graalvm.compiler.core.common.type.ArithmeticOpTable;
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
        throw new NotFoundException();
    }

     @Override
     public Question updateQuestion(String userId, String questionId, Question question) {

        if (questionCache.size() == 0)
            throw new NotFoundException();

        for (Question q : questionCache)
        {
            if (q.getId().equals(questionId))
                if (q.getAuthor().equals(userId)) {
                    if (question.getText() != null)
                        q.setText(question.getText());
                    if (question.getCorrectAnswer() != null)
                        q.setCorrectAnswer(question.getCorrectAnswer());
                    if (question.getSubject() != null)
                        q.setSubject(question.getSubject());
                    return q;
                } else throw new AccessDeniedException();
        }
        throw new NotFoundException();
     }

     @Override
     public void deleteQuestion(String userId, String questionId) {

        List<Question> questions = questionCache.stream().filter(i -> i.getId().equals(questionId)).collect(Collectors.toList());

        if (questions.size() == 0)
            throw new NotFoundException();

        if (!questions.removeIf(q -> q.getAuthor().equals(userId)))
            throw new AccessDeniedException();
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

        List<Question> questions = questionCache;

        if (userId != null && subject != null) {
            questions = questions.stream().filter(i -> i.getAuthor().equals(userId)
                    && i.getSubject().equals(subject)).collect(Collectors.toList());
        }
        else {
            if (userId != null) {
                questions = questions.stream().filter(i -> i.getAuthor().equals(userId)).collect(Collectors.toList());
            }
            else {
                if (subject != null) {
                    questions = questions.stream().filter(i -> i.getSubject().equals(subject)).collect(Collectors.toList());
                }
            }
        }

        int lastIndex = (questions.size() - 1) % 10;
        questions = (questions.size() <= intPage * 10) ?
                questions.subList((intPage - 1) * 10, (intPage - 1) * 10 + lastIndex + 1) :
                questions.subList((intPage - 1) * 10, (intPage - 1) * 10 + 10);

        return questions;
    }

    @Override
    public Collection<Question> getTestQuestions(String subject, String questionsCount) {
        List<Question> questions = questionCache.stream().filter(i -> i.getSubject().equals(subject)).collect(Collectors.toList());

        Random rand = new Random();
        int i;
        if (Integer.parseInt(questionsCount) > questions.size())
            throw new NotFoundException();

        while(questions.size() != Integer.parseInt(questionsCount))
        {
            i = rand.nextInt(questions.size());
            questions.remove(i);
        }
        return questions;
    }
}
