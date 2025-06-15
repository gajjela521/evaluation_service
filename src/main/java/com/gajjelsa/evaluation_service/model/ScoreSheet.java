package com.gajjelsa.evaluation_service.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "scoreSheets")
@Data
public class ScoreSheet {
    @Id
    private String testeeId;
    private List<SubjectScore> subjects;

    public ScoreSheet(String testeeId, List<SubjectScore> subjects) {
        this.testeeId = testeeId;
        this.subjects = subjects;
    }

    public ScoreSheet() {

    }

    @Override
    public String toString() {
        return "ScoreSheet{" +
                "testeeId='" + testeeId + '\'' +
                ", subjects=" + subjects +
                '}';
    }

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
    @Data
    @NoArgsConstructor  // Fixed: Removed @Document annotation - this is embedded, not a separate collection
    public static class SubjectScore {
        private String subject;
        private int totalQuestions;
        private int correct;
        private int incorrect;
        private double score;

        public SubjectScore(String subject, int totalQuestions, int correct, int incorrect) {
            this.subject = subject;
            this.totalQuestions = totalQuestions;
            this.correct = correct;
            this.incorrect = incorrect;
            // Score will be calculated by service
        }

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

        @Override
        public String toString() {
            return "SubjectScore{" +
                    "subject='" + subject + '\'' +
                    ", totalQuestions=" + totalQuestions +
                    ", correct=" + correct +
                    ", incorrect=" + incorrect +
                    ", score=" + score +
                    '}';
        }
    }
}