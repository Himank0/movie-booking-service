package com.moviebooking.movie_booking_service.service;


import com.moviebooking.movie_booking_service.Exception.CredentialException;
import com.moviebooking.movie_booking_service.Exception.RegistrationException;
import com.moviebooking.movie_booking_service.dto.UserRegistrationRequest;
import com.moviebooking.movie_booking_service.entities.User;
import com.moviebooking.movie_booking_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registerUser(UserRegistrationRequest request) {
        log.info("Attempting to register user with loginId: {}", request.getLoginId());

        try {
            // Validate password match
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                log.warn("Password mismatch for registration attempt: {}", request.getLoginId());
                throw new CredentialException("Passwords do not match");
            }

            // Check for existing user
            if (userRepository.existsByLoginId(request.getLoginId())) {
                log.warn("Duplicate loginId attempt: {}", request.getLoginId());
                throw new CredentialException("Login ID already exists");
            }

            if (userRepository.existsByEmail(request.getEmail())) {
                log.warn("Duplicate email attempt: {}", request.getEmail());
                throw new CredentialException("Email already exists");
            }

            validatePasswordStrength(request.getPassword());

            String hashedPassword = passwordEncoder.encode(request.getPassword());
            log.debug("Password hashed successfully for: {}", request.getLoginId());

            User user = new User(
                    request.getFirstName(),
                    request.getLastName(),
                    request.getEmail(),
                    request.getLoginId(),
                    hashedPassword,
                    "USER", // Default role
                    request.getContactNumber()
            );

            User savedUser = userRepository.save(user);
            log.info("User registered successfully: {}", savedUser.getLoginId());

            return savedUser;

        } catch (CredentialException e) {
            log.error("Registration failed for {}: {}", request.getLoginId(), e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Registration failed for {}: {}", request.getLoginId(), e.getMessage(), e);
            throw new RegistrationException("Registration failed. Please try again later.");
        }
    }

    public User login(String loginId, String password) {
        log.debug("Login attempt for: {}", loginId);

        try {
            User user = userRepository.findByLoginId(loginId)
                    .orElseThrow(() -> {
                        log.warn("Invalid login attempt - user not found: {}", loginId);
                        return new CredentialException("Invalid credentials");
                    });

            if (!passwordEncoder.matches(password, user.getPassword())) {
                log.warn("Invalid password attempt for: {}", loginId);
                throw new CredentialException("Invalid credentials");
            }

            log.info("User logged in successfully: {}", loginId);
            return user;

        } catch (CredentialException e) {
            throw e;
        } catch (Exception e) {
            log.error("Login processing failed for {}: {}", loginId, e.getMessage(), e);
            throw new RuntimeException("Login failed. Please try again later.");
        }
    }

    public void resetPassword(String loginId, String newPassword) {
        log.info("Password reset requested for: {}", loginId);

        try {
            validatePasswordStrength(newPassword);

            User user = userRepository.findByLoginId(loginId)
                    .orElseThrow(() -> {
                        log.warn("Password reset attempt for non-existent user: {}", loginId);
                        return new CredentialException("User not found");
                    });

            if (passwordEncoder.matches(newPassword, user.getPassword())) {
                log.warn("Password reset with same password for: {}", loginId);
                throw new CredentialException("New password must be different");
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            log.info("Password reset successfully for: {}", loginId);

        } catch (CredentialException e) {
            log.error("Password reset failed for {}: {}", loginId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Password reset failed for {}: {}", loginId, e.getMessage(), e);
            throw new RuntimeException("Password reset failed. Please try again later.");
        }
    }

    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new CredentialException("Password must be at least 8 characters");
        }
    }

    public void logout(String loginId) {
        // In real-world apps, session or token management goes here
        // Placeholder for the logout logic
    }
}
