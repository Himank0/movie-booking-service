package com.moviebooking.movie_booking_service.controller;

import com.moviebooking.movie_booking_service.dto.MovieResponse;
import com.moviebooking.movie_booking_service.entities.Movie;
import com.moviebooking.movie_booking_service.response.MovieApiResponse;
import com.moviebooking.movie_booking_service.service.LoggingService;
import com.moviebooking.movie_booking_service.service.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private LoggingService loggingService;


    @GetMapping("/all")
    public ResponseEntity<?> getAllMovies() {
        long startTime = System.currentTimeMillis();
        List<MovieResponse> movies = movieService.getAllMovies();
        if (movies.isEmpty()) {
            return ResponseEntity.ok(new MovieApiResponse("No movies available for booking.", movies));
        }
        loggingService.logInfo("Returning " + movies.size() + " movies (took " + (System.currentTimeMillis() - startTime) + "ms)");
        return ResponseEntity.ok(new MovieApiResponse("Movies fetched successfully", movies));
    }

    @GetMapping("/movies/search/{moviename}")
    public ResponseEntity<?> searchMovies(@PathVariable("moviename") String name) {
        List<MovieResponse> movies = movieService.searchMoviesByName(name);
        if (movies.isEmpty()) {
            return ResponseEntity.ok(new MovieApiResponse("No matching movie found.", movies));
        }
        return ResponseEntity.ok(new MovieApiResponse("Movie search successful", movies));
    }

}