package com.moviebooking.movie_booking_service.repository;


import com.moviebooking.movie_booking_service.entities.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface TicketRepository extends MongoRepository<Ticket, String> {
    List<Ticket> findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(String movieName, String theatreName);

    List<Ticket> findByMovieNameIgnoreCase(String movieName);
}

