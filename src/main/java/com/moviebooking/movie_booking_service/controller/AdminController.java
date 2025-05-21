package com.moviebooking.movie_booking_service.controller;

import com.moviebooking.movie_booking_service.entities.Movie;
import com.moviebooking.movie_booking_service.entities.Ticket;
import com.moviebooking.movie_booking_service.service.AdminService;
import com.moviebooking.movie_booking_service.service.LoggingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1.0/moviebooking")
public class AdminController {

    private final AdminService adminService;
    private final LoggingService loggingService;

    public AdminController(AdminService adminService, LoggingService loggingService) {
        this.adminService = adminService;
        this.loggingService = loggingService;
    }

    @GetMapping("/booked-tickets")
    public ResponseEntity<List<Ticket>> viewBookedTickets(
            @RequestParam String movieName,
            @RequestParam String theatreName) {
        long startTime = System.currentTimeMillis();
        loggingService.logInfo("Fetching booked tickets for movie: " + movieName +
                " at theatre: " + theatreName + " - started");

        List<Ticket> tickets = adminService.getBookedTickets(movieName, theatreName);

        loggingService.logInfo("Successfully fetched " + tickets.size() +
                " booked tickets for movie: " + movieName +
                " at theatre: " + theatreName +
                " (took " + (System.currentTimeMillis() - startTime) + "ms)");
        return ResponseEntity.ok(tickets);
    }

    @PutMapping("/update-balance")
    public ResponseEntity<Movie> updateBalanceTickets(
            @RequestParam String movieName,
            @RequestParam String theatreName) {
        long startTime = System.currentTimeMillis();
        loggingService.logInfo("Updating balance tickets for movie: " + movieName +
                " at theatre: " + theatreName + " - started");

        Movie updatedMovie = adminService.updateBalanceTickets(movieName, theatreName);

        loggingService.logInfo("Successfully updated balance tickets for movie: " + movieName +
                " at theatre: " + theatreName +
                ". New available tickets: " + updatedMovie.getBalanceTickets() +
                " (took " + (System.currentTimeMillis() - startTime) + "ms)");
        return ResponseEntity.ok(updatedMovie);
    }
}