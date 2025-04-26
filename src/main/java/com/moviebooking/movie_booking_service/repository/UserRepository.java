package com.moviebooking.movie_booking_service.repository;


import com.moviebooking.movie_booking_service.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User>  findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);
    boolean existsByEmail(String email);
    void deleteAllByLoginId(String admin);
}
