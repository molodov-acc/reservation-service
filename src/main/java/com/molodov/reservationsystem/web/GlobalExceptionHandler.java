package com.molodov.reservationsystem.web;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/***
 *
 * @ControllerAdvice в Spring — это механизм, который позволяет обрабатывать исключения из разных контроллеров в одном месте, вместо того чтобы писать try-catch в каждом endpoint.
 *
 * Представь, что у тебя 20 контроллеров. В каждом может произойти ошибка: ресурс не найден, запрос некорректный, конфликт состояния или внутренняя ошибка сервера. Не хочется, чтобы каждый контроллер самостоятельно превращал исключения в HTTP-ответы.
 *
 * Для этого и нужен глобальный обработчик исключений.
 *
 * 1. Как это работает
 *
 * Допустим, запрос проходит такую цепочку:
 *
 * HTTP request
 *      ↓
 * ReservationController
 *      ↓
 * ReservationService
 *      ↓
 * ReservationRepository
 *      ↓
 * Ошибка: ReservationNotFoundException
 *      ↓
 * Spring ищет подходящий exception handler
 *      ↓
 * GlobalExceptionHandler (@ControllerAdvice)
 *      ↓
 * HTTP 404 + JSON
 *
 *
 */
@ControllerAdvice // Объявляет общий механизм обработки исключений и других задач MVC для контроллеров.
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class) //Указывает, какие типы исключений обрабатывает конкретный метод.
    public ResponseEntity<ErrorResponseDto> handleEntityNotFound(
            EntityNotFoundException e
    ) {
        var errorDto = new ErrorResponseDto(
                "EntityNotFoundException",
                e.getMessage(),
                LocalDateTime.now()
        );

        log.error("EntityNotFoundException", errorDto.message());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
    }

    @ExceptionHandler(exception = {
            IllegalStateException.class,
            IllegalStateException.class,
            MethodArgumentNotValidException.class // ошибки валидации тоже здесь
    })
    public ResponseEntity<ErrorResponseDto> handleBadRequest(
            Exception e
    ) {
        var errorDto = new ErrorResponseDto(
                "IllegalStateException",
                e.getMessage(),
                LocalDateTime.now()
        );

        log.error("IllegalStateException", errorDto.message());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception e
    ) {
        var errorDto = new ErrorResponseDto(
                "Внутренняя ошибка сервера",
                e.getMessage(),
                LocalDateTime.now()
        );

        log.error("Handle exceprion", errorDto.message());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDto);
    }

}
