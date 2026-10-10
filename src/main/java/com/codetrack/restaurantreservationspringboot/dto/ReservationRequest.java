package com.codetrack.restaurantreservationspringboot.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationRequest(
        @NotNull Long customerId,
        @NotNull Long restaurantId,
        Long tableId,
        @NotNull LocalDate reservationDate,
        @NotNull LocalTime reservationTime,
        @Min(1) int guests
) {}