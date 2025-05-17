package com.moviebooking.movie_booking_service.service;

import com.moviebooking.movie_booking_service.entities.Movie;
import com.moviebooking.movie_booking_service.entities.Ticket;
import com.moviebooking.movie_booking_service.repository.MovieRepository;
import com.moviebooking.movie_booking_service.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final MovieRepository movieRepository;
    private final TicketRepository ticketRepository;

    public AdminService(MovieRepository movieRepository, TicketRepository ticketRepository) {
        this.movieRepository = movieRepository;
        this.ticketRepository = ticketRepository;
    }

    public Movie updateBalanceTickets(String movieName, String theatreName) {
        List<Ticket> bookedTickets = ticketRepository.findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieName, theatreName);

        int totalBooked = bookedTickets.stream()
                .mapToInt(Ticket::getNumberOfTickets)
                .sum();

        Movie movie = movieRepository.findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieName, theatreName)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        int remainingTickets = movie.getTotalTicketsAllotted() - totalBooked;
        movie.setBalanceTickets(remainingTickets);

        if (remainingTickets <= 0) {
            movie.setStatus("SOLD_OUT");
        } else {
            movie.setStatus("BOOKING_OPEN");
        }

        return movieRepository.save(movie);
    }

    public List<Ticket> getBookedTickets(String movieName, String theatreName) {
        return ticketRepository.findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieName, theatreName);
    }
}