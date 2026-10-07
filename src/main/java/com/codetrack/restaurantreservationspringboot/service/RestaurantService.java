package com.codetrack.restaurantreservationspringboot.service;

import com.codetrack.restaurantreservationspringboot.entity.Restaurant;
import com.codetrack.restaurantreservationspringboot.repository.RestaurantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public Restaurant createRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public Optional<Restaurant> getRestaurantById(Long id) {
        return restaurantRepository.findById(id);
    }

    public Restaurant updateRestaurant(Long id, Restaurant updatedRestaurant) {

        Restaurant existingRestaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        existingRestaurant.setName(updatedRestaurant.getName());
        existingRestaurant.setAddress(updatedRestaurant.getAddress());
        existingRestaurant.setPhone(updatedRestaurant.getPhone());
        existingRestaurant.setDescription(updatedRestaurant.getDescription());
        existingRestaurant.setOpeningTime(updatedRestaurant.getOpeningTime());
        existingRestaurant.setClosingTime(updatedRestaurant.getClosingTime());

        return restaurantRepository.save(existingRestaurant);
    }

    public void deleteRestaurant(Long id) {

        if (!restaurantRepository.existsById(id)) {
            throw new RuntimeException("Restaurant not found");
        }

        restaurantRepository.deleteById(id);
    }
}