package ftc.shift.sample.models;

import java.beans.ConstructorProperties;

public class Question {
    /**
     * Уникальный идентификатор вопроса
     */
    private final String id;
    /**
     * Текст вопроса
     */
    private final String text;
    /**
     * Ответ на вопрос
     */
    private final String correctAnswer;
    /**
     * Автор вопроса
     */
    private final String author;

    /**
     * Предмет вопроса
     */
    private final String subject;

    @ConstructorProperties({"id", "text", "correctAnswer", "author", "subject"})
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
