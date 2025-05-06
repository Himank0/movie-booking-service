package com.moviebooking.movie_booking_service.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Transactional
public class MovieApiResponse<T> {

    @JsonProperty("message")
    private String message;
    @JsonProperty("data")
    private T data;

    public MovieApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

}