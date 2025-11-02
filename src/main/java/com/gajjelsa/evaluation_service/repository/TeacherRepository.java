package com.gajjelsa.evaluation_service.repository;

import com.gajjelsa.evaluation_service.model.Teacher;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherRepository extends MongoRepository<Teacher, String> {
    Optional<Teacher> findByUserId(String userId);
    Optional<Teacher> findByEmployeeId(String employeeId);
}
