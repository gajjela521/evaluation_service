package com.gajjelsa.evaluation_service.repository;

import com.gajjelsa.evaluation_service.model.ScoreSheet;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScoreSheetRepository extends MongoRepository<ScoreSheet, String> {
}
