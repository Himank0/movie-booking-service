package com.moviebooking.movie_booking_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MovieRequest {

    String movieId;
    String movieName;

    String theatreName;

    int totalTicketsAllotted;
}
