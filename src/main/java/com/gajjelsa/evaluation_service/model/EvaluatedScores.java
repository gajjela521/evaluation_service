package com.gajjelsa.evaluation_service.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Setter
@Getter
@Document(collection = "evaluatedScores")
public class EvaluatedScores {
    @Id
    private String testeeId;
    private Map<String, Double> scores;

    public EvaluatedScores(String testeeId, Map<String, Double> scores) {
        this.testeeId = testeeId;
        this.scores = scores;
    }
    @Override
    public String toString() {
        return "EvaluatedScores{" +
                "testeeId='" + testeeId + '\'' +
                ", scores=" + scores +
                '}';
    }

}
