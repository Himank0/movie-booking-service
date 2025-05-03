package com.moviebooking.movie_booking_service.controller;

import com.moviebooking.movie_booking_service.entities.Movie;
import com.moviebooking.movie_booking_service.entities.Ticket;
import com.moviebooking.movie_booking_service.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ResponseEntity.ok(adminService.getBookedTickets(movieName, theatreName));
    }

    @PutMapping("/update-balance")
    public ResponseEntity<Movie> updateBalanceTickets(
            @RequestParam String movieName,
            @RequestParam String theatreName) {
        Movie updatedMovie = adminService.updateBalanceTickets(movieName, theatreName);
        return ResponseEntity.ok(updatedMovie);
    }
}
