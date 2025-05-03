package com.moviebooking.movie_booking_service.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMovieBalanceEvent {
    private  String movieName;

    private String theatreName;

    private int totalBooked;
    public UpdateMovieBalanceEvent(String movieName, String theatreName, int totalBooked) {
        this.movieName = movieName;
        this.theatreName = theatreName;
        this.totalBooked = totalBooked;
    }
}
