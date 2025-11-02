package com.gajjelsa.evaluation_service.dto;

import lombok.Data;

@Data
public class TeacherRegistrationRequest {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String employeeId;
    private String department;
    private String specialization;
    private String qualification;
}
