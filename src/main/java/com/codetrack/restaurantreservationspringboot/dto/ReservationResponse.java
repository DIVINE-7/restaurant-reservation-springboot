package com.codetrack.restaurantreservationspringboot.dto;

import com.codetrack.restaurantreservationspringboot.enums.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        Long customerId,
        Long restaurantId,
        Long tableId,
        LocalDate reservationDate,
        LocalTime reservationTime,
        int guests,
        ReservationStatus status,
        String qrToken,
        LocalDateTime createdAt
) {}