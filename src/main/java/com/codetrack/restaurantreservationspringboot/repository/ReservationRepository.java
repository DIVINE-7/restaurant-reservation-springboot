package com.codetrack.restaurantreservationspringboot.repository;

import com.codetrack.restaurantreservationspringboot.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    List<Reservation> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<Reservation> findByRestaurantIdAndReservationDateAndStatusIn(
            Long restaurantId,
            LocalDate reservationDate,
            List<com.codetrack.restaurantreservationspringboot.enums.ReservationStatus> statuses
    );

    boolean existsByQrToken(String qrToken);
}