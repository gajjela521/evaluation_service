package com.gajjelsa.evaluation_service.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "users")
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String password; // Hashed password
    private String name;
    private String phone;
    private UserRole role;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isActive;

    // Admin approval fields
    private boolean isApproved;
    private LocalDateTime approvalDate;
    private String approvedBy; // Admin user ID who approved
    private ApprovalStatus approvalStatus;

    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
        this.isApproved = false;
        this.approvalStatus = ApprovalStatus.PENDING;
    }

    public enum UserRole {
        STUDENT,
        TEACHER,
        PRINCIPAL,
        IT_ADMIN,
        ADMIN
    }

    public enum ApprovalStatus {
        PENDING,
        APPROVED,
        REJECTED,
        AUTO_APPROVED
    }

    // Check if user can access system (approved or auto-approved after 24hrs)
    public boolean canAccessSystem() {
        if (role == UserRole.ADMIN) {
            return true; // Admins don't need approval
        }

        // Auto-approve after 24 hours
        if (!isApproved && approvalStatus == ApprovalStatus.PENDING) {
            LocalDateTime autoApprovalTime = createdAt.plusHours(24);
            if (LocalDateTime.now().isAfter(autoApprovalTime)) {
                return true; // Will be auto-approved
            }
        }

        return isApproved;
    }
}
