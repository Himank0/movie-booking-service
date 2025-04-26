package com.moviebooking.movie_booking_service.repository;

import com.moviebooking.movie_booking_service.entities.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;


public interface MovieRepository extends MongoRepository<Movie, String> {

    Optional<Movie> findByMovieNameIgnoreCaseAndTheatreNameIgnoreCase(String movieName, String theatreName);
    List<Movie> findByMovieNameContainingIgnoreCase(String movieName);

}
