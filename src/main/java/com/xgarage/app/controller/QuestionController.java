package com.xgarage.app.controller;

import com.xgarage.app.model.Question;
import com.xgarage.app.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/core/api/v1/questions")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllQuestions() {
        try{
            List<Question> questions = questionService.findAllQuestions();
            if(questions == null) {
                return new ResponseEntity<>("Questions Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(questions);
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Questions");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable("id") Long id) {
        try{
            if(questionService.deleteQuestionById(id)) {
                return ResponseEntity.ok().body("Success");
            }
            return new ResponseEntity<>("Question Not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Questions");
        }
    }
}
