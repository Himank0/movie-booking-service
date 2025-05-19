package com.moviebooking.movie_booking_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class LoggingService {

    @Autowired
    private KafkaLogProducer kafkaLogProducer;

    private final ObjectMapper mapper = new ObjectMapper();

    public void logInfo(String message) {
        try {
            String logJson = mapper.writeValueAsString(Map.of(
                    "timestamp", Instant.now().toString(),
                    "level", "INFO",
                    "message", message,
                    "service", "movie-booking"
            ));
            kafkaLogProducer.sendLog(logJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}

