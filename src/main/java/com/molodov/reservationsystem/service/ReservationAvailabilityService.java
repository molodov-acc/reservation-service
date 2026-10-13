package com.molodov.reservationsystem.service;

import com.molodov.reservationsystem.repository.ReservationRepository;
import com.molodov.reservationsystem.enums.ReservationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationAvailabilityService {
    private final ReservationRepository repository;

    public boolean isReservationAvailable(
            Long roomId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("Start date must be 1 day earlier than endDate");
        }

        List<Long> conflictingIds = repository.findConflictReservationIds(
                roomId, startDate, endDate, ReservationStatus.APPROVED
        );

        if (conflictingIds.isEmpty()) {
            return true;
        }

        log.info("Conflicting with ids={}", conflictingIds);
        return false;
    }
}
