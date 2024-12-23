package com.gajjelsa.evaluation_service.model;

import java.util.List;

public class ScoreSheet {

    private String testeeId;
    private List<SubjectScore> subjects;

    // Constructor
    public ScoreSheet(String testeeId, List<SubjectScore> subjects) {
        this.testeeId = testeeId;
        this.subjects = subjects;
    }

    // Getters and Setters
    public String getTesteeId() {
        return testeeId;
    }

    public void setTesteeId(String testeeId) {
        this.testeeId = testeeId;
    }

    public List<SubjectScore> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<SubjectScore> subjects) {
        this.subjects = subjects;
    }

    // Inner class to represent each subject's score
    public static class SubjectScore {
        private String subject;
        private int totalQuestions;
        private int correct;
        private int incorrect;
        private double score;

        // Constructor
        public SubjectScore(String subject, int totalQuestions, int correct, int incorrect) {
            this.subject = subject;
            this.totalQuestions = totalQuestions;
            this.correct = correct;
            this.incorrect = incorrect;
        }

        // Getters and Setters
        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public int getTotalQuestions() {
            return totalQuestions;
        }

        public void setTotalQuestions(int totalQuestions) {
            this.totalQuestions = totalQuestions;
        }

        public int getCorrect() {
            return correct;
        }

        public void setCorrect(int correct) {
            this.correct = correct;
        }

        public int getIncorrect() {
            return incorrect;
        }

        public void setIncorrect(int incorrect) {
            this.incorrect = incorrect;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }
    }
}
