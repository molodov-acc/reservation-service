package com.molodov.reservationsystem.reservation.availability;

public record CheckAvailabilityResponse(
        String message,
        AvailabilityStatus status
) {
}
