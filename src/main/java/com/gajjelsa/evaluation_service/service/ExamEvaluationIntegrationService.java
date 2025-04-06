package com.gajjelsa.evaluation_service.service;

import com.gajjelsa.evaluation_service.model.ExamRegistration;
import com.gajjelsa.evaluation_service.model.RegistrationStatus;
import com.gajjelsa.evaluation_service.model.ScoreSheet;
import com.gajjelsa.evaluation_service.model.Subject;
import com.gajjelsa.evaluation_service.repository.EvaluationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExamEvaluationIntegrationService {

    private final EvaluationService evaluationService;
    private final EvaluationRepository.RegistrationRepository registrationRepository;
    private final EvaluationRepository.SubjectRepository subjectRepository;

    @Autowired
    public ExamEvaluationIntegrationService(
            ExamSchedulingService schedulingService,
            EvaluationService evaluationService, EvaluationRepository.RegistrationRepository registrationRepository, EvaluationRepository.SubjectRepository subjectRepository) {
        this.evaluationService = evaluationService;
        this.registrationRepository = registrationRepository;
        this.subjectRepository = subjectRepository;
    }

    /**
     * Creates evaluation sheets for all booked exams on a specific date
     */
    @Scheduled(cron = "0 0 8 * * ?")  // Run daily at 8 AM
    public void createEvaluationSheetsForUpcomingExams() {
        LocalDate today = LocalDate.now();
        List<ExamRegistration> todayRegistrations =
                registrationRepository.findByExamDateAndStatus(today, RegistrationStatus.CONFIRMED);

        List<ScoreSheet> scoreSheets = new ArrayList<>();
        for (ExamRegistration reg : todayRegistrations) {
            ScoreSheet sheet = new ScoreSheet();
            sheet.setTesteeId(reg.getStudentId());

            Subject subject = subjectRepository.findById(reg.getSubjectId()).orElse(null);
            if (subject != null) {
                ScoreSheet.SubjectScore subjectScore = new ScoreSheet.SubjectScore();
                subjectScore.setSubject(subject.getName());
                subjectScore.setCorrect(0);
                subjectScore.setIncorrect(0);
                List<ScoreSheet.SubjectScore> subjects = new ArrayList<>();
                subjects.add(subjectScore);
                sheet.setSubjects(subjects);
            }

            scoreSheets.add(sheet);
        }
        
        if (!scoreSheets.isEmpty()) {
            evaluationService.submitScoreSheets(scoreSheets);
        }
    }
}
