package com.gajjelsa.evaluation_service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Base exception class for all custom exceptions
    public static class EvaluationServiceException extends RuntimeException {
        public EvaluationServiceException(String message) {
            super(message);
        }
    }

    // Scheduling exceptions
    public static class SlotNotFoundException extends EvaluationServiceException {
        public SlotNotFoundException(String message) {
            super(message);
        }
    }

    public static class DuplicateBookingException extends EvaluationServiceException {
        public DuplicateBookingException(String message) {
            super(message);
        }
    }

    public static class SlotFullException extends EvaluationServiceException {
        public SlotFullException(String message) {
            super(message);
        }
    }

    public static class RegistrationNotFoundException extends EvaluationServiceException {
        public RegistrationNotFoundException(String message) {
            super(message);
        }
    }

    public static class UnauthorizedAccessException extends EvaluationServiceException {
        public UnauthorizedAccessException(String message) {
            super(message);
        }
    }

    public static class LateCancellationException extends EvaluationServiceException {
        public LateCancellationException(String message) {
            super(message);
        }
    }

    // Entity exceptions
    public static class StudentNotFoundException extends EvaluationServiceException {
        public StudentNotFoundException(String message) {
            super(message);
        }
    }

    public static class SubjectNotFoundException extends EvaluationServiceException {
        public SubjectNotFoundException(String message) {
            super(message);
        }
    }

    // Evaluation exceptions
    public static class ScoreSheetProcessingException extends EvaluationServiceException {
        public ScoreSheetProcessingException(String message) {
            super(message);
        }
    }

    // Email exceptions
    public static class EmailSendingException extends EvaluationServiceException {
        public EmailSendingException(String message, Throwable cause) {
            super(message);
            initCause(cause);
        }
    }

    // Response structure for all exceptions
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private String message;
        private String details;
        private String errorCode;

        public ErrorResponse(LocalDateTime timestamp, String message, String details, String errorCode) {
            this.timestamp = timestamp;
            this.message = message;
            this.details = details;
            this.errorCode = errorCode;
        }

        // Getters and setters
        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }
    }

    // Generic handler for all custom exceptions
    @ExceptionHandler(EvaluationServiceException.class)
    public ResponseEntity<ErrorResponse> handleEvaluationServiceExceptions(
            EvaluationServiceException ex, WebRequest request) {

        HttpStatus status;
        String errorCode;

        // Determine appropriate HTTP status and error code based on exception type
        if (ex instanceof SlotNotFoundException ||
                ex instanceof RegistrationNotFoundException ||
                ex instanceof StudentNotFoundException ||
                ex instanceof SubjectNotFoundException) {
            status = HttpStatus.NOT_FOUND;
            errorCode = "RESOURCE_NOT_FOUND";
        } else if (ex instanceof UnauthorizedAccessException) {
            status = HttpStatus.FORBIDDEN;
            errorCode = "ACCESS_DENIED";
        } else if (ex instanceof DuplicateBookingException ||
                ex instanceof SlotFullException ||
                ex instanceof LateCancellationException) {
            status = HttpStatus.BAD_REQUEST;
            errorCode = "VALIDATION_ERROR";
        } else if (ex instanceof EmailSendingException) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorCode = "EMAIL_SERVICE_ERROR";
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorCode = "INTERNAL_ERROR";
        }

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false),
                errorCode
        );

        return new ResponseEntity<>(errorResponse, status);
    }

    // Handler for other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "An unexpected error occurred",
                request.getDescription(false),
                "INTERNAL_SERVER_ERROR"
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}