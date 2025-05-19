package com.moviebooking.movie_booking_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaLogProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "spring-boot-logs";

    public void sendLog(String message) {
        kafkaTemplate.send(TOPIC, message);
    }
}

