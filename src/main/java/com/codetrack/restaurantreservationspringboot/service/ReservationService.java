package com.codetrack.restaurantreservationspringboot.service;

import com.codetrack.restaurantreservationspringboot.dto.ReservationRequest;
import com.codetrack.restaurantreservationspringboot.entity.Reservation;
import com.codetrack.restaurantreservationspringboot.enums.ReservationStatus;

import java.util.List;

public interface ReservationService {

    Reservation createReservation(ReservationRequest request);

    Reservation getReservationById(Long id);

    List<Reservation> getReservationsByCustomer(Long customerId);

    Reservation updateReservationStatus(
            Long id, ReservationStatus status);

    Reservation cancelReservation(Long id);
}