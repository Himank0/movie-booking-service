package com.moviebooking.movie_booking_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminMovieStatus {
    private String movieName;
    private String theatreName;
    private int totalTickets;
    private int ticketsBooked;
    private int ticketsAvailable;
    private String status;

    // Constructors, Getters, Setters
}

