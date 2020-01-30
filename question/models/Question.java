package ftc.shift.sample.models;

public class Question {
    private final String id;
    private final String text;
    private final String correctAnswer;
    private final String author;
    private final String subject;

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
