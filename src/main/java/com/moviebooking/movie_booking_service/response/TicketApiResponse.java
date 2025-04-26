package com.moviebooking.movie_booking_service.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

@Getter
@Setter
@Transactional
public class TicketApiResponse {
    private String message;
    private Object data;

    public TicketApiResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }
        // Getters & Setters
}


