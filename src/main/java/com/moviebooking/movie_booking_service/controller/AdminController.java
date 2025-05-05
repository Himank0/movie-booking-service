package com.moviebooking.movie_booking_service.controller;

import com.moviebooking.movie_booking_service.entities.Movie;
import com.moviebooking.movie_booking_service.entities.Ticket;
import com.moviebooking.movie_booking_service.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1.0/moviebooking")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/booked-tickets")
    public ResponseEntity<List<Ticket>> viewBookedTickets(
            @RequestParam String movieName,
            @RequestParam String theatreName) {
        log.info("Request received for booked tickets - Movie: {}, Theatre: {}",
                movieName, theatreName);
        return ResponseEntity.ok(adminService.getBookedTickets(movieName, theatreName));
    }

    @PutMapping("/update-balance")
    public ResponseEntity<Movie> updateBalanceTickets(
            @RequestParam String movieName,
            @RequestParam String theatreName) {
        log.info("Manual balance update requested for {} at {}", movieName, theatreName);
        Movie updatedMovie = adminService.updateBalanceTickets(movieName, theatreName);
        log.debug("Balance update completed: {}", updatedMovie);
        return ResponseEntity.ok(updatedMovie);
    }
}
