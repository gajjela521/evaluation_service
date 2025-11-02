package com.gajjelsa.evaluation_service.repository;

import com.gajjelsa.evaluation_service.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {
    Student findByEmail(String email);
    Student findByUserId(String userId);
}
