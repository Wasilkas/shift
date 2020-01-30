package ftc.shift.sample.models;

public class Question {
    private String id;
    private String text;
    private String correctAnswer;
    private String author;
    private String subject;

    public Question() {};

    public Question(String id, String text, String correctAnswer, String author, String subject) {
        this.id = id;
        this.text = text;
        this.correctAnswer = correctAnswer;
        this.author = author;
        this.subject = subject;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getAuthor() {
        return author;
    }

    public String getSubject() {
        return subject;
    }
}