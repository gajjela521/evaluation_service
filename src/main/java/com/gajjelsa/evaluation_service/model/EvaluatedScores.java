package com.gajjelsa.evaluation_service.model;

import java.util.Map;

public class EvaluatedScores {

    private String testeeId;
    private Map<String, Double> scores;

    // Constructor
    public EvaluatedScores(String testeeId, Map<String, Double> scores) {
        this.testeeId = testeeId;
        this.scores = scores;
    }

    // Getters and Setters
    public String getTesteeId() {
        return testeeId;
    }

    public void setTesteeId(String testeeId) {
        this.testeeId = testeeId;
    }

    public Map<String, Double> getScores() {
        return scores;
    }

    public void setScores(Map<String, Double> scores) {
        this.scores = scores;
    }
}
