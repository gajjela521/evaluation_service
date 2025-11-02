package com.gajjelsa.evaluation_service.repository;

import com.gajjelsa.evaluation_service.model.Principal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrincipalRepository extends MongoRepository<Principal, String> {
    Optional<Principal> findByUserId(String userId);
    Optional<Principal> findByEmployeeId(String employeeId);
}
