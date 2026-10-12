package com.molodov.reservationsystem.reservation;

import org.springframework.stereotype.Component;

/**
 * Mapper — преобразует объекты между доменной моделью Reservation
 * и JPA-сущностью ReservationEntity.
 * <p>
 * Зачем нужен:
 * * отделяет доменную модель от модели хранения данных;
 * * избавляет сервисы от ручного копирования полей;
 * * упрощает изменение Entity без обязательного изменения доменной модели.
 * <p>
 * toDomainReservation() — Entity -> Domain: для работы бизнес-логики.
 * toReservationEntity() — Domain -> Entity: для сохранения через Repository.
 * <p>
 * Mapper не содержит бизнес-логики и не работает с БД напрямую.
 */


@Component
public class ReservationMapper {
    public Reservation toDomainReservation(ReservationEntity entity) {
        return new Reservation(
                entity.getId(),
                entity.getRoomId(),
                entity.getUserId(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getStatus()
        );
    }

    public ReservationEntity toReservationEntity(Reservation entity) {
        return new ReservationEntity(
                entity.id(),
                entity.roomId(),
                entity.userId(),
                entity.startDate(),
                entity.endDate(),
                entity.status()
        );
    }
}
