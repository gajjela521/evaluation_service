package com.gajjelsa.evaluation_service.service;

import com.gajjelsa.evaluation_service.model.ScoreSheet;
import com.gajjelsa.evaluation_service.model.EvaluatedScores;
import com.gajjelsa.evaluation_service.repository.ScoreSheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EvaluationService {

    @Autowired
    private ScoreSheetRepository scoreSheetRepository;

    // Feature #1: Submit Score Sheets (Calculates and persists scores)
    public ScoreSheet submitScoreSheets(List<ScoreSheet> scoreSheets) {
        try {
            for (ScoreSheet scoreSheet : scoreSheets) {
                // Process each score sheet, calculate score, and save it
                for (ScoreSheet.SubjectScore subjectScore : scoreSheet.getSubjects()) {
                    subjectScore.setScore((subjectScore.getCorrect() * 1.0) - (subjectScore.getIncorrect() * 0.25)); // Apply scoring rule
                }
                scoreSheetRepository.save(scoreSheet); // Save to database
            }
            return null;  // Return nothing as the operation is successful
        } catch (Exception e) {
            throw new RuntimeException("Error processing score sheet(s): " + e.getMessage());
        }
    }

    // Feature #2: Retrieve Evaluated Scores (Filter by params, calculate total and average)
    public List<EvaluatedScores> retrieveEvaluatedScores(Map<String, String> queryParams) {
        List<ScoreSheet> scoreSheets = scoreSheetRepository.findAll();
        List<EvaluatedScores> evaluatedScores = new ArrayList<>();

        // Process the score sheets and calculate total and average scores
        for (ScoreSheet scoreSheet : scoreSheets) {
            Map<String, Double> scores = new HashMap<>();
            double total = 0;
            for (ScoreSheet.SubjectScore subjectScore : scoreSheet.getSubjects()) {
                scores.put(subjectScore.getSubject(), subjectScore.getScore());
                total += subjectScore.getScore();
            }
            double average = total / scoreSheet.getSubjects().size();
            scores.put("total", total);
            scores.put("average", average);

            EvaluatedScores evaluatedScore = new EvaluatedScores(scoreSheet.getTesteeId(), scores);
            evaluatedScores.add(evaluatedScore);
        }

        evaluatedScores.sort(Comparator.comparingDouble((EvaluatedScores es) -> es.getScores().get("total")).reversed()
                .thenComparing(EvaluatedScores::getTesteeId));

        return evaluatedScores;
    }
}
