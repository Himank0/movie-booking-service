package com.moviebooking.movie_booking_service.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketBookedEvent {
    private String movieName;
    private String theatreName;

    private int numberOfTickets;

    public TicketBookedEvent() {}

    @Override
    public String toString() {
        return "TicketBookedEvent{" +
                "movieName='" + movieName + '\'' +
                ", theatreName='" + theatreName + '\'' +
                ", numberOfTickets=" + numberOfTickets +
                '}';
    }
    @JsonCreator
    public TicketBookedEvent(
            @JsonProperty("movieName") String movieName,
            @JsonProperty("theatreName") String theatreName,
            @JsonProperty("numberOfTickets") int numberOfTickets) {
        this.movieName = movieName;
        this.theatreName = theatreName;
        this.numberOfTickets = numberOfTickets;
    }
}
