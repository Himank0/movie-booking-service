package com.moviebooking.movie_booking_service.service;

import com.moviebooking.movie_booking_service.Exception.ResourceNotFoundException;
import com.moviebooking.movie_booking_service.dto.MovieResponse;
import com.moviebooking.movie_booking_service.entities.Movie;
import com.moviebooking.movie_booking_service.entities.Ticket;
import com.moviebooking.movie_booking_service.repository.MovieRepository;
import com.moviebooking.movie_booking_service.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final TicketRepository ticketRepository;

    public MovieService(MovieRepository movieRepository, TicketRepository ticketRepository) {
        this.movieRepository = movieRepository;
        this.ticketRepository = ticketRepository;
    }

    public List<MovieResponse> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<MovieResponse> searchMoviesByName(String movieName) {
        return movieRepository.findByMovieNameContainingIgnoreCase(movieName)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void deleteMovie(String movieName, String theatreName, String movieId) {

        // Check if movie exists
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Movie not found for name: " + movieName + ", theatre: " + theatreName));

        // Delete associated tickets
        List<Ticket> tickets = ticketRepository
                .findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(movieName, theatreName);

        if (!tickets.isEmpty()) {
            ticketRepository.deleteAll(tickets);
        }

        // Delete the movie
        movieRepository.delete(movie);
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
