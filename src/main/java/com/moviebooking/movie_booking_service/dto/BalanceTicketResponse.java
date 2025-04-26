package com.moviebooking.movie_booking_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class BalanceTicketResponse {
    String movieName;

    String theatreName;

    Integer totalSeatsAllotted;

    Integer availableTickets;
}
