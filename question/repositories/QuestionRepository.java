package shift.question.repositories;

import shift.question.models.Question;

import java.util.Collection;


public interface QuestionRepository {

  Question fetchQuestion(String userId, String questionId);

  Question updateQuestion(String userId, String questionId, Question question);

  void deleteQuestion(String userId, String questionId);

  Question createQuestion(String userId, Question question);

  Collection<Question> getAllQuestions(String userId);
}