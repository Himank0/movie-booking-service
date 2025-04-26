package com.moviebooking.movie_booking_service.controller;

import com.moviebooking.movie_booking_service.entities.Movie;
import com.moviebooking.movie_booking_service.entities.Ticket;
import com.moviebooking.movie_booking_service.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1.0/moviebooking")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/booked-tickets")
    public ResponseEntity<List<Ticket>> viewBookedTickets(
            @RequestParam String movieName,
            @RequestParam String theatreName) {
        return ResponseEntity.ok(adminService.getBookedTickets(movieName, theatreName));
    }

    @PutMapping("/update-balance")
    public ResponseEntity<Movie> updateBalanceTickets(
            @RequestParam String movieName,
            @RequestParam String theatreName) {
        Movie updatedMovie = adminService.updateBalanceTickets(movieName, theatreName);
        return ResponseEntity.ok(updatedMovie);
    }
}


//package com.moviebooking.movie_booking_service.controller;
//
//import com.moviebooking.movie_booking_service.dto.AdminMovieStatus;
//import com.moviebooking.movie_booking_service.dto.BalanceTicketResponse;
//import com.moviebooking.movie_booking_service.dto.MovieRequest;
//import com.moviebooking.movie_booking_service.dto.MovieResponse;
//import com.moviebooking.movie_booking_service.entities.Movie;
//import com.moviebooking.movie_booking_service.response.MovieApiResponse;
//import com.moviebooking.movie_booking_service.service.MovieService;
//import com.moviebooking.movie_booking_service.service.TicketService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/v1.0/moviebooking")
//public class AdminController {
//
//    private final TicketService ticketService;
//    private final MovieService movieService;
//
//    public AdminController(TicketService ticketService, MovieService movieService) {
//        this.ticketService = ticketService;
//        this.movieService = movieService;
//    }
//
////    @PutMapping("/{moviename}/update/{ticket}")
////    public ResponseEntity<MovieApiResponse> updateTicketStatus(
////            @PathVariable("moviename") String movieName,
////            @PathVariable("ticket") String ticketId,
////            @RequestParam String theatreName) {
////        MovieApiResponse response = ticketService.updateTicketStatus(movieName, theatreName, ticketId);
////        return new ResponseEntity<MovieApiResponse>(response, HttpStatus.OK);
////    }
//
//    @DeleteMapping("/{moviename}/delete/{id}")
//    public ResponseEntity<String> deleteMovie(
//            @PathVariable("moviename") String movieName,
//            @PathVariable("id") String movieId,
//            @RequestParam String theatreName) {
//
//        movieService.deleteMovie(movieName, theatreName, movieId);
//        return ResponseEntity.ok("Movie and related tickets deleted successfully.");
//    }
//
//
////    @PostMapping("/{moviename}/status/{ticket}")
////    public ResponseEntity<AdminMovieStatus> getMovieStatus(
////            @RequestBody MovieRequest movieRequest) {
////        return ResponseEntity.ok(ticketService.getMovieStatus(movieRequest));
////    }
//
//    @PostMapping("/balance-tickets/{theatername}")
//    public ResponseEntity<BalanceTicketResponse> getBalanceTickets(
//            @RequestBody MovieRequest movieRequest,
//            @PathVariable("theatername") String theaterName) {
//        return ResponseEntity.ok(ticketService.getBalanceTickets(movieRequest, theaterName));
//    }
//
//    @GetMapping("/{moviename}/status/{ticket}")
//    public ResponseEntity<AdminMovieStatus> getMovieStatus(
//            @RequestBody MovieRequest movieRequest) {
//        AdminMovieStatus status = ticketService.getMovieStatus(movieRequest);
//        return ResponseEntity.ok(status);
//    }
//
//
//    @GetMapping("/admin/booked-tickets")
//    public ResponseEntity<?> getBookedTickets(
//            @RequestParam String movieName,
//            @RequestParam String theatreName) {
//
//        int bookedCount = ticketRepository
//                .findByMovieNameAndTheatreName(movieName, theatreName)
//                .stream()
//                .mapToInt(Ticket::getNumberOfTickets)
//                .sum();
//
//        return ResponseEntity.ok(Map.of("bookedCount", bookedCount));
//    }
//
//
//
//}
//
