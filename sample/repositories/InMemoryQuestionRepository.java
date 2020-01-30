package ftc.shift.sample.repositories;

import ftc.shift.sample.exception.AccessDeniedException;
import ftc.shift.sample.exception.NotFoundException;
import ftc.shift.sample.models.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryQuestionRepository implements QuestionRepository {
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
        throw new NotFoundException();
    }

     @Override
     public Question updateQuestion(String userId, String questionId, Question question) {

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
                    throw new AccessDeniedException();
                }
            }
        }
        throw new NotFoundException();
     }

     @Override
     public void deleteQuestion(String userId, String questionId) {

        List<Question> questions = questionCache.stream()
                .filter(q -> q.getId().equals(questionId))
                .collect(Collectors.toList());

        if (questions.size() == 0) {
            throw new NotFoundException();
        }

        if (!questionCache.removeIf(q ->  q.getId().equals(questionId) && q.getAuthor().equals(userId))) {
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
    public Collection<Question> getAllQuestions(String userId, String subject, String page, String order) {

        final int QUESTION_AMOUNT = 10;
        int intPage = Integer.parseInt(page);

        if (order.equals("1")) {
            questionCache.sort(Comparator.comparing(q -> Integer.parseInt(q.getId())));
        }
        else {
            questionCache.sort(Comparator.comparing(q -> -Integer.parseInt(q.getId())));
        }

        List<Question> questions = questionCache;

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

        if (page.equals("0"))
            return questions;

        int lastIndex = (questions.size() - 1) % QUESTION_AMOUNT;
        questions = (questions.size() <= intPage * QUESTION_AMOUNT) ?
                questions.subList((intPage - 1) * QUESTION_AMOUNT, (intPage - 1) * QUESTION_AMOUNT + lastIndex + 1) :
                questions.subList((intPage - 1) * QUESTION_AMOUNT, (intPage - 1) * QUESTION_AMOUNT + QUESTION_AMOUNT);

        return questions;
    }

    @Override
    public Collection<Question> getTestQuestions(String subject, String questionsCount) {
        List<Question> questions = questionCache.stream()
                .filter(i -> i.getSubject().equals(subject))
                .collect(Collectors.toList());

        Random rand = new Random();

        if (Integer.parseInt(questionsCount) > questions.size()) {
            throw new NotFoundException();
        }

        while (questions.size() != Integer.parseInt(questionsCount)) {
            questions.remove(rand.nextInt(questions.size()));
        }

        return questions;
    }
}
