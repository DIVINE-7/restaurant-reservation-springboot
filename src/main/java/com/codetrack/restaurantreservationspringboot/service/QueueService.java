
package com.codetrack.restaurantreservationspringboot.service;

import com.codetrack.restaurantreservationspringboot.entity.QueueEntry;
import com.codetrack.restaurantreservationspringboot.entity.QueueStatus;

import java.util.List;

public interface QueueService {

    QueueEntry joinQueue(Long customerId, Long restaurantId, int guests);

    List<QueueEntry> getRestaurantQueue(Long restaurantId);

    List<QueueEntry> getCustomerQueue(Long customerId);

    QueueEntry updateQueueStatus(Long queueId, QueueStatus status);
}
