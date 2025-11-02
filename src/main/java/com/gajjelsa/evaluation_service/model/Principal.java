package com.gajjelsa.evaluation_service.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "principals")
public class Principal {
    @Id
    private String id;

    @Indexed(unique = true)
    private String userId; // Reference to User

    @Indexed(unique = true)
    private String employeeId;

    private String department;
    private Integer yearsOfExperience;
}
