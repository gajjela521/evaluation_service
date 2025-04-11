
package com.gajjelsa.evaluation_service.repository;

import com.gajjelsa.evaluation_service.model.ExamRegistration;
import com.gajjelsa.evaluation_service.model.RegistrationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RegistrationRepository extends MongoRepository<ExamRegistration, String> {
    List<ExamRegistration> findByStudentId(String studentId);
    boolean existsByStudentIdAndSlotIdIn(String studentId, List<Long> slotIds);
    long countBySlotId(String slotId);
    List<ExamRegistration> findBySlotIdInAndStatus(List<String> slotIds, RegistrationStatus status);
    boolean existsByStudentIdAndExamDate(String studentId, LocalDate examDate);
    List<ExamRegistration> findByExamDateAndStatus(LocalDate tomorrow, RegistrationStatus registrationStatus);
}
