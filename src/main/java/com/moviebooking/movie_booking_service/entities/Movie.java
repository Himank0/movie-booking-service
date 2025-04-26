package com.moviebooking.movie_booking_service.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;


@NoArgsConstructor
@Data
@Document(collection = "movie")
@CompoundIndex(def = "{'movieName': 1, 'theatreName': 1}", name = "movie_theatre_idx", unique = true)
public class Movie {

    @Id
    private String id;

    private String movieName;

    private String theatreName;

    private int totalTicketsAllotted; // movie k particular theatre me kitni sets hai movie ki

    private Integer balanceTickets;

    private String status;

    private String ImageUrl;

    public Movie(String movieName, String theatreName, int totalTicketsAllotted, int balanceTickets, String status, String ImageUrl) {
        this.movieName = movieName;
        this.theatreName = theatreName;
        this.totalTicketsAllotted = totalTicketsAllotted;
        this.balanceTickets = balanceTickets;
        this.status = status;
        this.ImageUrl = ImageUrl;
    }
}
