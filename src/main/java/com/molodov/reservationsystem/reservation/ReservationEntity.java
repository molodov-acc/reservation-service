package com.molodov.reservationsystem.reservation;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Сущность для hibernate
 * Создаст таблицу с указанным именем и колонками
 * Пустой конструктор обязателен
 *
 * Описывает структуру бронирования для JPA и связь с таблицей базы данных.
 * Содержит поля и настройки ORM-маппинга.
 */

@Table(name = "reservations")
@Entity
public class ReservationEntity {
    @Column(name = "user_id")
    public Long userId;
    @Column(name = "room_id")
    public Long roomId;
    @Column(name = "start_date")
    public LocalDate startDate;
    @Column(name = "end_date")
    public LocalDate endDate;
    @Column(name = "status")
    @Enumerated(EnumType.STRING) // указывает JPA сохранять в базе текстовое значение, например PENDING, а не числовой индекс 0.
    public ReservationStatus status;
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY) // бд сама будет генерить id для сущности
    private Long id;


    public ReservationEntity() {
    }

    public ReservationEntity(Long id, Long userId, Long roomId, LocalDate startDate, LocalDate endDate, ReservationStatus status) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}
