package com.codetrack.restaurantreservationspringboot.controller;

import com.codetrack.restaurantreservationspringboot.dto.*;
import com.codetrack.restaurantreservationspringboot.entity.Reservation;
import com.codetrack.restaurantreservationspringboot.enums.ReservationStatus;
import com.codetrack.restaurantreservationspringboot.service.ReservationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    public record StatusRequest(ReservationStatus status) {}

    private ReservationResponse toResponse(Reservation r) {
        return new ReservationResponse(
                r.getId(),
                r.getCustomer().getId(),
                r.getRestaurant().getId(),
                r.getTable() != null ? r.getTable().getId() : null,
                r.getReservationDate(),
                r.getReservationTime(),
                r.getGuests(),
                r.getStatus(),
                r.getQrToken(),
                r.getCreatedAt()
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse create(
            @Valid @RequestBody ReservationRequest request) {
        return toResponse(reservationService.createReservation(request));
    }

    @GetMapping("/{id}")
    public ReservationResponse getById(@PathVariable Long id) {
        return toResponse(reservationService.getReservationById(id));
    }

    @GetMapping("/customer/{customerId}")
    public List<ReservationResponse> getByCustomer(
            @PathVariable Long customerId) {
        return reservationService.getReservationsByCustomer(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PatchMapping("/{id}/status")
    public ReservationResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusRequest request) {

        if (request.status() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Status is required");
        }

        return toResponse(reservationService.updateReservationStatus(
                id, request.status()));
    }

    @PatchMapping("/{id}/cancel")
    public ReservationResponse cancel(@PathVariable Long id) {
        return toResponse(reservationService.cancelReservation(id));
    }
}