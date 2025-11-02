package com.gajjelsa.evaluation_service.dto;

import lombok.Data;

@Data
public class StudentRegistrationRequest {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String enrollmentNumber;
    private String department;
    private Integer semester;

    // Additional existing Student fields (preserving existing features)
    private String classId;
    private String contactNumber;
    private String address;
    private String dateOfBirth; // Will be parsed to LocalDate
}
