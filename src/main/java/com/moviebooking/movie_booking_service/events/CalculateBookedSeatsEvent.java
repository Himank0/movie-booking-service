package com.moviebooking.movie_booking_service.events;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalculateBookedSeatsEvent {
    private String movieName;

    private  String theatreName;

    public CalculateBookedSeatsEvent(String movieName, String theatreName) {
        this.movieName = movieName;
        this.theatreName = theatreName;
    }
}
