package com.moviebooking.movie_booking_service.service;

import com.moviebooking.movie_booking_service.entities.Movie;
import com.moviebooking.movie_booking_service.entities.Ticket;
import com.moviebooking.movie_booking_service.repository.MovieRepository;
import com.moviebooking.movie_booking_service.repository.TicketRepository;
import com.moviebooking.movie_booking_service.service.LoggingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final MovieRepository movieRepository;
    private final TicketRepository ticketRepository;
    private final LoggingService loggingService;

    public AdminService(MovieRepository movieRepository,
                        TicketRepository ticketRepository,
                        LoggingService loggingService) {
        this.movieRepository = movieRepository;
        this.ticketRepository = ticketRepository;
        this.loggingService = loggingService;
    }

    public Movie updateBalanceTickets(String movieName, String theatreName) {
        long startTime = System.currentTimeMillis();
        loggingService.logInfo("Starting ticket balance update for movie: " + movieName +
                " at theatre: " + theatreName);

        List<Ticket> bookedTickets = ticketRepository.findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieName, theatreName);
        loggingService.logInfo("Found " + bookedTickets.size() + " booked ticket records");

        int totalBooked = bookedTickets.stream()
                .mapToInt(Ticket::getNumberOfTickets)
                .sum();
        loggingService.logInfo("Total tickets booked: " + totalBooked);

        Movie movie = movieRepository.findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieName, theatreName)
                .orElseThrow(() -> {
                    loggingService.logError("Movie not found: " + movieName + " at theatre: " + theatreName);
                    return new RuntimeException("Movie not found");
                });

        int remainingTickets = movie.getTotalTicketsAllotted() - totalBooked;
        movie.setBalanceTickets(remainingTickets);

        if (remainingTickets <= 0) {
            movie.setStatus("SOLD_OUT");
            loggingService.logWarn("Movie status changed to SOLD_OUT: " + movieName);
        } else {
            movie.setStatus("BOOKING_OPEN");
            loggingService.logInfo("Movie status set to BOOKING_OPEN: " + movieName);
        }

        Movie updatedMovie = movieRepository.save(movie);
        loggingService.logInfo("Successfully updated ticket balance. Remaining tickets: " + remainingTickets +
                " for movie: " + movieName +
                " (took " + (System.currentTimeMillis() - startTime) + "ms)");
        return updatedMovie;
    }

    public List<Ticket> getBookedTickets(String movieName, String theatreName) {
        long startTime = System.currentTimeMillis();
        loggingService.logInfo("Fetching booked tickets for movie: " + movieName +
                " at theatre: " + theatreName);

        List<Ticket> tickets = ticketRepository.findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieName, theatreName);

        loggingService.logInfo("Returning " + tickets.size() + " booked tickets for movie: " + movieName +
                " (took " + (System.currentTimeMillis() - startTime) + "ms)");
        return tickets;
    }
}