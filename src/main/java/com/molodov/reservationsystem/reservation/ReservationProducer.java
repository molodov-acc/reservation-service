package com.molodov.reservationsystem.reservation;


import com.molodov.reservationsystem.reservation.event.ReservationCancelledEvent;
import com.molodov.reservationsystem.reservation.event.ReservationCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class ReservationProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ReservationProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendReservationCreated(ReservationCreatedEvent event) {

        String message = objectMapper.writeValueAsString(event);

        kafkaTemplate.send("reservation-created", message);
    }

    public void sendReservationCancelled(ReservationCancelledEvent event) {

        String message = objectMapper.writeValueAsString(event);

        kafkaTemplate.send("reservation-cancelled", message);
    }
}
