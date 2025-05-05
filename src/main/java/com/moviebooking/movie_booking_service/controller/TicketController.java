package com.moviebooking.movie_booking_service.controller;

import com.moviebooking.movie_booking_service.dto.TicketBookingRequest;
import com.moviebooking.movie_booking_service.response.TicketApiResponse;
import com.moviebooking.movie_booking_service.service.TicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1.0/moviebooking")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/{moviename}/add")
    public ResponseEntity<TicketApiResponse> bookTicket(
            @PathVariable("moviename") String movieName,
            @Valid @RequestBody TicketBookingRequest request) {
        log.info("Booking request received for {}: {}", movieName, request);
        TicketApiResponse response = ticketService.bookTicket(movieName, request);
        log.debug("Booking completed successfully: {}", response);
        return ResponseEntity.ok(response);
    }

    // TicketController.java
    @GetMapping("/{moviename}/{theatrename}/seats")
    public ResponseEntity<TicketApiResponse> getBookedSeats(
            @PathVariable("moviename") String movieName,
            @PathVariable("theatrename") String theatreName) {
        log.info("Seat availability check for {} at {}", movieName, theatreName);
        List<String> bookedSeats = ticketService.getBookedSeats(movieName, theatreName);
        log.debug("Found {} booked seats for {} at {}",
                bookedSeats.size(), movieName, theatreName);
        return ResponseEntity.ok(new TicketApiResponse("Booked seats retrieved", bookedSeats));
    }
}
