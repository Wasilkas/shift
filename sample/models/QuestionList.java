package ftc.shift.sample.models;


import java.util.List;

public class QuestionList {
    private List<Question> questions;
    private int pagesAmount;

    public QuestionList(List<Question> questions, int pagesAmount) {
        this.questions = questions;
        this.pagesAmount = pagesAmount;
    }

    @SuppressWarnings("unused") // Используется Jackson
    public List<Question> getQuestions() {
        return questions;
    }

    @SuppressWarnings("unused") // Используется Jackson
    public int getPagesAmount() {
        return pagesAmount;
    }

}
