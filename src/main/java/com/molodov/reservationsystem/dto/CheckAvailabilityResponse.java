package com.molodov.reservationsystem.dto;

import com.molodov.reservationsystem.enums.AvailabilityStatus;

public record CheckAvailabilityResponse(
        String message,
        AvailabilityStatus status
) {
}
