package com.gajjelsa.evaluation_service.model;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@Document(collection = "subjects")
public class Subject {
    @Id
    private String id;
    private String name;
    private String classId;  // References the class/grade level
    private int durationMinutes;

    public Subject(String id, String name, String classId, int durationMinutes) {
        this.id = id;
        this.name = name;
        this.classId = classId;
        this.durationMinutes = durationMinutes;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", classId='" + classId + '\'' +
                ", durationMinutes=" + durationMinutes +
                '}';
    }
}