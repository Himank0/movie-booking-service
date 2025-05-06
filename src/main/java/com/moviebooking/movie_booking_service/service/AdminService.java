package com.moviebooking.movie_booking_service.service;

import com.moviebooking.movie_booking_service.Exception.ResourceNotFoundException;
import com.moviebooking.movie_booking_service.entities.Movie;
import com.moviebooking.movie_booking_service.entities.Ticket;
import com.moviebooking.movie_booking_service.events.CalculateBookedSeatsEvent;
import com.moviebooking.movie_booking_service.events.TicketBookedEvent;
import com.moviebooking.movie_booking_service.events.UpdateMovieBalanceEvent;
import com.moviebooking.movie_booking_service.repository.MovieRepository;
import com.moviebooking.movie_booking_service.repository.TicketRepository;
import jakarta.websocket.OnClose;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AdminService {

    private final MovieRepository movieRepository;
    private final TicketRepository ticketRepository;


    public AdminService(MovieRepository movieRepository, TicketRepository ticketRepository) {
        this.movieRepository = movieRepository;
        this.ticketRepository = ticketRepository;
    }

    @KafkaListener(topics = "ticket-booked-events", groupId = "movie-service-group")
    public void handleTicketBookedEvent(@Payload TicketBookedEvent event) {
        log.info("Received ticket booked event for {} at {}",
                event.getMovieName(), event.getTheatreName());
        try {
            updateBalanceTickets(event.getMovieName(), event.getTheatreName());
            log.debug("Successfully processed event for {} at {}",
                    event.getMovieName(), event.getTheatreName());
        } catch (Exception e) {
            log.error("Error processing ticket event for {} at {}: {}",
                    event.getMovieName(), event.getTheatreName(), e.getMessage(), e);
            throw e;
        }
    }

    public Movie updateBalanceTickets(String movieName, String theatreName) {
        log.debug("Updating balance tickets for {} at {}", movieName, theatreName);

        List<Ticket> bookedTickets = ticketRepository
                .findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieName, theatreName);
        log.trace("Found {} booked tickets for {} at {}",
                bookedTickets.size(), movieName, theatreName);

        int totalBooked = bookedTickets.stream()
                .mapToInt(Ticket::getNumberOfTickets)
                .sum();
        log.debug("Total tickets booked: {}", totalBooked);

        Movie movie = movieRepository
                .findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieName, theatreName)
                .orElseThrow(() -> {
                    log.error("Movie not found: {} at {}", movieName, theatreName);
                    return new ResourceNotFoundException("Movie not found");
                });

        int remainingTickets = movie.getTotalTicketsAllotted() - totalBooked;
        log.info("Updating remaining tickets from {} to {}",
                movie.getBalanceTickets(), remainingTickets);

        movie.setBalanceTickets(remainingTickets);
        String newStatus = remainingTickets <= 0 ? "SOLD_OUT" : "BOOKING_OPEN";
        log.info("Setting status to {} for {} at {}", newStatus, movieName, theatreName);
        movie.setStatus(newStatus);

        Movie updatedMovie = movieRepository.save(movie);
        log.debug("Successfully updated movie record: {}", updatedMovie);
        return updatedMovie;
    }

    public List<Ticket> getBookedTickets(String movieName, String theatreName) {
        log.debug("Fetching all booked tickets for movie: {} at theatre: {}",
                movieName, theatreName);

        try {
            long startTime = System.currentTimeMillis();

            List<Ticket> tickets = ticketRepository
                    .findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieName, theatreName);

            long duration = System.currentTimeMillis() - startTime;

            if (tickets.isEmpty()) {
                log.warn("No tickets found for movie: {} at theatre: {}", movieName, theatreName);
            } else {
                log.info("Retrieved {} tickets for {} at {} in {} ms",
                        tickets.size(), movieName, theatreName, duration);

                // Log summary statistics at debug level
                int totalSeats = tickets.stream()
                        .mapToInt(t -> t.getSeatNumbers().size())
                        .sum();
                log.debug("Total seats booked: {} across {} tickets", totalSeats, tickets.size());
            }

            return tickets;

        } catch (Exception e) {
            log.error("Failed to retrieve tickets for {} at {}: {}",
                    movieName, theatreName, e.getMessage(), e);
            throw new ResourceNotFoundException("Ticket retrieval failed");
        }
    }
}