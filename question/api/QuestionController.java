package ftc.shift.sample.api;


import ftc.shift.sample.models.Question;
import ftc.shift.sample.services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class QuestionController {
    private static final String QUEST_PATH = "/api/v001/questions";
    private QuestionService service;

    @Autowired
    public QuestionController(QuestionService service) {
        this.service = service;
    }

    @PostMapping(QUEST_PATH)
    public ResponseEntity<Question> createQuestion(
            @RequestHeader("userId") String userId,
            @RequestBody Question question) {
        Question result = service.createQuestion(userId, question);
        return ResponseEntity.ok(result);
    }

     @GetMapping(QUEST_PATH + "/{questionId}")
     public ResponseEntity<Question> readQuestion(
             @PathVariable String questionId) {
         Question question = service.provideQuestion(questionId);
         return ResponseEntity.ok(question);
     }

     @PatchMapping(QUEST_PATH + "/{questionId}")
     public ResponseEntity<Question> updateQuestion(
             @RequestHeader("userId") String userId,
             @PathVariable String questionId,
             @RequestBody Question question) {
         Question updatedQuestion = service.updateQuestion(userId, questionId, question);
         return ResponseEntity.ok(updatedQuestion);
     }

     @DeleteMapping(QUEST_PATH + "/{questionId}")
     public ResponseEntity<?> deleteQuestion(
             @RequestHeader("userId") String userId,
             @PathVariable String questionId) {
         service.deleteQuestion(userId, questionId);
         return ResponseEntity.ok().build();
     }

    @GetMapping(QUEST_PATH)
    public ResponseEntity<Collection<Question>> listQuestions(
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "subject", required = false) String subject,
            @RequestParam(value = "page", required = false, defaultValue = "1") String page,
            @RequestParam(value = "order", required = false, defaultValue = "1") String order
    ) {
        Collection<Question> questions = service.provideQuestions(userId, subject, page, order);
        return ResponseEntity.ok(questions);
    }
}