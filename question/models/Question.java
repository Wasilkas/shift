package question.models;

public class Question {
  private String id;
  private String text;
  private String correctAnswer;

  public Question() {}

  public Question(String id, String text, String correctAnswer) {
    this.id = id;
    this.text = text;
    this.correctAnswer = correctAnswer;
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
}
