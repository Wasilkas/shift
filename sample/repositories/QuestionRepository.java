package ftc.shift.sample.repositories;

import ftc.shift.sample.models.Question;
import ftc.shift.sample.models.QuestionList;

import java.util.Collection;


public interface QuestionRepository {

  Question fetchQuestion(String questionId);

  Question updateQuestion(String userId, String questionId, Question question);

  void deleteQuestion(String userId, String questionId);

  Question createQuestion(String userId, Question question);

  QuestionList getAllQuestions(String userId, String subject, String page, String order);

  QuestionList getTestQuestions(String subject, String questionsCount);
}
