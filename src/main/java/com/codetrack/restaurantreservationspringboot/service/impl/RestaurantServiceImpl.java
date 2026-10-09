package com.codetrack.restaurantreservationspringboot.service.impl;

import com.codetrack.restaurantreservationspringboot.entity.Restaurant;
import com.codetrack.restaurantreservationspringboot.exception.ResourceNotFoundException;
import com.codetrack.restaurantreservationspringboot.repository.RestaurantRepository;
import com.codetrack.restaurantreservationspringboot.service.RestaurantService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository repository;

    public RestaurantServiceImpl(RestaurantRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Restaurant> getAll(String search) {
        if (search == null || search.isBlank()) {
            return repository.findAll();
        }

        return repository.findByNameContainingIgnoreCase(search);
    }

    @Override
    @Transactional(readOnly = true)
    public Restaurant getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Restaurant not found with id: " + id));
    }

    @Override
    public Restaurant create(Restaurant restaurant) {
        restaurant.setId(null);
        return repository.save(restaurant);
    }

    @Override
    public Restaurant update(Long id, Restaurant input) {
        Restaurant restaurant = getById(id);

        restaurant.setName(input.getName());
        restaurant.setAddress(input.getAddress());
        restaurant.setPhone(input.getPhone());
        restaurant.setDescription(input.getDescription());
        restaurant.setOpeningTime(input.getOpeningTime());
        restaurant.setClosingTime(input.getClosingTime());

        return repository.save(restaurant);
    }

    @Override
    public void delete(Long id) {
        Restaurant restaurant = getById(id);
        repository.delete(restaurant);
    }
}