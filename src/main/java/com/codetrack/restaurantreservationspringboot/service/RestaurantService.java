package com.codetrack.restaurantreservationspringboot.service;

import com.codetrack.restaurantreservationspringboot.entity.Restaurant;

import java.util.List;

public interface RestaurantService {

    List<Restaurant> getAll(String search);

    Restaurant getById(Long id);

    Restaurant create(Restaurant restaurant);

    Restaurant update(Long id, Restaurant restaurant);

    void delete(Long id);
}