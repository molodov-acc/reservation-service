package com.molodov.reservationsystem.dto;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        String message,
        String error,
        LocalDateTime errorTime
) {
}

