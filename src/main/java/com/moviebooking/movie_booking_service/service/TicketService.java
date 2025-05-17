package com.moviebooking.movie_booking_service.service;

import com.moviebooking.movie_booking_service.Exception.BookingException;
import com.moviebooking.movie_booking_service.Exception.ResourceNotFoundException;
import com.moviebooking.movie_booking_service.dto.AdminMovieStatus;
import com.moviebooking.movie_booking_service.dto.BalanceTicketResponse;
import com.moviebooking.movie_booking_service.dto.MovieRequest;
import com.moviebooking.movie_booking_service.dto.TicketBookingRequest;
import com.moviebooking.movie_booking_service.entities.Movie;
import com.moviebooking.movie_booking_service.entities.Ticket;
import com.moviebooking.movie_booking_service.repository.MovieRepository;
import com.moviebooking.movie_booking_service.repository.TicketRepository;
import com.moviebooking.movie_booking_service.response.MovieApiResponse;
import com.moviebooking.movie_booking_service.response.TicketApiResponse;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TicketRepository ticketRepository;

    public TicketApiResponse bookTicket(String movieName, TicketBookingRequest request) {
        Optional<Movie> movieOpt = movieRepository
                .findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieName,
                        request.getTheatreName());

        if (movieOpt.isEmpty()) {
            throw new BookingException("Movie or Theatre not found.");
        }

        Movie movie = movieOpt.get();

        List<Ticket> tickets = ticketRepository.findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieName, request.getTheatreName());
        int totalBooked = tickets.stream().mapToInt(Ticket::getNumberOfTickets).sum();

        // Calculate balance
        int balance = movie.getTotalTicketsAllotted() - (totalBooked + request.getNumberOfTickets());
//        System.out.println("totalBooked: " + totalBooked + " balance : " + balance);




        if(movie.getStatus().equalsIgnoreCase("SOLD OUT") || balance < request.getNumberOfTickets()) {
            throw new BookingException("Not enough tickets available.");
        }

        // Create Ticket
        Ticket ticket = new Ticket();
        ticket.setMovieName(movieName);
        ticket.setTheatreName(request.getTheatreName());
        ticket.setNumberOfTickets(request.getNumberOfTickets());
        ticket.setSeatNumbers(request.getSeatNumbers());
        ticket.setUserId(request.getUserId());


        ticketRepository.save(ticket);
        ticketRepository.findAll().forEach(t -> System.out.println(t.getId() + " "));

        movie.setBalanceTickets(balance);
        movieRepository.save(movie);

        return new TicketApiResponse("Ticket booked successfully", ticket);
    }

//    Integer getBalanceTickets(MovieRequest movieRequest, String theatreName) {
//        List<Ticket> tickets = ticketRepository.findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieRequest.getMovieName(), theatreName);
//        int totalBooked = tickets.stream().mapToInt(Ticket::getNumberOfTickets).sum();
//        return movieRequest.getTotalTicketsAllotted() - totalBooked;
//    }

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