package com.gajjelsa.evaluation_service.controller;

import com.gajjelsa.evaluation_service.GlobalExceptionHandler;
import com.gajjelsa.evaluation_service.model.ExamRegistration;
import com.gajjelsa.evaluation_service.model.ExamSlot;
import com.gajjelsa.evaluation_service.model.Subject;
import com.gajjelsa.evaluation_service.model.SlotBookingRequest;
import com.gajjelsa.evaluation_service.service.ExamSchedulingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/exams")
public class ExamSchedulingController {

    private final ExamSchedulingService schedulingService;

    @Autowired
    public ExamSchedulingController(ExamSchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }

    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getAvailableSubjects(
            @RequestParam String classId) {
        List<Subject> subjects = schedulingService.getAvailableSubjectsByClass(classId);
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/slots")
    public ResponseEntity<List<ExamSlot>> getAvailableSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate examDate,
            @RequestParam String subjectId) {
        List<ExamSlot> slots = schedulingService.getAvailableSlots(examDate, subjectId);
        return ResponseEntity.ok(slots);
    }

    @PostMapping("/book")
    public ResponseEntity<ExamRegistration> bookExamSlot(@Valid @RequestBody SlotBookingRequest request) {
        try {
            // Fixed: slotId is now String type
            ExamRegistration registration = schedulingService.bookExamSlot(
                    request.getStudentId(), request.getSlotId(), request.getSubjectId());
            return ResponseEntity.ok(registration);
        } catch (GlobalExceptionHandler.SlotNotFoundException | GlobalExceptionHandler.StudentNotFoundException |
                 GlobalExceptionHandler.SubjectNotFoundException e) {
            throw e; // Let the global exception handler manage these
        } catch (GlobalExceptionHandler.DuplicateBookingException | GlobalExceptionHandler.SlotFullException e) {
            throw e; // Let the global exception handler manage these too
        } catch (Exception e) {
            throw new RuntimeException("Failed to book exam slot: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/cancel/{registrationId}")
    public ResponseEntity<Void> cancelRegistration(
            @PathVariable String registrationId,
            @RequestParam String studentId) {
        schedulingService.cancelExamRegistration(studentId, registrationId);
        return ResponseEntity.noContent().build();
    }
}