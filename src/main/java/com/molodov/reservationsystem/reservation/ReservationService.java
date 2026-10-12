package com.molodov.reservationsystem.reservation;

import com.molodov.notification.grpc.NotificationServiceGrpc;
import com.molodov.notification.grpc.ReservationCancelledRequest;
import com.molodov.reservationsystem.reservation.event.ReservationCreatedEvent;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {
    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);
    private final ReservationProducer reservationProducer;
    private final ReservationRepository repository;
    private final ReservationMapper reservationMapper;
    private final NotificationServiceGrpc.NotificationServiceBlockingStub notificationStub;

    public ReservationService(
            ReservationProducer reservationProducer,
            ReservationRepository repository,
            ReservationMapper reservationMapper,
            GrpcChannelFactory channelFactory
    ) {
        this.reservationProducer = reservationProducer;
        this.repository = repository;
        this.reservationMapper = reservationMapper;

        this.notificationStub = NotificationServiceGrpc.newBlockingStub(
                //channelFactory.createChannel("notification-service")
                channelFactory.createChannel("static://localhost:9090")
        );
    }

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

    public Reservation reservationUpdate(Long id, Reservation reservationToUpdate) {
        var reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("нет такого бронирования" + id));

        if (reservationEntity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("не можем изменить бронирование со статусом" + reservationEntity.getStatus());
        }
        if (!reservationEntity.getEndDate().isAfter(reservationEntity.getStartDate())) {
            throw new IllegalArgumentException("Start date must be 1 day earlier than endDate");
        }


        var reservationToSave = reservationMapper.toReservationEntity(reservationToUpdate);
        reservationToSave.setId(reservationEntity.getId());
        reservationToSave.setStatus(ReservationStatus.PENDING);

        var updatedReservation = repository.save(reservationToSave);

        return reservationMapper.toDomainReservation(updatedReservation);
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
