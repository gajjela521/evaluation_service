package com.gajjelsa.evaluation_service.service;

import com.gajjelsa.evaluation_service.GlobalExceptionHandler;
import com.gajjelsa.evaluation_service.model.ExamRegistration;
import com.gajjelsa.evaluation_service.model.ExamSlot;
import com.gajjelsa.evaluation_service.model.RegistrationStatus;
import com.gajjelsa.evaluation_service.model.Subject;
import com.gajjelsa.evaluation_service.repository.ExamSlotRepository;
import com.gajjelsa.evaluation_service.repository.RegistrationRepository;
import com.gajjelsa.evaluation_service.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamSchedulingService {

    private final SubjectRepository subjectRepository;
    private final ExamSlotRepository slotRepository;
    private final RegistrationRepository registrationRepository;
    private final NotificationService notificationService;

    @Autowired
    public ExamSchedulingService(SubjectRepository subjectRepository,
                                 ExamSlotRepository slotRepository,
                                 RegistrationRepository registrationRepository,
                                 NotificationService notificationService) {
        this.subjectRepository = subjectRepository;
        this.slotRepository = slotRepository;
        this.registrationRepository = registrationRepository;
        this.notificationService = notificationService;
    }

    /**
     * Retrieves available subjects based on student's class/grade
     */
    public List<Subject> getAvailableSubjectsByClass(String classId) {
        return subjectRepository.findByClassId(classId);
    }

    /**
     * Returns available exam slots for a given date
     * TODO: This will be replaced with proper slot creation in Step 2
     * Currently creates temporary slots for demonstration
     */
    public List<ExamSlot> getAvailableSlots(LocalDate examDate, String subjectId) {
        // Get existing slots from database first
        List<ExamSlot> existingSlots = slotRepository.findBySubjectIdAndExamDate(subjectId, examDate);

        if (!existingSlots.isEmpty()) {
            return existingSlots;
        }

        // Temporary: Create predefined slots if none exist (for backward compatibility)
        // This will be removed in Step 2 when we implement proper exam creation
        List<ExamSlot> availableSlots = new ArrayList<>();

        // Morning slots: 9-10 AM and 10-11 AM
        availableSlots.add(new ExamSlot(examDate, LocalTime.of(9, 0), LocalTime.of(10, 0),
                subjectId, 30, "Room A101"));
        availableSlots.add(new ExamSlot(examDate, LocalTime.of(10, 0), LocalTime.of(11, 0),
                subjectId, 30, "Room A102"));

        // Afternoon slots: 1-2 PM, 2-3 PM, and 3-4 PM
        availableSlots.add(new ExamSlot(examDate, LocalTime.of(13, 0), LocalTime.of(14, 0),
                subjectId, 30, "Room B101"));
        availableSlots.add(new ExamSlot(examDate, LocalTime.of(14, 0), LocalTime.of(15, 0),
                subjectId, 30, "Room B102"));
        availableSlots.add(new ExamSlot(examDate, LocalTime.of(15, 0), LocalTime.of(16, 0),
                subjectId, 30, "Room B103"));

        // Save the temporary slots to database
        return slotRepository.saveAll(availableSlots);
    }

    /**
     * Books an exam slot for a student
     * Fixed: Single method with consistent String types
     */
    @Transactional  // Removed mongoTransactionManager reference - will fix in Step 2
    public ExamRegistration bookExamSlot(String studentId, String slotId, String subjectId) {
        // 1. Validate slot exists
        ExamSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new GlobalExceptionHandler.SlotNotFoundException("Exam slot not found"));

        // 2. Validate slot belongs to the requested subject
        if (!slot.getSubjectId().equals(subjectId)) {
            throw new GlobalExceptionHandler.SlotNotFoundException("Slot does not match the requested subject");
        }

        // 3. Check for existing bookings on the same day
        boolean hasExistingBooking = registrationRepository.existsByStudentIdAndExamDate(
                studentId, slot.getExamDate());

        if (hasExistingBooking) {
            throw new GlobalExceptionHandler.DuplicateBookingException(
                    "Student has already booked a slot for this exam day");
        }

        // 4. Check if student already booked this specific slot
        boolean alreadyBookedSlot = registrationRepository.existsByStudentIdAndSlotId(studentId, slotId);
        if (alreadyBookedSlot) {
            throw new GlobalExceptionHandler.DuplicateBookingException(
                    "Student has already booked this specific slot");
        }

        // 5. Check slot availability
        long currentBookings = registrationRepository.countBySlotId(slotId);
        if (currentBookings >= slot.getCapacity()) {
            throw new GlobalExceptionHandler.SlotFullException("This slot is already at full capacity");
        }

        // 6. Create registration
        ExamRegistration registration = new ExamRegistration();
        registration.setStudentId(studentId);
        registration.setSlotId(slotId);  // Now String type
        registration.setSubjectId(subjectId);
        registration.setRegistrationTime(LocalDateTime.now());
        registration.setExamDate(slot.getExamDate());
        registration.setStatus(RegistrationStatus.CONFIRMED);

        ExamRegistration savedRegistration = registrationRepository.save(registration);

        // 7. Send notification (async)
        try {
            notificationService.sendExamBookingConfirmation(savedRegistration);
        } catch (Exception e) {
            // Log error but don't fail the booking
            System.err.println("Failed to send notification: " + e.getMessage());
        }

        return savedRegistration;
    }

    /**
     * Cancels an existing exam registration
     * Fixed: Consistent String type usage
     */
    @Transactional
    public void cancelExamRegistration(String studentId, String registrationId) {
        ExamRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new GlobalExceptionHandler.RegistrationNotFoundException("Registration not found"));

        // Verify student owns this registration
        if (!registration.getStudentId().equals(studentId)) {
            throw new GlobalExceptionHandler.UnauthorizedAccessException("Student does not own this registration");
        }

        // Check cancellation policy (e.g., can only cancel 24 hours before exam)
        ExamSlot slot = slotRepository.findById(registration.getSlotId())
                .orElseThrow(() -> new GlobalExceptionHandler.SlotNotFoundException("Exam slot not found"));

        LocalDateTime examDateTime = LocalDateTime.of(slot.getExamDate(), slot.getStartTime());
        LocalDateTime cancellationDeadline = examDateTime.minusHours(24);

        if (LocalDateTime.now().isAfter(cancellationDeadline)) {
            throw new GlobalExceptionHandler.LateCancellationException("Cannot cancel within 24 hours of exam");
        }

        // Delete the registration
        registrationRepository.delete(registration);

        // Send cancellation notification
        try {
            notificationService.sendExamCancellationNotification(studentId, registration);
        } catch (Exception e) {
            // Log error but don't fail the cancellation
            System.err.println("Failed to send cancellation notification: " + e.getMessage());
        }
    }

    /**
     * Helper method to get slot IDs by date
     * Fixed: Return type changed from List<Long> to List<String>
     */
    private List<String> getSlotIdsByDate(LocalDate date) {
        List<ExamSlot> slots = slotRepository.findByExamDate(date);
        return slots.stream().map(ExamSlot::getId).collect(Collectors.toList());
    }

    // Removed the duplicate bookExamSlot method with Long slotId parameter
}