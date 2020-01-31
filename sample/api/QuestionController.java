package ftc.shift.sample.api;

import ftc.shift.sample.models.Question;
import ftc.shift.sample.models.QuestionList;
import ftc.shift.sample.services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v001/questions")
public class QuestionController {
    private QuestionService service;

    @Autowired
    public QuestionController(QuestionService service) {
        this.service = service;
    }
    /**
     * Добавление нового вопроса
     *
     * @param userId - Идентификатор пользователя
     * @param question - Данные для новоого вопроса (идентификатор вопроса, текст вопроса, ответ, автор, предмет)
     * @return Сохранённый вопрос
     */
    @PostMapping
    public ResponseEntity<Question> createQuestion(
            @RequestHeader("userId") String userId,
            @RequestBody Question question) {
        Question result = service.createQuestion(userId, question);
        return ResponseEntity.ok(result);
    }
    /**
     * Получение нового вопроса с указанным идентификатором
     *
     * @param questionId - Идентификатор вопроса
     *
     */
    @GetMapping("/{questionId}")
    public ResponseEntity<Question> readQuestion(
            @PathVariable String questionId) {
        Question question = service.provideQuestion(questionId);
        return ResponseEntity.ok(question);
    }
    /**
     * Обновление существующего вопроса
     *
     * @param userId - Идентификатор АВТОРА вопроса
     * @param questionId - Идентификатор вопроса
     * @param question - Новые данные для вопроса (текст вопроса, ответ, предмет)
     */
    @PatchMapping("/{questionId}")
    public ResponseEntity<Question> updateQuestion(
             @RequestHeader("userId") String userId,
             @PathVariable String questionId,
             @RequestBody Question question) {
         Question updatedQuestion = service.updateQuestion(userId, questionId, question);
         return ResponseEntity.ok(updatedQuestion);
    }
    /**
     * Удаление существующего вопроса
     *
     * @param userId - Идентификатор пользователя
     * @param questionId - Идентификатор книги, которую необходимо удалить
     */
    @DeleteMapping("/{questionId}")
    public ResponseEntity<?> deleteQuestion(
             @RequestHeader("userId") String userId,
             @PathVariable String questionId) {
         service.deleteQuestion(userId, questionId);
         return ResponseEntity.ok().build();
    }
    /**
     * Получение вопросов
     *
     * Параметры фильтрации списка вопросов:
     * @param userId - Идентификатор пользователя
     * @param subject - Предмет вопросов
     * @param page - Страница в списке вопросов
     * @param order - Порядок выдачи вопросов (1 - по возрастанию, 2 - по убыванию)
     */
    @GetMapping
    public ResponseEntity<QuestionList> listQuestions(
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "subject", required = false) String subject,
            @RequestParam(value = "page", required = false, defaultValue = "1") String page,
            @RequestParam(value = "order", required = false, defaultValue = "1") String order
    ) {
         QuestionList questions = service.provideQuestions(userId, subject, page, order);
         return ResponseEntity.ok(questions);
    }

    /**
     * Получение случайных вопросов
     *
     * @param subject - Предмет вопросов
     * @param questionsCount - Количество вопросов (до 10)
     */
    @GetMapping("/test")
    public ResponseEntity<QuestionList> testQuestions(
            @RequestParam(value = "subject") String subject,
            @RequestParam(value = "questionsCount") String questionsCount) {
        QuestionList questions = service.provideTestQuestions(subject, questionsCount);
        return ResponseEntity.ok(questions);
    }
}