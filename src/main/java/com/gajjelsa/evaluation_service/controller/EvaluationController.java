package com.gajjelsa.evaluation_service.controller;

import com.gajjelsa.evaluation_service.model.EvaluatedScores;
import com.gajjelsa.evaluation_service.model.ScoreSheet;
import com.gajjelsa.evaluation_service.service.EvaluationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/evaluation")
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    // Feature #1: Submit Scoresheets
    @PostMapping("/sheets")
    public ResponseEntity<String> submitScoreSheets(@RequestBody @Valid List<ScoreSheet> scoreSheets) {
        try {
            evaluationService.submitScoreSheets(scoreSheets);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Score sheet(s) submitted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: " + e.getMessage());
        }
    }

    // Feature #2: Retrieve Evaluated Scores
    @GetMapping("/scores")
    public ResponseEntity<List<EvaluatedScores>> getEvaluatedScores(@RequestParam Map<String, String> queryParams) {
        try {
            List<EvaluatedScores> evaluatedScores = evaluationService.retrieveEvaluatedScores(queryParams);
            return ResponseEntity.ok(evaluatedScores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
