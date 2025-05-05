package com.moviebooking.movie_booking_service.service;

import com.moviebooking.movie_booking_service.Exception.BookingException;
import com.moviebooking.movie_booking_service.Exception.ResourceNotFoundException;
import com.moviebooking.movie_booking_service.dto.AdminMovieStatus;
import com.moviebooking.movie_booking_service.dto.BalanceTicketResponse;
import com.moviebooking.movie_booking_service.dto.MovieRequest;
import com.moviebooking.movie_booking_service.dto.TicketBookingRequest;
import com.moviebooking.movie_booking_service.entities.Movie;
import com.moviebooking.movie_booking_service.entities.Ticket;
import com.moviebooking.movie_booking_service.events.TicketBookedEvent;
import com.moviebooking.movie_booking_service.repository.MovieRepository;
import com.moviebooking.movie_booking_service.repository.TicketRepository;
import com.moviebooking.movie_booking_service.response.MovieApiResponse;
import com.moviebooking.movie_booking_service.response.TicketApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TicketService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private KafkaTemplate<String, TicketBookedEvent> kafkaTemplate;

    public TicketApiResponse bookTicket(String movieName, TicketBookingRequest request) {

        try {
            // Create and save ticket
            Ticket ticket = new Ticket();
            ticket.setMovieName(movieName);
            ticket.setTheatreName(request.getTheatreName());
            ticket.setNumberOfTickets(request.getNumberOfTickets());
            ticket.setSeatNumbers(request.getSeatNumbers());
            ticket.setUserId(request.getUserId());
            ticketRepository.save(ticket);
            log.debug("Ticket saved with ID: {}", ticket.getId());

            // Send event to Kafka
            TicketBookedEvent event = new TicketBookedEvent(movieName, request.getTheatreName(), request.getNumberOfTickets());
            kafkaTemplate.send("ticket-booked-events", event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.debug("Event sent. Movie: {}, Tickets: {}, Metadata: {}",
                                    event.getMovieName(),
                                    event.getNumberOfTickets(),
                                    result.getRecordMetadata());
                        } else {
                            log.error("Kafka send failed. Movie: {}. Error: {}",
                                    event.getMovieName(),
                                    ex.getMessage());
                            // Consider adding to a retry queue here
                        }
                    });
            log.info("Successfully booked {} tickets for {} at {}",
                    request.getNumberOfTickets(), movieName, request.getTheatreName());
            return new TicketApiResponse("Ticket booked successfully", ticket);

        } catch (Exception e) {
            log.error("Ticket booking failed for {}: {}", request, e.getMessage(), e);
            throw new BookingException("Ticket booking failed");
        }
    }


    public BalanceTicketResponse getBalanceTickets(MovieRequest movieRequest, String theatreName) {
        Optional<Movie> movie = movieRepository.findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieRequest.getMovieName(), theatreName);
        if(movie.isEmpty()) {
            throw new ResourceNotFoundException("Invalid request");
        }
        return BalanceTicketResponse.builder()
                .movieName(movieRequest.getMovieName())
                .theatreName(movieRequest.getTheatreName())
                .availableTickets(movie.get().getBalanceTickets())
                .totalSeatsAllotted(movieRequest.getTotalTicketsAllotted())
                .build();
    }


    public AdminMovieStatus getMovieStatus(MovieRequest movieRequest) {


        List<Ticket> tickets = ticketRepository.findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieRequest.getMovieName(), movieRequest.getTheatreName());
        int totalBooked = tickets.stream().mapToInt(Ticket::getNumberOfTickets).sum();
        int available = movieRequest.getTotalTicketsAllotted() - totalBooked;


        return new AdminMovieStatus(
                movieRequest.getMovieName(),
                movieRequest.getTheatreName(),
                movieRequest.getTotalTicketsAllotted(),
                totalBooked,
                available,
                available == 0 ? "SOLD OUT" : "BOOK ASAP"
        );
    }

    // TicketService.java
    public List<String> getBookedSeats(String movieName, String theatreName) {
        log.debug("Fetching booked seats for movie: {} at theatre: {}", movieName, theatreName);

        try {
            long startTime = System.currentTimeMillis();

            List<Ticket> tickets = ticketRepository
                    .findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieName, theatreName);

            log.trace("Found {} tickets for movie: {} at theatre: {}",
                    tickets.size(), movieName, theatreName);

            List<String> bookedSeats = tickets.stream()
                    .peek(ticket -> log.trace("Processing ticket ID: {} with {} seats",
                            ticket.getId(), ticket.getSeatNumbers().size()))
                    .flatMap(ticket -> ticket.getSeatNumbers().stream())
                    .collect(Collectors.toList());

            long duration = System.currentTimeMillis() - startTime;
            log.info("Successfully retrieved {} booked seats for {} at {} in {} ms",
                    bookedSeats.size(), movieName, theatreName, duration);

            return bookedSeats;

        } catch (Exception e) {
            log.error("Error fetching booked seats for {} at {}: {}",
                    movieName, theatreName, e.getMessage(), e);
            throw new ResourceNotFoundException("Failed to retrieve booked seats");
        }
    }
}
