package com.moviebooking.movie_booking_service.service;

import com.moviebooking.movie_booking_service.Exception.ResourceNotFoundException;
import com.moviebooking.movie_booking_service.dto.MovieResponse;
import com.moviebooking.movie_booking_service.entities.Movie;
import com.moviebooking.movie_booking_service.entities.Ticket;
import com.moviebooking.movie_booking_service.repository.MovieRepository;
import com.moviebooking.movie_booking_service.repository.TicketRepository;
import com.moviebooking.movie_booking_service.service.LoggingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final TicketRepository ticketRepository;
    private final LoggingService loggingService;

    public MovieService(MovieRepository movieRepository,
                        TicketRepository ticketRepository,
                        LoggingService loggingService) {
        this.movieRepository = movieRepository;
        this.ticketRepository = ticketRepository;
        this.loggingService = loggingService;
    }

    public List<MovieResponse> getAllMovies() {
        long startTime = System.currentTimeMillis();
        loggingService.logInfo("Fetching all movies from repository");

        List<MovieResponse> movies = movieRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        loggingService.logInfo("Successfully retrieved " + movies.size() + " movies" +
                " (took " + (System.currentTimeMillis() - startTime) + "ms)");
        return movies;
    }

    public List<MovieResponse> searchMoviesByName(String movieName) {
        long startTime = System.currentTimeMillis();
        loggingService.logInfo("Searching movies by name: " + movieName);

        List<MovieResponse> movies = movieRepository.findByMovieNameContainingIgnoreCase(movieName)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        loggingService.logInfo("Found " + movies.size() + " movies matching search: " + movieName +
                " (took " + (System.currentTimeMillis() - startTime) + "ms)");
        return movies;
    }

    public void deleteMovie(String movieName, String theatreName, String movieId) {
        long startTime = System.currentTimeMillis();
        loggingService.logInfo("Starting movie deletion - Movie: " + movieName +
                ", Theatre: " + theatreName +
                ", ID: " + movieId);

        // Check if movie exists
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> {
                    loggingService.logError("Movie not found - ID: " + movieId +
                            ", Name: " + movieName +
                            ", Theatre: " + theatreName);
                    return new ResourceNotFoundException(
                            "Movie not found for name: " + movieName + ", theatre: " + theatreName);
                });

        // Delete associated tickets
        List<Ticket> tickets = ticketRepository
                .findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieName, theatreName);

        if (!tickets.isEmpty()) {
            loggingService.logInfo("Deleting " + tickets.size() + " associated tickets");
            ticketRepository.deleteAll(tickets);
        } else {
            loggingService.logInfo("No associated tickets found for deletion");
        }

        // Delete the movie
        movieRepository.delete(movie);
        loggingService.logInfo("Successfully deleted movie: " + movieName +
                " from theatre: " + theatreName +
                " (took " + (System.currentTimeMillis() - startTime) + "ms)");
    }

    private MovieResponse mapToDTO(Movie movie) {
        MovieResponse dto = new MovieResponse();
        dto.setMovieName(movie.getMovieName());
        dto.setTheatreName(movie.getTheatreName());
        dto.setTotalTickets(movie.getTotalTicketsAllotted());
        dto.setBalanceTickets(movie.getBalanceTickets());
        dto.setImageUrl(movie.getImageUrl());
        return dto;
    }
}