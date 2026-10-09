package com.codetrack.restaurantreservationspringboot.repository;

import com.codetrack.restaurantreservationspringboot.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantTableRepository
        extends JpaRepository<RestaurantTable, Long> {

    List<RestaurantTable> findByRestaurantIdAndActiveTrue(Long restaurantId);
}