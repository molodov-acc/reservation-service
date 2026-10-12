package com.molodov.reservationsystem.web;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        String message,
        String error,
        LocalDateTime errorTime
) {
}

