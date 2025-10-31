package com.gajjelsa.evaluation_service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

@Configuration
public class ApplicationConfig {
    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.mail.password}")
    private String mailPassword;

    @Autowired
    private Environment environment;

    @PostConstruct
    public void validateConfiguration() {
        // Skip validation for local profile (credentials are in application-local.yml)
        if (Arrays.asList(environment.getActiveProfiles()).contains("local")) {
            return;
        }

        List<String> missingVars = new ArrayList<>();

        if (System.getenv("MONGODB_USERNAME") == null) {
            missingVars.add("MONGODB_USERNAME");
        }
        if (System.getenv("MONGODB_PASSWORD") == null) {
            missingVars.add("MONGODB_PASSWORD");
        }
        if (System.getenv("MAIL_PASSWORD") == null) {
            missingVars.add("MAIL_PASSWORD");
        }

        if (!missingVars.isEmpty()) {
            throw new IllegalStateException(
                    "Missing required environment variables: " + String.join(", ", missingVars)
            );
        }
    }
}
