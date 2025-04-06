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
    private Long id;
    private LocalDate examDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String subjectId;
    private int capacity;
    private String location;

    public ExamSlot(Long id, LocalDate examDate, LocalTime startTime, LocalTime endTime, String subjectId, int capacity, String location) {
        this.id = id;
        this.examDate = examDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subjectId = subjectId;
        this.capacity = capacity;
        this.location = location;
    }

    public ExamSlot(LocalDate examDate, LocalTime of, LocalTime of1, String subjectId) {
    }

    @Override
    public String toString() {
        return "ExamSlot{" +
                "id=" + id +
                ", examDate=" + examDate +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", subjectId='" + subjectId + '\'' +
                ", capacity=" + capacity +
                ", location='" + location + '\'' +
                '}';
    }
}
