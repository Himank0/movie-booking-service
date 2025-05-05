package com.moviebooking.movie_booking_service.controller;

import com.moviebooking.movie_booking_service.dto.PasswordResetRequest;
import com.moviebooking.movie_booking_service.dto.UserRegistrationRequest;
import com.moviebooking.movie_booking_service.entities.User;
import com.moviebooking.movie_booking_service.response.UserApiResponse;
import com.moviebooking.movie_booking_service.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1.0/moviebooking")
public class UserController {

    @Autowired
    private UserService userService;

    // Register User
    @PostMapping("/register")
    public ResponseEntity<UserApiResponse<User>> register(@Valid @RequestBody UserRegistrationRequest request) {
        try {
            User user = userService.registerUser(request);
            log.info("User registered successfully: {}", user.getLoginId());
            return ResponseEntity.ok(
                    new UserApiResponse<>(
                            "Registration successful",
                            user,
                            HttpStatus.OK.value()
                    )
            );

        } catch (Exception e) {
            log.error("Registration failed for {}: {}", request.getLoginId(), e.getMessage());
            return ResponseEntity.badRequest().body(
                    new UserApiResponse<>(
                            e.getMessage(),
                            null,
                            HttpStatus.BAD_REQUEST.value()
                    )
            );
        }

    }

    // Login User
    @GetMapping("/login")
    public ResponseEntity<UserApiResponse<User>> login(@RequestParam String loginId, @RequestParam String password) {
        try {
            User user = userService.login(loginId, password);
            log.info("Login successful for: {}", loginId);
            return ResponseEntity.ok(
                    new UserApiResponse<>(
                            "Login successful",
                            user,
                            HttpStatus.OK.value()
                    )
            );
        } catch (Exception e) {
            log.warn("Login failed for {}: {}", loginId, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new UserApiResponse<>(
                            e.getMessage(),
                            null,
                            HttpStatus.UNAUTHORIZED.value()
                    )
            );
        }
    }

    // Reset Password
    @PostMapping("/{username}/forgot")
    public ResponseEntity<UserApiResponse<Void>> resetPassword( @PathVariable("username") String loginId, @RequestBody PasswordResetRequest request) {
        try {
            userService.resetPassword(loginId, request.getNewPassword());
            log.info("Password reset successful for: {}", loginId);
            return ResponseEntity.ok(
                    new UserApiResponse<>(
                            "Password reset successfully",
                            null,
                            HttpStatus.OK.value()
                    )
            );
        } catch (Exception e) {
            log.error("Password reset failed for {}: {}", loginId, e.getMessage());
            return ResponseEntity.badRequest().body(
                    new UserApiResponse<>(
                            e.getMessage(),
                            null,
                            HttpStatus.BAD_REQUEST.value()
                    )
            );
        }
    }

    // Logout User
    @GetMapping("/logout")
    public ResponseEntity<UserApiResponse<Void>> logout(@RequestParam String loginId) {
        try {
            userService.logout(loginId);
            log.info("Logout successful for: {}", loginId);
            return ResponseEntity.ok(
                    new UserApiResponse<>(
                            "Logout successful",
                            null,
                            HttpStatus.OK.value()
                    )
            );
        } catch (Exception e) {
            log.error("Logout failed for {}: {}", loginId, e.getMessage());
            return ResponseEntity.internalServerError().body(
                    new UserApiResponse<>(
                            "Logout failed",
                            null,
                            HttpStatus.INTERNAL_SERVER_ERROR.value()
                    )
            );
        }
    }
}
