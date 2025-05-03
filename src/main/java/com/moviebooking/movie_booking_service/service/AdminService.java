package com.moviebooking.movie_booking_service.service;

import com.moviebooking.movie_booking_service.entities.Movie;
import com.moviebooking.movie_booking_service.entities.Ticket;
import com.moviebooking.movie_booking_service.events.CalculateBookedSeatsEvent;
import com.moviebooking.movie_booking_service.events.TicketBookedEvent;
import com.moviebooking.movie_booking_service.events.UpdateMovieBalanceEvent;
import com.moviebooking.movie_booking_service.repository.MovieRepository;
import com.moviebooking.movie_booking_service.repository.TicketRepository;
import jakarta.websocket.OnClose;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final MovieRepository movieRepository;
    private final TicketRepository ticketRepository;

//    @Autowired
//    private KafkaTemplate<String, TicketBookedEvent> kafkaTemplate;

    public AdminService(MovieRepository movieRepository, TicketRepository ticketRepository) {
        this.movieRepository = movieRepository;
        this.ticketRepository = ticketRepository;
    }

    @KafkaListener(
            topics = "ticket-booked-events",
            groupId = "movie-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleTicketBookedEvent(@Payload TicketBookedEvent event) {
        updateBalanceTickets(event.getMovieName(), event.getTheatreName());
    }

//    @KafkaListener(topics = "ticket-booked-events", groupId = "movie-service-group")
//    public void handleTicketBookedEvent(TicketBookedEvent event) {
//        // Forward to next step (calculate booked seats)
//        kafkaTemplate.send("calculate-booked-seats",
//                new CalculateBookedSeatsEvent(event.getMovieName(), event.getTheatreName())
//        );
//    }

//    @KafkaListener(topics = "calculate-booked-seats", groupId = "movie-service-group")
//    public void calculateBookedSeats(CalculateBookedSeatsEvent event) {
//        // Skip if using atomic counters; otherwise:
//        List<Ticket> tickets = ticketRepository.findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(
//                event.getMovieName(),
//                event.getTheatreName()
//        );
//        int totalBooked = tickets.stream().mapToInt(Ticket::getNumberOfTickets).sum();
//
//        // Publish to next step
//        kafkaTemplate.send("update-movie-balance",
//                new UpdateMovieBalanceEvent(event.getMovieName(), event.getTheatreName(), totalBooked)
//        );
//    }

//    @KafkaListener(topics = "update-movie-balance", groupId = "movie-service-group")
//    public void updateMovieBalance(UpdateMovieBalanceEvent event) {
//        Movie movie = movieRepository.findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(
//                event.getMovieName(),
//                event.getTheatreName()
//        ).orElseThrow();
//
//        // Update balance/status
//        movie.setBalanceTickets(movie.getTotalTicketsAllotted() - event.getTotalBooked());
//        movie.setStatus(movie.getBalanceTickets() <= 0 ? "SOLD_OUT" : "BOOKING_OPEN");
//        movieRepository.save(movie);
//    }

    public Movie updateBalanceTickets(String movieName, String theatreName) {
        List<Ticket> bookedTickets = ticketRepository.findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieName, theatreName);

        int totalBooked = bookedTickets.stream()
                .mapToInt(Ticket::getNumberOfTickets)
                .sum();

        Movie movie = movieRepository.findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieName, theatreName)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        int remainingTickets = movie.getTotalTicketsAllotted() - totalBooked;
        movie.setBalanceTickets(remainingTickets);

        movie.setStatus(remainingTickets <= 0 ? "SOLD_OUT" : "BOOKING_OPEN");
        return movieRepository.save(movie);
    }

    public List<Ticket> getBookedTickets(String movieName, String theatreName) {
        return ticketRepository.findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieName, theatreName);
    }
}
