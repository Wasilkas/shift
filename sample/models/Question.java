package ftc.shift.sample.models;

public class Question {
    /**
     * Уникальный идентификатор вопроса
     */
    private String id;
    /**
     * Текст вопроса
     */
    private String text;
    /**
     * Ответ на вопрос
     */
    private String correctAnswer;
    /**
     * Автор вопроса
     */
    private String author;

    /**
     * Предмет вопроса
     */
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
