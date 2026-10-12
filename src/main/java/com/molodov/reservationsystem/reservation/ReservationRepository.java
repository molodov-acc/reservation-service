package com.molodov.reservationsystem.reservation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Отвечает за чтение, сохранение, изменение и удаление бронирований.
 * Содержит запросы к БД, но не должен реализовывать бизнес-сценарии.
 */

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    /***
     *
     * Что это: пользовательский JPQL-запрос для изменения статуса бронирования.
     *
     * За счёт чего работает:
     *
     * @Query задаёт запрос, который Spring Data JPA выполняет при вызове метода репозитория.
     * JPQL работает с Entity (ReservationEntity) и её полями (status, id), а не напрямую с названиями таблиц и колонок БД.
     * :status и :id — именованные параметры, значения которых передаются через @Param.
     * @Modifying сообщает Spring Data JPA, что запрос изменяет данные (UPDATE или DELETE), а не возвращает результат выборки.
     * Spring Data JPA реализует вызов метода репозитория; вручную писать его тело не требуется.
     *
     * Важно: метод обычно вызывают в транзакции — например, из сервисного метода с @Transactional. При использовании @Modifying persistence context может содержать устаревшее состояние Entity, поскольку запрос выполняется напрямую в БД, минуя обычное изменение загруженного объекта.
     *
     * Когда использовать: когда нужно выполнить конкретное обновление в БД, не загружая Entity и не изменяя её поля вручную.
     *
     * Service
     *   ↓ вызывает
     * ReservationRepository.setStatus(id, status)
     *   ↓
     * Spring Data JPA обрабатывает @Query
     *   ↓
     * Hibernate выполняет JPQL, преобразуя его в SQL
     *   ↓
     * UPDATE reservation ...
     *   ↓
     * Database
     *
     * Нюанс: @Modifying сам по себе не открывает транзакцию. Для такого метода транзакцию обычно обеспечивает @Transactional на сервисном методе или репозитории. Если нужно узнать, была ли обновлена запись, удобно возвращать int вместо void — это количество затронутых строк.
     *
     * @param id
     * @param reservationStatus
     */
    @Modifying
    @Query("""
            update ReservationEntity r set r.status = :status
            where r.id = :id
            """)
    void setStatus(
            @Param("id") Long id,
            @Param("status") ReservationStatus reservationStatus

    );

    @Query("""
            SELECT r FROM ReservationEntity r 
                       WHERE(:roomId IS NULL OR r.roomId = :roomId) 
                       AND (:userId IS NULL OR r.userId = :userId)
            """)
    List<ReservationEntity> searchByFilter(
            @Param("roomId") Long roomId,
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("""
            SELECT r FROM ReservationEntity r\s
                       WHERE(:roomId IS NULL OR r.roomId = :roomId)\s
                       AND (:startDate IS NULL OR r.startDate = :startDate)
                       AND (:endDate IS NULL OR r.endDate= :endDate)
                       AND (:status IS NULL OR r.status = :status)
                                  \s
           \s""")
    List<Long> findConflictReservationIds(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") ReservationStatus reservationStatus
    );
}


