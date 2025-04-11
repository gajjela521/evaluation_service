package com.gajjelsa.evaluation_service.repository;

import com.gajjelsa.evaluation_service.model.ExamSlot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExamSlotRepository extends MongoRepository<ExamSlot, String> {
    List<ExamSlot> findByExamDate(LocalDate examDate);
    List<ExamSlot> findBySubjectIdAndExamDate(String subjectId, LocalDate examDate);
}
