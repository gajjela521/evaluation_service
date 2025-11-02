package com.gajjelsa.evaluation_service.service;

import com.gajjelsa.evaluation_service.model.User;
import com.gajjelsa.evaluation_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AutoApprovalScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AutoApprovalScheduler.class);

    @Autowired
    private UserRepository userRepository;

    // Run every hour
    @Scheduled(cron = "0 0 * * * *")
    public void autoApprovePendingUsers() {
        logger.info("Running auto-approval job...");

        try {
            List<User> pendingUsers = userRepository.findByApprovalStatus(User.ApprovalStatus.PENDING);
            LocalDateTime now = LocalDateTime.now();
            int autoApprovedCount = 0;

            for (User user : pendingUsers) {
                // Skip admin users (they don't need approval)
                if (user.getRole() == User.UserRole.ADMIN) {
                    continue;
                }

                // Check if 24 hours have passed since user registration
                LocalDateTime autoApprovalTime = user.getCreatedAt().plusHours(24);

                if (now.isAfter(autoApprovalTime)) {
                    user.setApproved(true);
                    user.setApprovalStatus(User.ApprovalStatus.AUTO_APPROVED);
                    user.setApprovalDate(now);
                    user.setApprovedBy("SYSTEM");

                    userRepository.save(user);
                    autoApprovedCount++;

                    logger.info("Auto-approved user: {} ({})", user.getEmail(), user.getRole());
                }
            }

            logger.info("Auto-approval job completed. {} users auto-approved.", autoApprovedCount);

        } catch (Exception e) {
            logger.error("Error during auto-approval job: {}", e.getMessage(), e);
        }
    }
}
