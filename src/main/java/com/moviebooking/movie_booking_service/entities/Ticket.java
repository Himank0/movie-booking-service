package com.moviebooking.movie_booking_service.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tickets")
public class Ticket {

    @Id
    private String id;

    private String movieName;
    private String theatreName;

    private int numberOfTickets;    // ek ticket me kitni seats book ki

    private List<String> seatNumbers;  // kon konsi ticket book ki

    private String userId; // to track which user booked the ticket

    private LocalDateTime bookingTime = LocalDateTime.now();

    // Getters, Setters, Constructors
}
