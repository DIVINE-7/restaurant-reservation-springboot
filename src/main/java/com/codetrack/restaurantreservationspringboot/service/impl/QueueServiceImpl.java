
package com.codetrack.restaurantreservationspringboot.service.impl;

import com.codetrack.restaurantreservationspringboot.entity.*;
import com.codetrack.restaurantreservationspringboot.repository.*;
import com.codetrack.restaurantreservationspringboot.service.QueueService;
import com.codetrack.restaurantreservationspringboot.service.TableAvailabilityService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class QueueServiceImpl implements QueueService {

    private final QueueEntryRepository queueRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantTableRepository tableRepository;
    private final TableAvailabilityService availabilityService;

    public QueueServiceImpl(
            QueueEntryRepository queueRepository,
            CustomerRepository customerRepository,
            RestaurantRepository restaurantRepository,
            RestaurantTableRepository tableRepository,
            TableAvailabilityService availabilityService) {

        this.queueRepository = queueRepository;
        this.customerRepository = customerRepository;
        this.restaurantRepository = restaurantRepository;
        this.tableRepository = tableRepository;
        this.availabilityService = availabilityService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<QueueEntry> getAllRestaurantQueue(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Restaurant not found"
            );
        }

        return queueRepository
                .findByRestaurantIdOrderByJoinedAtAsc(restaurantId);
    }

    @Override
    @Transactional
    public QueueEntry assignTable(Long queueId, Long tableId) {
        QueueEntry entry = queueRepository.findById(queueId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Queue entry not found"
                ));

        if (entry.getStatus() != QueueStatus.WAITING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A table can only be assigned to a waiting queue entry"
            );
        }

        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Table not found"
                ));

        if (!table.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Table is inactive"
            );
        }

        if (!table.getRestaurant().getId()
                .equals(entry.getRestaurant().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Table belongs to a different restaurant"
            );
        }

        if (table.getCapacity() < entry.getGuests()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Table capacity is insufficient"
            );
        }

        if (!availabilityService.isTableAvailable(tableId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Table is not available"
            );
        }

        entry.setTable(table);

        return queueRepository.save(entry);
    }

    @Override
    @Transactional
    public QueueEntry joinQueue(
            Long customerId,
            Long restaurantId,
            int guests) {

        if (guests < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Guests must be at least 1"
            );
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found"
                ));

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Restaurant not found"
                ));

        QueueEntry entry = new QueueEntry();
        entry.setCustomer(customer);
        entry.setRestaurant(restaurant);
        entry.setGuests(guests);
        entry.setStatus(QueueStatus.WAITING);

        return queueRepository.save(entry);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QueueEntry> getRestaurantQueue(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Restaurant not found"
            );
        }

        return queueRepository
                .findByRestaurantIdAndStatusOrderByJoinedAtAsc(
                        restaurantId,
                        QueueStatus.WAITING
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<QueueEntry> getCustomerQueue(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Customer not found"
            );
        }

        return queueRepository
                .findByCustomerIdOrderByJoinedAtDesc(customerId);
    }

    @Override
    @Transactional
    public QueueEntry updateQueueStatus(
            Long queueId,
            QueueStatus status) {

        QueueEntry entry = queueRepository.findById(queueId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Queue entry not found"
                ));

        if (status == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status is required"
            );
        }

        QueueStatus current = entry.getStatus();

        boolean allowed = switch (current) {
            case WAITING ->
                    status == QueueStatus.CALLED
                            || status == QueueStatus.CANCELLED;

            case CALLED ->
                    status == QueueStatus.SEATED
                            || status == QueueStatus.CANCELLED;

            case SEATED ->
                    status == QueueStatus.COMPLETED;

            case CANCELLED, COMPLETED -> false;
        };

        if (!allowed) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid queue status transition: "
                            + current + " -> " + status
            );
        }

        if (status == QueueStatus.CALLED && entry.getTable() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Assign a table before calling the customer"
            );
        }

        if (status == QueueStatus.SEATED && entry.getTable() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A table must be assigned before seating the customer"
            );
        }

        entry.setStatus(status);

        return queueRepository.save(entry);
    }
}
