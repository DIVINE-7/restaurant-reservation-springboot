package com.codetrack.restaurantreservationspringboot.repository;

import com.codetrack.restaurantreservationspringboot.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import com.codetrack.restaurantreservationspringboot.enums.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    long countByStatus(ReservationStatus status);
    List<Reservation> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<Reservation> findByRestaurantIdAndReservationDateAndStatusIn(
            Long restaurantId,
            LocalDate reservationDate,
            List<com.codetrack.restaurantreservationspringboot.enums.ReservationStatus> statuses
    );

    boolean existsByQrToken(String qrToken);
}