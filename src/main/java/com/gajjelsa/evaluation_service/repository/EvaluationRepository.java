package com.gajjelsa.evaluation_service.repository;

import com.gajjelsa.evaluation_service.model.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;


public class EvaluationRepository {

    @Repository
    public interface StudentRepository extends MongoRepository<Student, String> {
        // Find student by email for notification purposes
        Student findByEmail(String email);
    }

    @Repository
    public interface SubjectRepository extends MongoRepository<Subject, String> {
        // Find subjects by class/grade level
        List<Subject> findByClassId(String classId);
    }

    @Repository
    public interface ExamSlotRepository extends MongoRepository<ExamSlot, String> {
        // Find slots by date
        List<ExamSlot> findByExamDate(LocalDate examDate);

        // Find slots by subject and date
        List<ExamSlot> findBySubjectIdAndExamDate(String subjectId, LocalDate examDate);
    }

    @Repository
    public interface RegistrationRepository extends MongoRepository<ExamRegistration, String> {
        // Find by student ID
        List<ExamRegistration> findByStudentId(String studentId);

        // Check if student has booking on a specific date
        boolean existsByStudentIdAndSlotIdIn(String studentId, List<Long> slotIds);

        // Count registrations for a slot (for capacity checking)
        long countBySlotId(String slotId);

        // Find by slot IDs and status
        List<ExamRegistration> findBySlotIdInAndStatus(List<String> slotIds, RegistrationStatus status);

        boolean existsByStudentIdAndExamDate(String studentId, LocalDate examDate);

        List<ExamRegistration> findByExamDateAndStatus(LocalDate tomorrow, RegistrationStatus registrationStatus);
    }
}
