package shift.question.services;

import shift.question.models.Question;
import shift.question.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question provideQuestion(String questionId) {
        return questionRepository.fetchQuestion(questionId);
    }

    public Question updateQuestion(String userId, String questionId, Question question) {
        return questionRepository.updateQuestion(userId, questionId, question);
    }

    public void deleteQuestion(String userId, String questionId) {
        questionRepository.deleteQuestion(userId, questionId);
    }

    public Question createQuestion(String userId, Question question) {
        return questionRepository.createQuestion(userId, question);
    }

    public Collection<Question> provideQuestions() {
        return questionRepository.getAllQuestions();
    }
}
