package ftc.shift.sample.repositories;

import ftc.shift.sample.models.Question;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class QuestionExtractor implements ResultSetExtractor<List<Question>> {
    @Override
    public List<Question> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Question> questions = new ArrayList<>();

        while (rs.next()) {
            Question question = new Question(rs.getString("QUESTION_ID"), rs.getString("TEXT"), rs.getString("CORRECT_ANSWER"), rs.getString("AUTHOR"), rs.getString("SUBJECT"));

            questions.add(question);
        }

        return questions;
    }
}
