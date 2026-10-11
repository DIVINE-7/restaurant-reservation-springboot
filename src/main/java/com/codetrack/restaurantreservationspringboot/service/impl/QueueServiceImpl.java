
package com.codetrack.restaurantreservationspringboot.service.impl;

import com.codetrack.restaurantreservationspringboot.entity.*;
import com.codetrack.restaurantreservationspringboot.repository.*;
import com.codetrack.restaurantreservationspringboot.service.QueueService;

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

    public QueueServiceImpl(
            QueueEntryRepository queueRepository,
            CustomerRepository customerRepository,
            RestaurantRepository restaurantRepository) {
        this.queueRepository = queueRepository;
        this.customerRepository = customerRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    @Transactional
    public QueueEntry joinQueue(
            Long customerId, Long restaurantId, int guests) {

        if (guests < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Guests must be at least 1"
            );
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Customer not found"));

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Restaurant not found"));

        QueueEntry entry = new QueueEntry();
        entry.setCustomer(customer);
        entry.setRestaurant(restaurant);
        entry.setGuests(guests);
        entry.setStatus(QueueStatus.WAITING);

        return queueRepository.save(entry);
    }

    @Override
    public List<QueueEntry> getRestaurantQueue(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Restaurant not found");
        }

        return queueRepository
                .findByRestaurantIdAndStatusOrderByJoinedAtAsc(
                        restaurantId, QueueStatus.WAITING);
    }

    @Override
    public List<QueueEntry> getCustomerQueue(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Customer not found");
        }

        return queueRepository
                .findByCustomerIdOrderByJoinedAtDesc(customerId);
    }

    @Override
    @Transactional
    public QueueEntry updateQueueStatus(
            Long queueId, QueueStatus status) {

        QueueEntry entry = queueRepository.findById(queueId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Queue entry not found"));

        if (status == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Status is required");
        }

        entry.setStatus(status);
        return queueRepository.save(entry);
    }
}
