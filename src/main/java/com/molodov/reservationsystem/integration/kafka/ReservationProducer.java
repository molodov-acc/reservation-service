package com.molodov.reservationsystem.integration.kafka;

import com.molodov.reservationsystem.event.ReservationCancelledEvent;
import com.molodov.reservationsystem.event.ReservationCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ReservationProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendReservationCreated(ReservationCreatedEvent event) {

        String message = objectMapper.writeValueAsString(event);

        kafkaTemplate.send("reservation-created", message);
    }

    public void sendReservationCancelled(ReservationCancelledEvent event) {

        String message = objectMapper.writeValueAsString(event);

        kafkaTemplate.send("reservation-cancelled", message);
    }
}
