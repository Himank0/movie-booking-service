package com.moviebooking.movie_booking_service.config;

import com.moviebooking.movie_booking_service.entities.Movie;
import com.moviebooking.movie_booking_service.entities.User;

import com.moviebooking.movie_booking_service.repository.MovieRepository;
import com.moviebooking.movie_booking_service.repository.TicketRepository;
import com.moviebooking.movie_booking_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initMovies(MovieRepository movieRepository, TicketRepository ticketRepository, UserRepository userRepository) {
         BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return (args) -> {
            movieRepository.deleteAll(); // Optional: Clear old data during dev
            ticketRepository.deleteAll();
            userRepository.deleteAllByLoginId("admin");

            if (!userRepository.existsByLoginId("admin")) {
                User admin = new User(
                        "Admin",
                        "User",
                        "admin@example.com",
                        "admin",
                        passwordEncoder.encode("admin@123"),
                        "ADMIN",
                        "9999999999"
                );

                userRepository.save(admin);
            }

            Movie m1 = new Movie("Inception", "PVR Cinemas", 100, 100, "BOOK ASAP", "https://play-lh.googleusercontent.com/buKf27Hxendp3tLNpNtP3E-amP0o4yYV-SGKyS2u-Y3GdGRTyfNCIT5WAVs2OudOz6so5K1jtYdAUKI9nw8");
            Movie m2 = new Movie("Inception", "INOX", 120, 120, "BOOK ASAP", "https://play-lh.googleusercontent.com/buKf27Hxendp3tLNpNtP3E-amP0o4yYV-SGKyS2u-Y3GdGRTyfNCIT5WAVs2OudOz6so5K1jtYdAUKI9nw8");
            Movie m3 = new Movie("Interstellar", "PVR Cinemas", 90, 90, "BOOK ASAP", "https://i.ytimg.com/vi/YF1eYbfbH5k/maxresdefault.jpg?sqp=-oaymwEmCIAKENAF8quKqQMa8AEB-AH-CYAC0AWKAgwIABABGE4gYShlMA8=&rs=AOn4CLDt6JPoesvmQnP8qf-00JpeDZUfyA");
            Movie m4 = new Movie("Interstellar", "INOX", 110, 110, "BOOK ASAP", "https://i.ytimg.com/vi/YF1eYbfbH5k/maxresdefault.jpg?sqp=-oaymwEmCIAKENAF8quKqQMa8AEB-AH-CYAC0AWKAgwIABABGE4gYShlMA8=&rs=AOn4CLDt6JPoesvmQnP8qf-00JpeDZUfyA");

            movieRepository.save(m1);
            movieRepository.save(m2);
            movieRepository.save(m3);
            movieRepository.save(m4);

            log.info("Sample movies initialized in MongoDB.");
        };
    }
}
