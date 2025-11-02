package com.gajjelsa.evaluation_service.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@Document(collection = "students")
public class Student {
    @Id
    private String id;
    private String fullName;
    private String email;
    private String classId;  // To track which class/grade the student belongs to
    private String contactNumber;

    // Optional fields you might want
    private String address;
    private LocalDate dateOfBirth;

    // New fields for authentication and registration
    private String userId; // Reference to User for auth
    private String enrollmentNumber;
    private String department;
    private Integer semester;

    public Student(String id, String fullName, String email, String classId, String contactNumber, String address, LocalDate dateOfBirth) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.classId = classId;
        this.contactNumber = contactNumber;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", classId='" + classId + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", address='" + address + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
}