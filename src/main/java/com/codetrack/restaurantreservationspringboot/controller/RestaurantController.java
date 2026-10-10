
package com.codetrack.restaurantreservationspringboot.controller;

import com.codetrack.restaurantreservationspringboot.entity.Restaurant;
import com.codetrack.restaurantreservationspringboot.service.RestaurantService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public List<Restaurant> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address) {
        return restaurantService.searchRestaurants(name, address);
    }

    @GetMapping("/{id}")
    public Restaurant getById(@PathVariable Long id) {
        return restaurantService.getRestaurantById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Restaurant create(@RequestBody Restaurant restaurant) {
        return restaurantService.createRestaurant(restaurant);
    }

    @PutMapping("/{id}")
    public Restaurant update(
            @PathVariable Long id,
            @RequestBody Restaurant restaurant) {
        return restaurantService.updateRestaurant(id, restaurant);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
    }
}
