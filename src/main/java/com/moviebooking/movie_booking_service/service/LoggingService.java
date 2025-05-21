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
        sendLogToKafka("INFO", message);
    }

    public void logWarn(String message) {
        sendLogToKafka("WARN", message);
    }

    public void logError(String message) {
        sendLogToKafka("ERROR", message);
    }

    public void logDebug(String message) {
        sendLogToKafka("DEBUG", message);
    }

    private void sendLogToKafka(String level, String message) {
        try {
            String logJson = mapper.writeValueAsString(Map.of(
                    "timestamp", Instant.now().toString(),
                    "level", level,
                    "message", message,
                    "service", "movie-booking"
            ));
            kafkaLogProducer.sendLog(logJson);
        } catch (JsonProcessingException e) {
            // Fallback to console logging if JSON serialization fails
            System.err.println("Failed to serialize log message: " + e.getMessage());
            System.out.println("[" + level + "] " + message);
        }
    }
}