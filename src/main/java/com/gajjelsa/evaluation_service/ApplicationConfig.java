package com.gajjelsa.evaluation_service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ApplicationConfig {
    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.mail.password}")
    private String mailPassword;

    @PostConstruct
    public void validateConfiguration() {
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
