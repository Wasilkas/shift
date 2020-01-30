package ftc.shift.sample.models;

public class Question {
    private String id;
    private String text;
    private String correctAnswer;
    private String author;
    private String subject;

    public Question(String id, String text, String correctAnswer, String author, String subject) {
        this.id = id;
        this.text = text;
        this.correctAnswer = correctAnswer;
        this.author = author;
        this.subject = subject;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

}
