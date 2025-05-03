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
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private KafkaTemplate<String, TicketBookedEvent> kafkaTemplate;

    public TicketApiResponse bookTicket(String movieName, TicketBookingRequest request) {
//        Optional<Movie> movieOpt = movieRepository
//                .findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieName,
//                        request.getTheatreName());
//
//        if (movieOpt.isEmpty()) {
//            throw new BookingException("Movie or Theatre not found.");
//        }

//        Movie movie = movieOpt.get();

//        List<Ticket> tickets = ticketRepository.findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieName, request.getTheatreName());
//
//        int totalBooked = tickets.stream().mapToInt(Ticket::getNumberOfTickets).sum();
//        int available = movie.getTotalTicketsAllotted() - totalBooked;
//
//        // Fix availability check
//        if (movie.getStatus().equalsIgnoreCase("SOLD_OUT") || request.getNumberOfTickets() > available) {
//            throw new BookingException("Not enough tickets available.");
//        }

        // Create and save ticket
        Ticket ticket = new Ticket();
        ticket.setMovieName(movieName);
        ticket.setTheatreName(request.getTheatreName());
        ticket.setNumberOfTickets(request.getNumberOfTickets());
        ticket.setSeatNumbers(request.getSeatNumbers());
        ticket.setUserId(request.getUserId());
        ticketRepository.save(ticket);

        // Send event to Kafka
        TicketBookedEvent event = new TicketBookedEvent(movieName, request.getTheatreName(), request.getNumberOfTickets());
        kafkaTemplate.send("ticket-booked-events", event);

        return new TicketApiResponse("Ticket booked successfully", ticket);
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
        List<Ticket> tickets = ticketRepository
                .findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieName, theatreName);

        return tickets.stream()
                .flatMap(ticket -> ticket.getSeatNumbers().stream())
                .collect(Collectors.toList());
    }
}
