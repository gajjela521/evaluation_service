package com.gajjelsa.evaluation_service.repository;

import com.gajjelsa.evaluation_service.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    List<User> findByApprovalStatus(User.ApprovalStatus status);
    List<User> findByRole(User.UserRole role);
    List<User> findByRoleAndApprovalStatus(User.UserRole role, User.ApprovalStatus status);
}
