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
     * Only returns morning slots (9-11 AM) and afternoon slots (1-4 PM)
     */
    public List<ExamSlot> getAvailableSlots(LocalDate examDate, String subjectId) {
        // Get predefined slots for the specified date
        List<ExamSlot> availableSlots = new ArrayList<>();

        // Morning slots: 9-10 AM and 10-11 AM
        availableSlots.add(new ExamSlot(examDate, LocalTime.of(9, 0), LocalTime.of(10, 0), subjectId));
        availableSlots.add(new ExamSlot(examDate, LocalTime.of(10, 0), LocalTime.of(11, 0), subjectId));

        // Afternoon slots: 1-2 PM, 2-3 PM, and 3-4 PM
        availableSlots.add(new ExamSlot(examDate, LocalTime.of(13, 0), LocalTime.of(14, 0), subjectId));
        availableSlots.add(new ExamSlot(examDate, LocalTime.of(14, 0), LocalTime.of(15, 0), subjectId));
        availableSlots.add(new ExamSlot(examDate, LocalTime.of(15, 0), LocalTime.of(16, 0), subjectId));

        return availableSlots;
    }

    /**
     * Books an exam slot for a student
     * Ensures a student can only book one slot per exam day
     */
    @Transactional("mongoTransactionManager")
    public ExamRegistration bookExamSlot(String studentId, String slotId, String subjectId) {
        // Find the slot
        ExamSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new GlobalExceptionHandler.SlotNotFoundException("Exam slot not found"));

        // Check for existing bookings on the same day
        LocalDate examDate = slot.getExamDate();
        List<Long> slotsForDate = getSlotIdsByDate(examDate);
        boolean hasExistingBooking = registrationRepository.existsByStudentIdAndSlotIdIn(studentId, slotsForDate);

        if (hasExistingBooking) {
            throw new GlobalExceptionHandler.DuplicateBookingException("Student has already booked a slot for this exam day");
        }

        // Check slot availability
        long currentBookings = registrationRepository.countBySlotId(slotId);
        if (currentBookings >= slot.getCapacity()) {
            throw new GlobalExceptionHandler.SlotFullException("This slot is already at full capacity");
        }

        // Create a new registration object
        ExamRegistration registration = new ExamRegistration();
        registration.setStudentId(studentId);
        registration.setSlotId(slotId);
        registration.setSubjectId(subjectId);
        registration.setRegistrationTime(LocalDateTime.now());
        registration.setStatus(RegistrationStatus.CONFIRMED);

        // Save and return the registration
        ExamRegistration savedRegistration = registrationRepository.save(registration);

        // Send confirmation email (consider doing this asynchronously)
        notificationService.sendExamBookingConfirmation(savedRegistration);

        return savedRegistration;
    }

    // Update the helper method to return String IDs
    private List<Long> getSlotIdsByDate(LocalDate date) {
        List<ExamSlot> slots = slotRepository.findByExamDate(date);
        return slots.stream().map(ExamSlot::getId).collect(Collectors.toList());
    }




    /**
     * Cancels an existing exam registration
     */
    @Transactional("mongoTransactionManager")
    public void cancelExamRegistration(String studentId, String registrationId) {
        ExamRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new GlobalExceptionHandler.RegistrationNotFoundException("Registration not found"));

        // Verify student owns this registration
        if (!registration.getStudentId().equals(studentId)) {
            throw new GlobalExceptionHandler.UnauthorizedAccessException("Student does not own this registration");
        }

        // Check cancellation policy (e.g., can only cancel 24 hours before exam)
        ExamSlot slot = slotRepository.findById(String.valueOf(registration.getSlotId()))
                .orElseThrow(() -> new GlobalExceptionHandler.SlotNotFoundException("Exam slot not found"));

        LocalDateTime examDateTime = LocalDateTime.of(slot.getExamDate(), slot.getStartTime());
        LocalDateTime cancellationDeadline = examDateTime.minusHours(24);

        if (LocalDateTime.now().isAfter(cancellationDeadline)) {
            throw new GlobalExceptionHandler.LateCancellationException("Cannot cancel within 24 hours of exam");
        }

        // Delete the registration
        registrationRepository.delete(registration);

        // Send cancellation notification
        notificationService.sendExamCancellationNotification(studentId, registration);
    }


    /**
     * Books an exam slot for a student and sends confirmation email
     */
    @Transactional("mongoTransactionManager")
    public ExamRegistration bookExamSlot(String studentId, Long slotId, String subjectId) {
        ExamRegistration registration = new ExamRegistration();
        registration.setStudentId(studentId);
        registration.setSlotId(String.valueOf(slotId));
        registration.setSubjectId(subjectId);
        registration.setRegistrationTime(LocalDateTime.now());
        registration.setStatus(RegistrationStatus.CONFIRMED);

        ExamRegistration savedRegistration = registrationRepository.save(registration);

        // Send confirmation email asynchronously
        notificationService.sendExamBookingConfirmation(savedRegistration);

        return savedRegistration;
    }

    /**
     * Cancels an existing exam registration and sends cancellation email
     */
    @Transactional("mongoTransactionManager")
    public void cancelExamRegistration(String studentId, Long registrationId) {
        ExamRegistration registration = registrationRepository.findById(String.valueOf(registrationId))
                .orElseThrow(() -> new GlobalExceptionHandler.RegistrationNotFoundException("Registration not found"));
                registrationRepository.delete(registration);

        notificationService.sendExamCancellationNotification(studentId, registration);
    }

}
