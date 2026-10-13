package com.molodov.reservationsystem.service;

import com.molodov.notification.grpc.NotificationServiceGrpc;
import com.molodov.notification.grpc.ReservationCancelledRequest;
import com.molodov.reservationsystem.dto.ReservationSearchFilter;
import com.molodov.reservationsystem.entity.ReservationEntity;
import com.molodov.reservationsystem.enums.ReservationStatus;
import com.molodov.reservationsystem.integration.kafka.ReservationProducer;
import com.molodov.reservationsystem.mapper.ReservationMapper;
import com.molodov.reservationsystem.model.Reservation;
import com.molodov.reservationsystem.repository.ReservationRepository;
import com.molodov.reservationsystem.event.ReservationCreatedEvent;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {
    private final ReservationProducer reservationProducer;
    private final ReservationRepository repository;
    private final ReservationMapper reservationMapper;
    private final NotificationServiceGrpc.NotificationServiceBlockingStub notificationStub;

    public List<Reservation> getAllReservation() {
        return repository.findAll().stream()
                .map(reservationMapper::toDomainReservation).toList();
    }

    public List<Reservation> searchByFilter(ReservationSearchFilter filter) {
        var pageSize = filter.pageSize() != null ? filter.pageSize() : 10;
        var pageNumber = filter.pageNumber() != null ? filter.pageNumber() : 0;
        var pageable = Pageable.ofSize(pageSize).withPage(pageNumber);

        return repository.searchByFilter(
                        filter.roomId(),
                        filter.userId(),
                        pageable
                ).stream()
                .map(reservationMapper::toDomainReservation)
                .toList();

    }

    public Reservation getById(Long id) {

        ReservationEntity reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("not found" + id));

        return reservationMapper.toDomainReservation(reservationEntity);

    }

    public Reservation createReservation(Reservation reservationToCreate) {
        if (reservationToCreate.status() != null) {
            throw new IllegalArgumentException("status должен быть пустым");
        }

        var entityToSave = reservationMapper.toReservationEntity(reservationToCreate);
        entityToSave.setId(null);
        entityToSave.setStatus(ReservationStatus.PENDING);

        var savedEntity = repository.save(entityToSave);

        var savedReservation = reservationMapper.toDomainReservation(savedEntity);

        var event = new ReservationCreatedEvent(
                savedReservation.id(),
                savedReservation.roomId(),
                savedReservation.startDate(),
                savedReservation.endDate()
        );

        reservationProducer.sendReservationCreated(event);

        return savedReservation;
    }

    @Transactional
    public Reservation reservationUpdate(
            Long id,
            Reservation reservationToUpdate
    ) {
        var reservationEntity = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Нет такого бронирования: " + id
                        )
                );

        if (reservationEntity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException(
                    "Не можем изменить бронирование со статусом: "
                            + reservationEntity.getStatus()
            );
        }

        reservationMapper.updateEntity(
                reservationToUpdate,
                reservationEntity
        );

        if (reservationEntity.getStartDate() == null
                || reservationEntity.getEndDate() == null
                || !reservationEntity.getEndDate()
                .isAfter(reservationEntity.getStartDate())) {
            throw new IllegalArgumentException(
                    "Дата окончания должна быть позже даты начала"
            );
        }

        return reservationMapper.toDomainReservation(reservationEntity);
    }

    @Transactional
    public void cancelReservation(Long id) {
        var reservationEntity = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("нет такого бронирования " + id)
                );

        var reservation = reservationMapper.toDomainReservation(reservationEntity);

        repository.setStatus(id, ReservationStatus.CANCELED);
/// kafka
//        var event = new ReservationCancelledEvent(
//                reservation.id(),
//                reservation.userId(),
//                reservation.roomId()
//        );
//
//        reservationProducer.sendReservationCancelled(event);

        var request = ReservationCancelledRequest.newBuilder()
                .setReservationId(reservation.id())
                .setUserId(reservation.userId())
                .setRoomId(reservation.roomId())
                .build();

        var response = notificationStub.notifyReservationCancelled(request);

        log.info(
                "gRPC response: accepted={}, message={}",
                response.getAccepted(),
                response.getMessage()
        );

        log.info("Успешная отмена бронирования");

    }
}
