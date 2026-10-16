
package com.codetrack.restaurantreservationspringboot.controller;

import com.codetrack.restaurantreservationspringboot.entity.QueueEntry;
import com.codetrack.restaurantreservationspringboot.entity.QueueStatus;
import com.codetrack.restaurantreservationspringboot.service.QueueService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/queue")
public class QueueController {

    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    public record AssignTableRequest(Long tableId) {}

    public record JoinQueueRequest(
            Long customerId,
            Long restaurantId,
            int guests
    ) {}

    public record UpdateQueueStatusRequest(
            QueueStatus status
    ) {}

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.CREATED)
    public QueueEntry joinQueue(@RequestBody JoinQueueRequest request) {
        if (request == null
                || request.customerId() == null
                || request.restaurantId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Customer ID and restaurant ID are required"
            );
        }

        return queueService.joinQueue(
                request.customerId(),
                request.restaurantId(),
                request.guests()
        );
    }

    // Returns only waiting queue entries.
    @GetMapping("/restaurant/{restaurantId}")
    public List<QueueEntry> getRestaurantQueue(
            @PathVariable Long restaurantId) {

        return queueService.getRestaurantQueue(restaurantId);
    }

    // Returns all queue entries for a restaurant.
    @GetMapping("/restaurant/{restaurantId}/all")
    public List<QueueEntry> getAllRestaurantQueue(
            @PathVariable Long restaurantId) {

        return queueService.getAllRestaurantQueue(restaurantId);
    }

    // Returns the customer's queue history.
    @GetMapping("/customer/{customerId}")
    public List<QueueEntry> getCustomerQueue(
            @PathVariable Long customerId) {

        return queueService.getCustomerQueue(customerId);
    }

    // Updates the queue status.
    @PatchMapping("/{queueId}/status")
    public QueueEntry updateStatus(
            @PathVariable Long queueId,
            @RequestBody UpdateQueueStatusRequest request) {

        if (request == null || request.status() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status is required"
            );
        }

        return queueService.updateQueueStatus(
                queueId,
                request.status()
        );
    }

    // Assigns a table using its database ID.
    @PatchMapping("/{queueId}/table")
    public QueueEntry assignTable(
            @PathVariable Long queueId,
            @RequestBody AssignTableRequest request) {

        if (request == null || request.tableId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Table ID is required"
            );
        }

        return queueService.assignTable(
                queueId,
                request.tableId()
        );
    }
}
