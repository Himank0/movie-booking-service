package com.moviebooking.movie_booking_service.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.transaction.annotation.Transactional;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Transactional
public class UserApiResponse<T> {

    @JsonProperty("message")
    private String message;
    @JsonProperty("data")
    private T data;
    @JsonProperty("httpStatus")
    int httpStatusCode;

    public UserApiResponse(String message, T data, int httpStatusCode) {
        this.message = message;
        this.data = data;
        this.httpStatusCode = httpStatusCode;
    }

}
