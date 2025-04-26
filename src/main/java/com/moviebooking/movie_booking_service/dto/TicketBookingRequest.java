package com.moviebooking.movie_booking_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketBookingRequest {


    @NotBlank(message = "Theatre name is required")
    private String theatreName;

    @Min(value = 1, message = "At least one ticket must be booked")
    private int numberOfTickets;

    @Size(min = 1, message = "Seat numbers must be provided")
    private List<String> seatNumbers;

    @NotBlank(message = "User ID is required")
    private String userId;

}
