package com.codetrack.restaurantreservationspringboot.controller;

import com.codetrack.restaurantreservationspringboot.entity.Restaurant;
import com.codetrack.restaurantreservationspringboot.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService service;

    public RestaurantController(RestaurantService service) {
        this.service = service;
    }

    @GetMapping
    public List<Restaurant> getAll(
            @RequestParam(required = false) String search) {
        return service.getAll(search);
    }

    @GetMapping("/{id}")
    public Restaurant getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Restaurant create(@Valid @RequestBody Restaurant restaurant) {
        return service.create(restaurant);
    }

    @PutMapping("/{id}")
    public Restaurant update(
            @PathVariable Long id,
            @Valid @RequestBody Restaurant restaurant) {
        return service.update(id, restaurant);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}