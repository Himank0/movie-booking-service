package com.moviebooking.movie_booking_service.service;

import com.moviebooking.movie_booking_service.Exception.ResourceNotFoundException;
import com.moviebooking.movie_booking_service.dto.MovieResponse;
import com.moviebooking.movie_booking_service.entities.Movie;
import com.moviebooking.movie_booking_service.entities.Ticket;
import com.moviebooking.movie_booking_service.repository.MovieRepository;
import com.moviebooking.movie_booking_service.repository.TicketRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final TicketRepository ticketRepository;

    public MovieService(MovieRepository movieRepository, TicketRepository ticketRepository) {
        this.movieRepository = movieRepository;
        this.ticketRepository = ticketRepository;
    }

    public List<MovieResponse> getAllMovies() {
        log.info("Fetching all movies from database");
        try {
            long startTime = System.currentTimeMillis();

            List<MovieResponse> movies = movieRepository.findAll().stream()
                    .peek(movie -> log.trace("Mapping movie: {}", movie.getMovieName()))
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());

            log.info("Successfully retrieved {} movies in {} ms",
                    movies.size(), System.currentTimeMillis() - startTime);
            return movies;

        } catch (Exception e) {
            log.error("Failed to retrieve movies: {}", e.getMessage(), e);
            throw new ResourceNotFoundException("Movie retrieval failed");
        }
    }


    public List<MovieResponse> searchMoviesByName(String movieName) {
        log.debug("Searching movies containing: {}", movieName);

        try {
            if (movieName == null || movieName.trim().isEmpty()) {
                log.warn("Empty search term provided");
                return Collections.emptyList();
            }

            long startTime = System.currentTimeMillis();
            List<MovieResponse> results = movieRepository
                    .findByMovieNameContainingIgnoreCase(movieName)
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());

            log.debug("Found {} movies matching '{}' in {} ms",
                    results.size(), movieName, System.currentTimeMillis() - startTime);
            return results;

        } catch (Exception e) {
            log.error("Search failed for term '{}': {}", movieName, e.getMessage(), e);
            throw new ResourceNotFoundException("Movie search failed");
        }
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
