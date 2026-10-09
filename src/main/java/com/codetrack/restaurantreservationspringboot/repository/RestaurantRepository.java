package com.codetrack.restaurantreservationspringboot.repository;

import com.codetrack.restaurantreservationspringboot.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantRepository
        extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByNameContainingIgnoreCase(String name);

    List<Restaurant> findByAddressContainingIgnoreCase(String address);
}