package com.gajjelsa.evaluation_service.controller;

import com.gajjelsa.evaluation_service.dto.AuthResponse;
import com.gajjelsa.evaluation_service.dto.PrincipalRegistrationRequest;
import com.gajjelsa.evaluation_service.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/principals")
@CrossOrigin(origins = "*")
public class PrincipalRegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/register")
    public ResponseEntity<?> registerPrincipal(@RequestBody @Valid PrincipalRegistrationRequest request) {
        try {
            AuthResponse response = registrationService.registerPrincipal(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}
