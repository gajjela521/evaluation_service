package com.gajjelsa.evaluation_service.service;

import com.gajjelsa.evaluation_service.dto.*;
import com.gajjelsa.evaluation_service.model.*;
import com.gajjelsa.evaluation_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class RegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private PrincipalRepository principalRepository;

    @Autowired
    private ITAdminRepository itAdminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponse registerStudent(StudentRegistrationRequest request) {
        // Check if user already exists
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        // Create User
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setRole(User.UserRole.STUDENT);
        user = userRepository.save(user);

        // Create Student
        Student student = new Student();
        student.setUserId(user.getId());
        student.setFullName(request.getName());
        student.setEmail(request.getEmail());
        student.setEnrollmentNumber(request.getEnrollmentNumber());
        student.setDepartment(request.getDepartment());
        student.setSemester(request.getSemester());

        // Optional fields
        student.setClassId(request.getClassId());
        student.setContactNumber(request.getContactNumber());
        student.setAddress(request.getAddress());
        student.setDateOfBirth(parseDateOfBirth(request.getDateOfBirth()));

        studentRepository.save(student);

        return createAuthResponse(user, "Student registration successful. Pending admin approval.");
    }

    public AuthResponse registerTeacher(TeacherRegistrationRequest request) {
        // Check if user already exists
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        // Create User
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setRole(User.UserRole.TEACHER);
        user = userRepository.save(user);

        // Create Teacher
        Teacher teacher = new Teacher();
        teacher.setUserId(user.getId());
        teacher.setEmployeeId(request.getEmployeeId());
        teacher.setDepartment(request.getDepartment());
        teacher.setSpecialization(request.getSpecialization());
        teacher.setQualification(request.getQualification());
        teacherRepository.save(teacher);

        return createAuthResponse(user, "Teacher registration successful. Pending admin approval.");
    }

    public AuthResponse registerPrincipal(PrincipalRegistrationRequest request) {
        // Check if user already exists
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        // Create User
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setRole(User.UserRole.PRINCIPAL);
        user = userRepository.save(user);

        // Create Principal
        Principal principal = new Principal();
        principal.setUserId(user.getId());
        principal.setEmployeeId(request.getEmployeeId());
        principal.setDepartment(request.getDepartment());
        principal.setYearsOfExperience(request.getYearsOfExperience());
        principalRepository.save(principal);

        return createAuthResponse(user, "Principal registration successful. Pending admin approval.");
    }

    public AuthResponse registerITAdmin(ITAdminRegistrationRequest request) {
        // Check if user already exists
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        // Create User
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setRole(User.UserRole.IT_ADMIN);
        user = userRepository.save(user);

        // Create ITAdmin
        ITAdmin itAdmin = new ITAdmin();
        itAdmin.setUserId(user.getId());
        itAdmin.setEmployeeId(request.getEmployeeId());
        itAdmin.setDepartment(request.getDepartment());
        itAdmin.setSystemRole(request.getSystemRole());
        itAdminRepository.save(itAdmin);

        return createAuthResponse(user, "IT Admin registration successful. Pending admin approval.");
    }

    private AuthResponse createAuthResponse(User user, String message) {
        AuthResponse response = new AuthResponse();
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setRole(user.getRole());
        response.setApproved(user.isApproved());
        response.setApprovalStatus(user.getApprovalStatus());
        response.setMessage(message + " You will be automatically approved after 24 hours if admin doesn't respond. For immediate access, contact admin at: gajjelasuryateja2021@gmail.com");
        return response;
    }

    private LocalDate parseDateOfBirth(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            return null;
        }
    }
}
