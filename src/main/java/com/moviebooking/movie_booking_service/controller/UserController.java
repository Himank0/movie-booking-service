package com.moviebooking.movie_booking_service.controller;

import com.moviebooking.movie_booking_service.dto.PasswordResetRequest;
import com.moviebooking.movie_booking_service.dto.UserRegistrationRequest;
import com.moviebooking.movie_booking_service.entities.User;
import com.moviebooking.movie_booking_service.service.LoggingService;
import com.moviebooking.movie_booking_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1.0/moviebooking")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private LoggingService loggingService;

    // Register User
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserRegistrationRequest request) {
        long startTime = System.currentTimeMillis();
        loggingService.logInfo("Starting user registration for: " + request.getLoginId());

        User user = userService.registerUser(request);

        loggingService.logInfo("Successfully registered user: " + user.getLoginId() +
                ", User ID: " + user.getLoginId() +
                " (took " + (System.currentTimeMillis() - startTime) + "ms)");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // Login User
    @GetMapping("/login")
    public ResponseEntity<User> login(@RequestParam String loginId, @RequestParam String password) {
        long startTime = System.currentTimeMillis();
        loggingService.logInfo("Login attempt for user: " + loginId);

        User user = userService.login(loginId, password);

        loggingService.logInfo("Successful login for user: " + loginId +
                " (took " + (System.currentTimeMillis() - startTime) + "ms)");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // Reset Password
    @PostMapping("/{username}/forgot")
    public ResponseEntity<String> resetPassword(
            @PathVariable("username") String loginId,
            @RequestBody PasswordResetRequest request) {

        long startTime = System.currentTimeMillis();
        loggingService.logInfo("Password reset request for user: " + loginId);

        userService.resetPassword(loginId, request.getNewPassword());

        loggingService.logInfo("Password successfully reset for user: " + loginId +
                " (took " + (System.currentTimeMillis() - startTime) + "ms)");
        return new ResponseEntity<>("Password reset successfully", HttpStatus.OK);
    }

    // Logout User
    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String loginId) {
        long startTime = System.currentTimeMillis();
        loggingService.logInfo("Logout request for user: " + loginId);

        userService.logout(loginId);

        loggingService.logInfo("Successful logout for user: " + loginId +
                " (took " + (System.currentTimeMillis() - startTime) + "ms)");
        return new ResponseEntity<>("User logged out successfully", HttpStatus.OK);
    }
}