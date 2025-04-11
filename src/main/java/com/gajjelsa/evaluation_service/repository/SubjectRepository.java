package com.gajjelsa.evaluation_service.repository;

import com.gajjelsa.evaluation_service.model.Subject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubjectRepository extends MongoRepository<Subject, String> {
    List<Subject> findByClassId(String classId);
}
