package question.services;

import question.models.Question;
import question.repositories.QuestionRepository;
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

    public Question provideQuestion(String userId, String questionId) {
        return questionRepository.fetchQuestion(userId, questionId);
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

    public Collection<Question> provideQuestions(String userId) {
        return questionRepository.getAllQuestions(userId);
    }
}
