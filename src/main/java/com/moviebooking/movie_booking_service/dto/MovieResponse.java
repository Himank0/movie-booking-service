package com.moviebooking.movie_booking_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponse {
    private String movieName;
    private String theatreName;
    private int totalTickets;
    private int balanceTickets;
    private String imageUrl;
}