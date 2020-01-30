package ftc.shift.sample.repositories;

import ftc.shift.sample.models.Question;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class QuestionExtractor implements ResultSetExtractor<List<Question>> {
    @Override
    public List<Question> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Question> questions = new ArrayList<>();

        while (rs.next()) {
            String questionId = rs.getString("QUESTION_ID");

            Question Question;
            if (questions.containsKey(questionId)) {
                Question = questions.get(questionId);
            } else {
                Question = new Question();

                Question.setId(rs.getString("QUESTION_ID"));
                Question.setText(rs.getString("TEXT"));
                Question.setCorrectAnswer(rs.getString("CORRECT_ANSWER"));
                Question.setAuthor(rs.getString("AUTHOR"));
                Question.setSubject(rs.getString("SUBJECT"));

                questions.add(Question);
            }
        }

        return questions;
    }
}
