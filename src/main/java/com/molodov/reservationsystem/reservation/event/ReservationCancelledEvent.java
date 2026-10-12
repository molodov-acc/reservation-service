package com.molodov.reservationsystem.reservation.event;


public record ReservationCancelledEvent(
        Long reservationId,
        Long userId,
        Long roomId
) {
}