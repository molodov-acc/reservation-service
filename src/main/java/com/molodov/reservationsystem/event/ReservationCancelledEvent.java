package com.molodov.reservationsystem.event;


public record ReservationCancelledEvent(
        Long reservationId,
        Long userId,
        Long roomId
) {
}