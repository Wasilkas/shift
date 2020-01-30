package ftc.shift.sample.models;


import java.util.List;

public class QuestionList {
    private List<Question> questions;
    private int pagesAmount;

    public QuestionList() {}

    public QuestionList(List<Question> questions, int pagesAmount) {
        this.questions = questions;
        this.pagesAmount = pagesAmount;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public int getPagesAmount() {
        return pagesAmount;
    }

}
