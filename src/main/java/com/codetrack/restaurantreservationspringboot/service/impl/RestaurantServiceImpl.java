
package com.codetrack.restaurantreservationspringboot.service.impl;

import com.codetrack.restaurantreservationspringboot.entity.Restaurant;
import com.codetrack.restaurantreservationspringboot.repository.RestaurantRepository;
import com.codetrack.restaurantreservationspringboot.service.RestaurantService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantServiceImpl(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Restaurant not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Restaurant> searchRestaurants(String name, String address) {
        if (name != null && !name.isBlank()) {
            return restaurantRepository.findByNameContainingIgnoreCase(name);
        }

        if (address != null && !address.isBlank()) {
            return restaurantRepository.findByAddressContainingIgnoreCase(address);
        }

        return restaurantRepository.findAll();
    }

    @Override
    public Restaurant createRestaurant(Restaurant restaurant) {
        restaurant.setId(null);
        return restaurantRepository.save(restaurant);
    }

    @Override
    public Restaurant updateRestaurant(Long id, Restaurant updated) {
        Restaurant existing = getRestaurantById(id);

        existing.setName(updated.getName());
        existing.setAddress(updated.getAddress());
        existing.setDescription(updated.getDescription());
        existing.setPhone(updated.getPhone());
        existing.setOpeningTime(updated.getOpeningTime());
        existing.setClosingTime(updated.getClosingTime());

        return restaurantRepository.save(existing);
    }

    @Override
    public void deleteRestaurant(Long id) {
        Restaurant restaurant = getRestaurantById(id);
        restaurantRepository.delete(restaurant);
    }
}
