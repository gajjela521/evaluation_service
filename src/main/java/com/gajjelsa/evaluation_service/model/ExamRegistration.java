package com.gajjelsa.evaluation_service.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "examRegistrations")
public class ExamRegistration {
    @Id
    private String id;
    private String studentId;
    private String slotId;
    private String subjectId;
    private LocalDateTime registrationTime;
    private LocalDate examDate;
    private RegistrationStatus status;

    public ExamRegistration(String id, String studentId, String slotId, String subjectId, LocalDateTime registrationTime, LocalDate examDate, RegistrationStatus status) {
        this.id = id;
        this.studentId = studentId;
        this.slotId = slotId;
        this.subjectId = subjectId;
        this.registrationTime = registrationTime;
        this.examDate = examDate;
        this.status = status;
    }

    @Override
    public String toString() {
        return "ExamRegistration{" +
                "id='" + id + '\'' +
                ", studentId='" + studentId + '\'' +
                ", slotId='" + slotId + '\'' +
                ", subjectId='" + subjectId + '\'' +
                ", registrationTime=" + registrationTime +
                ", examDate=" + examDate +
                ", status=" + status +
                '}';
    }
}