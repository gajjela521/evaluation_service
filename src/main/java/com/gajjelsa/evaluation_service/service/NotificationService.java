package com.gajjelsa.evaluation_service.service;

import com.gajjelsa.evaluation_service.GlobalExceptionHandler;
import com.gajjelsa.evaluation_service.model.ExamRegistration;
import com.gajjelsa.evaluation_service.model.ExamSlot;
import com.gajjelsa.evaluation_service.model.Student;
import com.gajjelsa.evaluation_service.model.Subject;
import com.gajjelsa.evaluation_service.repository.EvaluationRepository;
import jakarta.mail.internet.MimeMessage;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final EvaluationRepository.ExamSlotRepository slotRepository;
    private final EvaluationRepository.SubjectRepository subjectRepository;
    private final EvaluationRepository.StudentRepository studentRepository;

    @Autowired
    public NotificationService(JavaMailSender mailSender,
                               TemplateEngine templateEngine,
                               EvaluationRepository.ExamSlotRepository slotRepository,
                               EvaluationRepository.SubjectRepository subjectRepository,
                               EvaluationRepository.StudentRepository studentRepository) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.slotRepository = slotRepository;
        this.subjectRepository = subjectRepository;
        this.studentRepository = studentRepository;
    }

    /**
     * Send exam booking confirmation email
     */
    @Async
    public void sendExamBookingConfirmation(ExamRegistration registration) {

        try {
            // Fetch related data
            Student student = studentRepository.findById(registration.getStudentId())
                    .orElseThrow(() -> new GlobalExceptionHandler.StudentNotFoundException("Student not found"));

            ExamSlot slot = slotRepository.findById(String.valueOf(registration.getSlotId()))
                    .orElseThrow(() -> new GlobalExceptionHandler.SlotNotFoundException("Exam slot not found"));

            Subject subject = subjectRepository.findById(registration.getSubjectId())
                    .orElseThrow(() -> new GlobalExceptionHandler.SubjectNotFoundException("Subject not found"));

            // Prepare email template context
            Context context = new Context();
            context.setVariable("studentName", student.getFullName());
            context.setVariable("studentId", student.getId());
            context.setVariable("subject", subject.getName());
            context.setVariable("examDate", slot.getExamDate().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
            context.setVariable("examTime", slot.getStartTime().format(DateTimeFormatter.ofPattern("h:mm a")) +
                    " - " + slot.getEndTime().format(DateTimeFormatter.ofPattern("h:mm a")));
            context.setVariable("location", slot.getLocation());
            context.setVariable("registrationId", registration.getId());
            context.setVariable("confirmationCode", generateConfirmationCode(registration));

            // Process the template
            String emailContent = templateEngine.process("exam-confirmation", context);

            // Create the email message
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(student.getEmail());
            helper.setSubject("Exam Registration Confirmation: " + subject.getName());
            helper.setText(emailContent, true); // true indicates HTML content

            // Add calendar invite attachment
            helper.addAttachment("exam-invite.ics", generateCalendarInvite(student, slot, subject));

            // Send the email
            mailSender.send(message);

            // Log the email sending
            log.info("Sent exam confirmation email to student {}", student.getId());

        } catch (Exception e) {
            log.error("Failed to send exam confirmation email", e);
            // Consider retry mechanism or fallback notification
        }
    }

    /**
     * Generate a unique confirmation code for the registration
     */
    private String generateConfirmationCode(ExamRegistration registration) {
        // Combining registration ID with timestamp and applying a hash function
        String baseString = registration.getId() + "-" + registration.getRegistrationTime().toString();
        return DigestUtils.sha256Hex(baseString).substring(0, 8).toUpperCase();
    }

    /**
     * Generate an iCalendar file for the exam
     */
    private ByteArrayResource generateCalendarInvite(Student student, ExamSlot slot, Subject subject) {
        try {
            // Create a new iCal4j calendar
            net.fortuna.ical4j.model.Calendar calendar = new net.fortuna.ical4j.model.Calendar();
            calendar.getProperties().add(new ProdId("-//Exam System//iCal4j 1.0//EN"));
            calendar.getProperties().add(Version.VERSION_2_0);
            calendar.getProperties().add(CalScale.GREGORIAN);

            // Create the event
            LocalDateTime startDateTime = LocalDateTime.of(slot.getExamDate(), slot.getStartTime());
            LocalDateTime endDateTime = LocalDateTime.of(slot.getExamDate(), slot.getEndTime());

            // Convert Java time to iCal4j DateTime
            java.util.Date startDate = java.util.Date.from(startDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant());
            java.util.Date endDate = java.util.Date.from(endDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant());

            DateTime start = new DateTime(startDate);
            DateTime end = new DateTime(endDate);

            VEvent examEvent = new VEvent(start, end, subject.getName() + " Exam");

            // Add event details
            examEvent.getProperties().add(new Description("Exam for " + subject.getName()));
            examEvent.getProperties().add(new Location(slot.getLocation()));

            // Add an alarm/reminder 1 day before
            VAlarm reminder = new VAlarm(java.time.Duration.ofHours(-24));  // 24 hours before
            reminder.getProperties().add(Action.DISPLAY);
            reminder.getProperties().add(new Description("Reminder: " + subject.getName() + " exam tomorrow"));
            examEvent.getAlarms().add(reminder);


            // Add the event to the calendar
            calendar.getComponents().add(examEvent);

            // Convert to a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CalendarOutputter outputter = new CalendarOutputter();
            outputter.output(calendar, baos);

            return new ByteArrayResource(baos.toByteArray());

        } catch (Exception e) {
            log.error("Failed to generate calendar invite", e);
            return null;
        }
    }

    /**
     * Send exam cancellation notification
     */
    @Async
    public void sendExamCancellationNotification(String studentId, ExamRegistration registration) {
        // Similar implementation for cancellation emails
    }

    /**
     * Send reminder email 24 hours before exam
     */
    @Async
    public void sendExamReminder(ExamRegistration registration) {
        // Implementation for reminder emails
    }
}
