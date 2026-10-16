
package com.codetrack.restaurantreservationspringboot.controller;

import com.codetrack.restaurantreservationspringboot.entity.QueueEntry;
import com.codetrack.restaurantreservationspringboot.entity.QueueStatus;
import com.codetrack.restaurantreservationspringboot.service.QueueService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/queue")
public class QueueController {

    public record AssignTableRequest(Long tableId) {}
    @GetMapping("/restaurant/{restaurantId}/all")
    public List<QueueEntry> getAllRestaurantQueue(
            @PathVariable Long restaurantId) {
        return queueService.getAllRestaurantQueue(restaurantId);
    }

    @PatchMapping("/{queueId}/table")
    public QueueEntry assignTable(
            @PathVariable Long queueId,
            @RequestBody AssignTableRequest request) {

        if (request.tableId() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Table ID is required");
        }

        return queueService.assignTable(queueId, request.tableId());
    }
    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

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
        if (request.customerId() == null ||
                request.restaurantId() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
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

    @GetMapping("/restaurant/{restaurantId}")
    public List<QueueEntry> getRestaurantQueue(
            @PathVariable Long restaurantId) {
        return queueService.getRestaurantQueue(restaurantId);
    }

    @GetMapping("/customer/{customerId}")
    public List<QueueEntry> getCustomerQueue(
            @PathVariable Long customerId) {
        return queueService.getCustomerQueue(customerId);
    }

    @PatchMapping("/{queueId}/status")
    public QueueEntry updateStatus(
            @PathVariable Long queueId,
            @RequestBody UpdateQueueStatusRequest request) {
        return queueService.updateQueueStatus(
                queueId, request.status());
    }
}
