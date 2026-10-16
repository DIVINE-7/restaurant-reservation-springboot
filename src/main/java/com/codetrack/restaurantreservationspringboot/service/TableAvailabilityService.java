package com.codetrack.restaurantreservationspringboot.service;

import com.codetrack.restaurantreservationspringboot.entity.QueueEntry;
import com.codetrack.restaurantreservationspringboot.entity.QueueStatus;
import com.codetrack.restaurantreservationspringboot.entity.Reservation;
import com.codetrack.restaurantreservationspringboot.enums.ReservationStatus;
import com.codetrack.restaurantreservationspringboot.entity.RestaurantTable;
import com.codetrack.restaurantreservationspringboot.repository.QueueEntryRepository;
import com.codetrack.restaurantreservationspringboot.repository.ReservationRepository;
import com.codetrack.restaurantreservationspringboot.repository.RestaurantTableRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TableAvailabilityService {

    private static final long RESERVATION_DURATION_MINUTES = 120;

    private final RestaurantTableRepository tableRepository;
    private final ReservationRepository reservationRepository;
    private final QueueEntryRepository queueRepository;

    public TableAvailabilityService(
            RestaurantTableRepository tableRepository,
            ReservationRepository reservationRepository,
            QueueEntryRepository queueRepository) {
        this.tableRepository = tableRepository;
        this.reservationRepository = reservationRepository;
        this.queueRepository = queueRepository;
    }

    public Set<Long> getAvailableTableIds() {
        LocalDateTime now = LocalDateTime.now();
        Set<Long> busyTableIds = new HashSet<>();

        for (Reservation reservation : reservationRepository.findAll()) {
            if (reservation.getTable() == null) {
                continue;
            }

            ReservationStatus status = reservation.getStatus();

            if (status == ReservationStatus.CHECKED_IN) {
                busyTableIds.add(reservation.getTable().getId());
                continue;
            }

            boolean activeReservation =
                    status == ReservationStatus.PENDING
                            || status == ReservationStatus.CONFIRMED;

            if (!activeReservation) {
                continue;
            }

            LocalDateTime start = reservation.getReservationDate()
                    .atTime(reservation.getReservationTime());

            LocalDateTime end = start.plusMinutes(
                    RESERVATION_DURATION_MINUTES);

            if (!now.isBefore(start) && now.isBefore(end)) {
                busyTableIds.add(reservation.getTable().getId());
            }
        }

        for (QueueEntry entry : queueRepository.findAll()) {
            if (entry.getTable() == null) {
                continue;
            }

            if (entry.getStatus() == QueueStatus.CALLED
                    || entry.getStatus() == QueueStatus.SEATED) {
                busyTableIds.add(entry.getTable().getId());
            }
        }

        return tableRepository.findByActiveTrue()
                .stream()
                .map(RestaurantTable::getId)
                .filter(id -> !busyTableIds.contains(id))
                .collect(Collectors.toSet());
    }

    public boolean isTableAvailable(Long tableId) {
        return getAvailableTableIds().contains(tableId);
    }
}