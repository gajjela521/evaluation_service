package com.gajjelsa.evaluation_service.controller;

import com.gajjelsa.evaluation_service.dto.AuthResponse;
import com.gajjelsa.evaluation_service.dto.ITAdminRegistrationRequest;
import com.gajjelsa.evaluation_service.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/it-admins")
@CrossOrigin(origins = "*")
public class ITAdminRegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/register")
    public ResponseEntity<?> registerITAdmin(@RequestBody @Valid ITAdminRegistrationRequest request) {
        try {
            AuthResponse response = registrationService.registerITAdmin(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}
