package com.moviebooking.movie_booking_service.service;

import com.moviebooking.movie_booking_service.Exception.CredentialException;
import com.moviebooking.movie_booking_service.dto.UserRegistrationRequest;
import com.moviebooking.movie_booking_service.entities.User;
import com.moviebooking.movie_booking_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoggingService loggingService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registerUser(UserRegistrationRequest request) {
        long startTime = System.currentTimeMillis();
        loggingService.logInfo("Starting user registration for loginId: " + request.getLoginId());

        // Password validation
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            loggingService.logWarn("Password mismatch for registration attempt: " + request.getLoginId());
            throw new CredentialException("Passwords do not match");
        }

        // Check for existing loginId
        if (userRepository.existsByLoginId(request.getLoginId())) {
            loggingService.logWarn("Duplicate loginId attempt: " + request.getLoginId());
            throw new CredentialException("Login ID already exists");
        }

        // Check for existing email
        if (userRepository.existsByEmail(request.getEmail())) {
            loggingService.logWarn("Duplicate email attempt: " + request.getEmail());
            throw new CredentialException("Email already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        loggingService.logInfo("Password hashed successfully for: " + request.getLoginId());

        User user = new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getLoginId(),
                hashedPassword,
                "USER",
                request.getContactNumber()
        );

        User savedUser = userRepository.save(user);
        loggingService.logInfo("User registered successfully - ID: " + savedUser.getLoginId() +
                ", Login: " + savedUser.getLoginId() +
                " (took " + (System.currentTimeMillis() - startTime) + "ms)");
        return savedUser;
    }

    public User login(String loginId, String password) {
        long startTime = System.currentTimeMillis();
        loggingService.logInfo("Login attempt for: " + loginId);

        User user = userRepository.findByLoginId(loginId)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .orElseThrow(() -> {
                    loggingService.logWarn("Failed login attempt for: " + loginId);
                    return new CredentialException("Invalid login credentials");
                });

        loggingService.logInfo("Successful login for: " + loginId +
                " (took " + (System.currentTimeMillis() - startTime) + "ms)");
        return user;
    }

    public void resetPassword(String loginId, String newPassword) {
        long startTime = System.currentTimeMillis();
        loggingService.logInfo("Password reset request for: " + loginId);

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> {
                    loggingService.logError("Password reset failed - User not found: " + loginId);
                    return new CredentialException("User not found");
                });

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        loggingService.logInfo("Password reset successful for: " + loginId +
                " (took " + (System.currentTimeMillis() - startTime) + "ms)");
    }

    public void logout(String loginId) {
        long startTime = System.currentTimeMillis();
        loggingService.logInfo("Logout initiated for: " + loginId);

        // In real-world apps, session or token management goes here
        loggingService.logInfo("Logout completed for: " + loginId +
                " (took " + (System.currentTimeMillis() - startTime) + "ms)");
    }
}