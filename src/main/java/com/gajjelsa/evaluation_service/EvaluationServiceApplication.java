package com.gajjelsa.evaluation_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EvaluationServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(EvaluationServiceApplication.class, args);
	}
}
