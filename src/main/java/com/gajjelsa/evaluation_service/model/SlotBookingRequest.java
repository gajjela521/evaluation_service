package com.gajjelsa.evaluation_service.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "slotBookings")
public class SlotBookingRequest {
    @Id
    private String id;          // Added proper ID for the request
    private String studentId;   // Already correct
    private String slotId;      // Fixed: Changed from Long to String
    private String subjectId;   // Already correct

    // Default constructor
    public SlotBookingRequest() {}

    // Constructor without ID (for creating new requests)
    public SlotBookingRequest(String studentId, String slotId, String subjectId) {
        this.studentId = studentId;
        this.slotId = slotId;
        this.subjectId = subjectId;
    }

    // Full constructor
    public SlotBookingRequest(String id, String studentId, String slotId, String subjectId) {
        this.id = id;
        this.studentId = studentId;
        this.slotId = slotId;
        this.subjectId = subjectId;
    }

    @Override
    public String toString() {
        return "SlotBookingRequest{" +
                "id='" + id + '\'' +
                ", studentId='" + studentId + '\'' +
                ", slotId='" + slotId + '\'' +
                ", subjectId='" + subjectId + '\'' +
                '}';
    }
}