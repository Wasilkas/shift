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

    @PostMapping
    public ResponseEntity<Question> createQuestion(
            @RequestHeader("userId") String userId,
            @RequestBody Question question) {
        Question result = service.createQuestion(userId, question);
        return ResponseEntity.ok(result);
    }

     @GetMapping("/{questionId}")
     public ResponseEntity<Question> readQuestion(
             @PathVariable String questionId) {
         Question question = service.provideQuestion(questionId);
         return ResponseEntity.ok(question);
     }

     @PatchMapping("/{questionId}")
     public ResponseEntity<Question> updateQuestion(
             @RequestHeader("userId") String userId,
             @PathVariable String questionId,
             @RequestBody Question question) {
         Question updatedQuestion = service.updateQuestion(userId, questionId, question);
         return ResponseEntity.ok(updatedQuestion);
     }

     @DeleteMapping("/{questionId}")
     public ResponseEntity<?> deleteQuestion(
             @RequestHeader("userId") String userId,
             @PathVariable String questionId) {
         service.deleteQuestion(userId, questionId);
         return ResponseEntity.ok().build();
     }

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

    @GetMapping("/test")
    public ResponseEntity<QuestionList> testQuestions(
            @RequestParam(value = "subject", required = true) String subject,
            @RequestParam(value = "questionsCount", required = true) String questionsCount) {
        QuestionList questions = service.provideTestQuestions(subject, questionsCount);
        return ResponseEntity.ok(questions);
    }
}