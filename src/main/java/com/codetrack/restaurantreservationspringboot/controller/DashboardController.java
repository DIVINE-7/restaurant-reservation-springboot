package com.codetrack.restaurantreservationspringboot.controller;

import com.codetrack.restaurantreservationspringboot.entity.QueueStatus;
import com.codetrack.restaurantreservationspringboot.enums.ReservationStatus;
import com.codetrack.restaurantreservationspringboot.repository.QueueEntryRepository;
import com.codetrack.restaurantreservationspringboot.repository.ReservationRepository;
import com.codetrack.restaurantreservationspringboot.service.TableAvailabilityService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final ReservationRepository reservationRepository;
    private final QueueEntryRepository queueRepository;
    private final TableAvailabilityService availabilityService;

    public DashboardController(
            ReservationRepository reservationRepository,
            QueueEntryRepository queueRepository,
            TableAvailabilityService availabilityService) {
        this.reservationRepository = reservationRepository;
        this.queueRepository = queueRepository;
        this.availabilityService = availabilityService;
    }

    public record DashboardSummary(
            long totalReservations,
            long pendingReservations,
            long availableTables,
            long currentQueueCount) {}

    @GetMapping("/summary")
    @Transactional(readOnly = true)
    public DashboardSummary getSummary() {
        return new DashboardSummary(
                reservationRepository.count(),
                reservationRepository.countByStatus(
                        ReservationStatus.PENDING),
                availabilityService.getAvailableTableIds().size(),
                queueRepository.countByStatus(QueueStatus.WAITING)
        );
    }
}