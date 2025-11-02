package com.gajjelsa.evaluation_service.service;

import com.gajjelsa.evaluation_service.dto.AuthResponse;
import com.gajjelsa.evaluation_service.dto.LoginRequest;
import com.gajjelsa.evaluation_service.model.User;
import com.gajjelsa.evaluation_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Generate simple token (can be replaced with JWT later)
        String token = generateToken(user);

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setRole(user.getRole());
        response.setApproved(user.isApproved());
        response.setApprovalStatus(user.getApprovalStatus());

        if (!user.canAccessSystem()) {
            response.setMessage("Your registration is pending admin approval. You will be automatically approved after 24 hours if admin doesn't respond. For immediate access, contact admin at: gajjelasuryateja2021@gmail.com");
        } else {
            response.setMessage("Login successful");
        }

        return response;
    }

    private String generateToken(User user) {
        // Simple token generation - can be replaced with JWT
        String tokenData = user.getId() + ":" + user.getEmail() + ":" + System.currentTimeMillis();
        return Base64.getEncoder().encodeToString(tokenData.getBytes());
    }
}
