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

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registerUser(UserRegistrationRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new CredentialException("Passwords do not match");
        }

        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new CredentialException("Login ID already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CredentialException("Email already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getLoginId(),
                hashedPassword,
                "USER",
                request.getContactNumber()
        );

        return userRepository.save(user);
    }

    public User login(String loginId, String password) {
        System.out.println(passwordEncoder.encode(password));
        return userRepository.findByLoginId(loginId)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElseThrow(() -> new CredentialException("Invalid login credentials"));
    }

    public void resetPassword(String loginId, String newPassword) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CredentialException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void logout(String loginId) {
        // In real-world apps, session or token management goes here
        // Placeholder for the logout logic
    }
}
