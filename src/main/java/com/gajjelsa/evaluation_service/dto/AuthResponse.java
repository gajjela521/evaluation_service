package com.gajjelsa.evaluation_service.dto;

import com.gajjelsa.evaluation_service.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String userId;
    private String email;
    private String name;
    private User.UserRole role;
    private boolean isApproved;
    private User.ApprovalStatus approvalStatus;
    private String message;
}
