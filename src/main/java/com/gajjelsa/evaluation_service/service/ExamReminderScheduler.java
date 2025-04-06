package com.gajjelsa.evaluation_service.service;

import com.gajjelsa.evaluation_service.model.ExamRegistration;
import com.gajjelsa.evaluation_service.model.RegistrationStatus;
import com.gajjelsa.evaluation_service.repository.EvaluationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExamReminderScheduler {

    private final EvaluationRepository.RegistrationRepository registrationRepository;
    private final NotificationService notificationService;

    @Autowired
    public ExamReminderScheduler(EvaluationRepository.RegistrationRepository registrationRepository,
                                 NotificationService notificationService) {
        this.registrationRepository = registrationRepository;
        this.notificationService = notificationService;
    }

    /**
     * Send reminder emails 24 hours before exams
     */
    @Scheduled(cron = "0 0 10 * * ?") // Run daily at 10 AM
    public void sendExamReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<ExamRegistration> tomorrowExams =
                registrationRepository.findByExamDateAndStatus(tomorrow, RegistrationStatus.CONFIRMED);

        for (ExamRegistration registration : tomorrowExams) {
            notificationService.sendExamReminder(registration);
        }
    }
}
