package com.moviebooking.movie_booking_service.controller;

import com.moviebooking.movie_booking_service.dto.PasswordResetRequest;
import com.moviebooking.movie_booking_service.dto.UserRegistrationRequest;
import com.moviebooking.movie_booking_service.entities.User;
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

    // Register User
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserRegistrationRequest request) {
        User user = userService.registerUser(request);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // Login User
    @GetMapping("/login")
    public ResponseEntity<User> login(@RequestParam String loginId, @RequestParam String password) {
        User user = userService.login(loginId, password);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // Reset Password
    @PostMapping("/{username}/forgot")
    public ResponseEntity<String> resetPassword( @PathVariable("username") String loginId, @RequestBody PasswordResetRequest request) {
        userService.resetPassword(loginId, request.getNewPassword());
        return new ResponseEntity<>("Password reset successfully", HttpStatus.OK);
    }

    // Logout User
    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String loginId) {
        userService.logout(loginId);
        return new ResponseEntity<>("User logged out successfully", HttpStatus.OK);
    }
}
