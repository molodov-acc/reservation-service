package com.molodov.reservationsystem.model;

import com.molodov.reservationsystem.enums.ReservationStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.time.LocalDate;

/**
 *
 * Представляет бронирование с точки зрения бизнес-логики.
 * Не должна зависеть от деталей HTTP и хранения данных.
 * Конкретная роль зависит от того, как эта модель используется в проекте.
 *
 * это называется Bean Validation — механизм в Java для проверки данных с помощью аннотаций.
 * Сами аннотации вроде @NotNull и @FutureOrPresent называются аннотациями валидации
 *
 * @NotNull - Значение не должно быть null.
 * @FutureOrPresent - Дата должна быть сегодня или в будущем.
 * @Min(1) - Число должно быть не меньше 1.
 * @NotBlank - Строка не должна быть null, пустой или состоять только из пробелов.
 */
public record Reservation(
        @Null Long id,
        @NotNull Long roomId,
        @NotNull Long userId,
        @FutureOrPresent @NotNull LocalDate startDate,
        @FutureOrPresent @NotNull LocalDate endDate,
        ReservationStatus status
) {
}
