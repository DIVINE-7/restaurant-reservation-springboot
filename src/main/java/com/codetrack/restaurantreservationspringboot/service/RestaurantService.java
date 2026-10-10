
package com.codetrack.restaurantreservationspringboot.service;

import com.codetrack.restaurantreservationspringboot.entity.Restaurant;
import java.util.List;

public interface RestaurantService {

    List<Restaurant> getAllRestaurants();

    Restaurant getRestaurantById(Long id);

    List<Restaurant> searchRestaurants(String name, String address);

    Restaurant createRestaurant(Restaurant restaurant);

    Restaurant updateRestaurant(Long id, Restaurant restaurant);

    void deleteRestaurant(Long id);
}
