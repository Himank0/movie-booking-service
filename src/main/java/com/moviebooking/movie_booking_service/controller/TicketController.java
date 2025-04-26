package com.moviebooking.movie_booking_service.controller;

import com.moviebooking.movie_booking_service.dto.TicketBookingRequest;
import com.moviebooking.movie_booking_service.response.TicketApiResponse;
import com.moviebooking.movie_booking_service.service.TicketService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/v1.0/moviebooking")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/{moviename}/add")
    public ResponseEntity<TicketApiResponse> bookTicket(@PathVariable("moviename") String movieName, @Valid @RequestBody TicketBookingRequest request) {
        System.out.println("start");
        TicketApiResponse response = ticketService.bookTicket(movieName, request);
        System.out.println("end");
        return ResponseEntity.ok(response);
    }

    // TicketController.java
    @GetMapping("/{moviename}/{theatrename}/seats")
    public ResponseEntity<TicketApiResponse> getBookedSeats(
            @PathVariable("moviename") String movieName,
            @PathVariable("theatrename") String theatreName) {

        List<String> bookedSeats = ticketService.getBookedSeats(movieName, theatreName);
        return ResponseEntity.ok(new TicketApiResponse("Booked seats retrieved", bookedSeats));
    }
}
