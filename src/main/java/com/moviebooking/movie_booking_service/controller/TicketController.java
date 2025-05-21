package com.moviebooking.movie_booking_service.controller;

import com.moviebooking.movie_booking_service.dto.TicketBookingRequest;
import com.moviebooking.movie_booking_service.response.TicketApiResponse;
import com.moviebooking.movie_booking_service.service.LoggingService;
import com.moviebooking.movie_booking_service.service.TicketService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/v1.0/moviebooking")
public class TicketController {

    private final TicketService ticketService;
    private final LoggingService loggingService;

    public TicketController(TicketService ticketService, LoggingService loggingService) {
        this.ticketService = ticketService;
        this.loggingService = loggingService;
    }

    @PostMapping("/{moviename}/add")
    public ResponseEntity<TicketApiResponse> bookTicket(
            @PathVariable("moviename") String movieName,
            @Valid @RequestBody TicketBookingRequest request) {

        long startTime = System.currentTimeMillis();
        loggingService.logInfo("Starting ticket booking for movie: " + movieName +
                ", request: " + request.toString());

        TicketApiResponse response = ticketService.bookTicket(movieName, request);

        loggingService.logInfo("Successfully booked ticket. Movie: " + movieName +
                ", Seats: " + request.getSeatNumbers() +
                " (took " + (System.currentTimeMillis() - startTime) + "ms)");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{moviename}/{theatrename}/seats")
    public ResponseEntity<TicketApiResponse> getBookedSeats(
            @PathVariable("moviename") String movieName,
            @PathVariable("theatrename") String theatreName) {

        long startTime = System.currentTimeMillis();
        loggingService.logInfo("Fetching booked seats for movie: " + movieName +
                " at theatre: " + theatreName);

        List<String> bookedSeats = ticketService.getBookedSeats(movieName, theatreName);

        loggingService.logInfo("Retrieved " + bookedSeats.size() + " booked seats for movie: " +
                movieName + " at theatre: " + theatreName +
                " (took " + (System.currentTimeMillis() - startTime) + "ms)");
        return ResponseEntity.ok(new TicketApiResponse("Booked seats retrieved", bookedSeats));
    }
}