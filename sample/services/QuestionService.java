package ftc.shift.sample.services;

import ftc.shift.sample.models.Question;
import ftc.shift.sample.models.QuestionList;
import ftc.shift.sample.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public QuestionList provideQuestions(String userId, String subject, String page, String order) {
        return questionRepository.getAllQuestions(userId, subject, page, order);
    }

    public QuestionList provideTestQuestions(String subject, String questionsCount) {
        return questionRepository.getTestQuestions(subject, questionsCount);
    }
}
