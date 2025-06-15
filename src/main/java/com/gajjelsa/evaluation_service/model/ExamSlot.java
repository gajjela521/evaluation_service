package com.gajjelsa.evaluation_service.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Document(collection = "examSlots")
public class ExamSlot {
    @Id
    private String id;  // Fixed: Changed from Long to String
    private LocalDate examDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String subjectId;
    private int capacity;
    private String location;

    // Default constructor for MongoDB
    public ExamSlot() {}

    // Full constructor
    public ExamSlot(String id, LocalDate examDate, LocalTime startTime, LocalTime endTime,
                    String subjectId, int capacity, String location) {
        this.id = id;
        this.examDate = examDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subjectId = subjectId;
        this.capacity = capacity;
        this.location = location;
    }

    // Constructor without ID (for creating new slots)
    public ExamSlot(LocalDate examDate, LocalTime startTime, LocalTime endTime,
                    String subjectId, int capacity, String location) {
        this.examDate = examDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subjectId = subjectId;
        this.capacity = capacity;
        this.location = location;
    }

    @Override
    public String toString() {
        return "ExamSlot{" +
                "id='" + id + '\'' +
                ", examDate=" + examDate +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", subjectId='" + subjectId + '\'' +
                ", capacity=" + capacity +
                ", location='" + location + '\'' +
                '}';
    }
}