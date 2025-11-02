package com.gajjelsa.evaluation_service.service;

import com.gajjelsa.evaluation_service.model.User;
import com.gajjelsa.evaluation_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getPendingApprovals() {
        return userRepository.findByApprovalStatus(User.ApprovalStatus.PENDING);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User approveUser(String userId, String adminId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        if (user.getRole() == User.UserRole.ADMIN) {
            throw new RuntimeException("Admin users don't need approval");
        }

        user.setApproved(true);
        user.setApprovalStatus(User.ApprovalStatus.APPROVED);
        user.setApprovalDate(LocalDateTime.now());
        user.setApprovedBy(adminId);

        return userRepository.save(user);
    }

    public User rejectUser(String userId, String adminId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        if (user.getRole() == User.UserRole.ADMIN) {
            throw new RuntimeException("Cannot reject admin users");
        }

        user.setApproved(false);
        user.setApprovalStatus(User.ApprovalStatus.REJECTED);
        user.setApprovalDate(LocalDateTime.now());
        user.setApprovedBy(adminId);

        return userRepository.save(user);
    }

    public List<User> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role);
    }

    public List<User> getUsersByRoleAndStatus(User.UserRole role, User.ApprovalStatus status) {
        return userRepository.findByRoleAndApprovalStatus(role, status);
    }
}
