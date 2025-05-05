package com.moviebooking.movie_booking_service.controller;

import com.moviebooking.movie_booking_service.dto.MovieResponse;
import com.moviebooking.movie_booking_service.entities.Movie;
import com.moviebooking.movie_booking_service.response.MovieApiResponse;
import com.moviebooking.movie_booking_service.service.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v1.0/moviebooking")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/all")
    public ResponseEntity<MovieApiResponse<List<MovieResponse>>> getAllMovies() {
        try {
            long startTime = System.currentTimeMillis();
            List<MovieResponse> movies = movieService.getAllMovies();

            if (movies.isEmpty()) {
                log.info("Returning empty movie list (took {}ms)", System.currentTimeMillis() - startTime);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                        new MovieApiResponse<>("No movies available", null)
                );
            }

            log.info("Returning {} movies (took {}ms)", movies.size(), System.currentTimeMillis() - startTime);
            return ResponseEntity.ok(
                    new MovieApiResponse<>("Movies retrieved successfully", movies)
            );
        } catch (Exception e) {
            log.error("Failed to fetch movies: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    new MovieApiResponse<>("Failed to retrieve movies", null)
            );
        }
    }

    @GetMapping("/movies/search/{moviename}")
    public ResponseEntity<?> searchMovies(@PathVariable("moviename") String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                log.warn("Empty search parameter received");
                return ResponseEntity.badRequest().body(
                        new MovieApiResponse<>("Search parameter cannot be empty", null)
                );
            }

            long startTime = System.currentTimeMillis();
            List<MovieResponse> movies = movieService.searchMoviesByName(name);

            if (movies.isEmpty()) {
                log.info("No movies found for search term '{}' (took {}ms)", name, System.currentTimeMillis() - startTime);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new MovieApiResponse<>("No matching movies found", null)
                );
            }

            log.info("Found {} movies for search term '{}' (took {}ms)",
                    movies.size(), name, System.currentTimeMillis() - startTime);
            return ResponseEntity.ok(
                    new MovieApiResponse<>("Movies retrieved successfully", movies)
            );
        } catch (Exception e) {
            log.error("Search failed for term '{}': {}", name, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    new MovieApiResponse<>("Failed to search movies", null)
            );
        }

    }

}