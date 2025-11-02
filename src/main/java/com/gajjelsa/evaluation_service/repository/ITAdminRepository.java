package com.gajjelsa.evaluation_service.repository;

import com.gajjelsa.evaluation_service.model.ITAdmin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ITAdminRepository extends MongoRepository<ITAdmin, String> {
    Optional<ITAdmin> findByUserId(String userId);
    Optional<ITAdmin> findByEmployeeId(String employeeId);
}
