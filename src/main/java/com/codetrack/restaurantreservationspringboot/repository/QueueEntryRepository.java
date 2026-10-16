package com.codetrack.restaurantreservationspringboot.repository;

import com.codetrack.restaurantreservationspringboot.entity.QueueEntry;
import com.codetrack.restaurantreservationspringboot.entity.QueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QueueEntryRepository
        extends JpaRepository<QueueEntry, Long> {

    List<QueueEntry> findByRestaurantIdAndStatusOrderByJoinedAtAsc(
            Long restaurantId, QueueStatus status);

    List<QueueEntry> findByRestaurantIdOrderByJoinedAtAsc(
            Long restaurantId);

    List<QueueEntry> findByCustomerIdOrderByJoinedAtDesc(
            Long customerId);

    long countByStatus(QueueStatus status);
}