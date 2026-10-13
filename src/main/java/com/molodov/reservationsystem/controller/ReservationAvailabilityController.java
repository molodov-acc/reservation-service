package com.molodov.reservationsystem.controller;

import com.molodov.reservationsystem.enums.AvailabilityStatus;
import com.molodov.reservationsystem.dto.CheckAvailabilityRequest;
import com.molodov.reservationsystem.dto.CheckAvailabilityResponse;
import com.molodov.reservationsystem.service.ReservationAvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservation/availability")
@RequiredArgsConstructor
@Slf4j
public class ReservationAvailabilityController {
    private final ReservationAvailabilityService reservationAvailabilityService;

    @PostMapping("/check")
    public ResponseEntity<CheckAvailabilityResponse> checkAvailability(
            @Valid @RequestBody CheckAvailabilityRequest request
    ) {
        log.info("Called method checkAvailability: requestt={}", request);

        var isAvailable = reservationAvailabilityService.isReservationAvailable(
                request.roomId(),
                request.startDate(),
                request.endDate()

        );

        var message = isAvailable
                ? "Room available to reservation"
                : "Room not available to reservation";

        var status = isAvailable
                ? AvailabilityStatus.AVAILABLE
                : AvailabilityStatus.RESERVED;


        return ResponseEntity.status(200).body(new CheckAvailabilityResponse(message, status));
    }
}
