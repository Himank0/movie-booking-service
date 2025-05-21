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

    @Autowired
    private LoggingService loggingService;

    public TicketApiResponse bookTicket(String movieName, TicketBookingRequest request) {
        long startTime = System.currentTimeMillis();
        loggingService.logInfo("Starting ticket booking for movie: " + movieName +
                " at theatre: " + request.getTheatreName() +
                " for user: " + request.getUserId());

        Optional<Movie> movieOpt = movieRepository
                .findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieName,
                        request.getTheatreName());

        if (movieOpt.isEmpty()) {
            loggingService.logError("Movie/Theatre not found - Movie: " + movieName +
                    ", Theatre: " + request.getTheatreName());
            throw new BookingException("Movie or Theatre not found.");
        }

        Movie movie = movieOpt.get();
        loggingService.logInfo("Found movie with ID: " + movie.getId() +
                ", Current status: " + movie.getStatus());

        List<Ticket> tickets = ticketRepository.findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieName, request.getTheatreName());
        int totalBooked = tickets.stream().mapToInt(Ticket::getNumberOfTickets).sum();
        loggingService.logInfo("Total tickets already booked: " + totalBooked);

        int balance = movie.getTotalTicketsAllotted() - (totalBooked + request.getNumberOfTickets());
        loggingService.logInfo("Ticket availability check - Requested: " + request.getNumberOfTickets() +
                ", Available: " + balance);

        if(movie.getStatus().equalsIgnoreCase("SOLD OUT") || balance < request.getNumberOfTickets()) {
            loggingService.logWarn("Insufficient tickets - Available: " + balance +
                    ", Requested: " + request.getNumberOfTickets());
            throw new BookingException("Not enough tickets available.");
        }

        Ticket ticket = new Ticket();
        ticket.setMovieName(movieName);
        ticket.setTheatreName(request.getTheatreName());
        ticket.setNumberOfTickets(request.getNumberOfTickets());
        ticket.setSeatNumbers(request.getSeatNumbers());
        ticket.setUserId(request.getUserId());

        ticketRepository.save(ticket);
        loggingService.logInfo("Ticket created with ID: " + ticket.getId() +
                " for seats: " + request.getSeatNumbers());

        movie.setBalanceTickets(balance);
        movieRepository.save(movie);
        loggingService.logInfo("Updated movie balance tickets to: " + balance);

        loggingService.logInfo("Successfully completed booking for movie: " + movieName +
                " (took " + (System.currentTimeMillis() - startTime) + "ms)");
        return new TicketApiResponse("Ticket booked successfully", ticket);
    }

    public BalanceTicketResponse getBalanceTickets(MovieRequest movieRequest, String theatreName) {
        long startTime = System.currentTimeMillis();
        loggingService.logInfo("Checking balance tickets for movie: " + movieRequest.getMovieName() +
                " at theatre: " + theatreName);

        Optional<Movie> movie = movieRepository.findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieRequest.getMovieName(), theatreName);
        if(movie.isEmpty()) {
            loggingService.logError("Invalid balance check request - Movie: " + movieRequest.getMovieName() +
                    ", Theatre: " + theatreName);
            throw new ResourceNotFoundException("Invalid request");
        }

        BalanceTicketResponse response = BalanceTicketResponse.builder()
                .movieName(movieRequest.getMovieName())
                .theatreName(movieRequest.getTheatreName())
                .availableTickets(movie.get().getBalanceTickets())
                .totalSeatsAllotted(movieRequest.getTotalTicketsAllotted())
                .build();

        loggingService.logInfo("Balance check completed - Available: " + response.getAvailableTickets() +
                " (took " + (System.currentTimeMillis() - startTime) + "ms)");
        return response;
    }

    public AdminMovieStatus getMovieStatus(MovieRequest movieRequest) {
        long startTime = System.currentTimeMillis();
        loggingService.logInfo("Checking status for movie: " + movieRequest.getMovieName() +
                " at theatre: " + movieRequest.getTheatreName());

        List<Ticket> tickets = ticketRepository.findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieRequest.getMovieName(), movieRequest.getTheatreName());
        int totalBooked = tickets.stream().mapToInt(Ticket::getNumberOfTickets).sum();
        int available = movieRequest.getTotalTicketsAllotted() - totalBooked;

        String status = available == 0 ? "SOLD OUT" : "BOOK ASAP";
        loggingService.logInfo("Current status: " + status +
                ", Booked: " + totalBooked +
                ", Available: " + available);

        AdminMovieStatus response = new AdminMovieStatus(
                movieRequest.getMovieName(),
                movieRequest.getTheatreName(),
                movieRequest.getTotalTicketsAllotted(),
                totalBooked,
                available,
                status
        );

        loggingService.logInfo("Status check completed (took " + (System.currentTimeMillis() - startTime) + "ms)");
        return response;
    }

    public List<String> getBookedSeats(String movieName, String theatreName) {
        long startTime = System.currentTimeMillis();
        loggingService.logInfo("Fetching booked seats for movie: " + movieName +
                " at theatre: " + theatreName);

        List<Ticket> tickets = ticketRepository
                .findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieName, theatreName);

        List<String> bookedSeats = tickets.stream()
                .flatMap(ticket -> ticket.getSeatNumbers().stream())
                .collect(Collectors.toList());

        loggingService.logInfo("Found " + bookedSeats.size() + " booked seats" +
                " (took " + (System.currentTimeMillis() - startTime) + "ms)");
        return bookedSeats;
    }
}