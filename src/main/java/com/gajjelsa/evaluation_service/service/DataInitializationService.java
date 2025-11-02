package com.gajjelsa.evaluation_service.service;

import com.gajjelsa.evaluation_service.model.User;
import com.gajjelsa.evaluation_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DataInitializationService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializationService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createDefaultAdminUser();
    }

    private void createDefaultAdminUser() {
        String adminEmail = "admin";

        Optional<User> existingAdmin = userRepository.findByEmail(adminEmail);

        if (existingAdmin.isPresent()) {
            logger.info("Default admin user already exists");
            return;
        }

        try {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("gajjelasuryateja"));
            admin.setName("System Administrator");
            admin.setPhone("N/A");
            admin.setRole(User.UserRole.ADMIN);
            admin.setActive(true);
            admin.setApproved(true); // Admins don't need approval
            admin.setApprovalStatus(User.ApprovalStatus.APPROVED);
            admin.setApprovalDate(LocalDateTime.now());
            admin.setApprovedBy("SYSTEM");

            userRepository.save(admin);

            logger.info("Default admin user created successfully");
            logger.info("Login credentials - Email: admin, Password: gajjelasuryateja");
        } catch (Exception e) {
            logger.error("Error creating default admin user: {}", e.getMessage(), e);
        }
    }
}
