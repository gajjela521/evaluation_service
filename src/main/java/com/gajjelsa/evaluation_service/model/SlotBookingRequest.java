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
    private String studentId;
    private Long slotId;
    private String subjectId;

    public SlotBookingRequest(String studentId, Long slotId, String subjectId) {
        this.studentId = studentId;
        this.slotId = slotId;
        this.subjectId = subjectId;
    }
    @Override
    public String toString() {
        return "SlotBookingRequest{" +
                "studentId='" + studentId + '\'' +
                ", slotId=" + slotId +
                ", subjectId='" + subjectId + '\'' +
                '}';
    }
}
