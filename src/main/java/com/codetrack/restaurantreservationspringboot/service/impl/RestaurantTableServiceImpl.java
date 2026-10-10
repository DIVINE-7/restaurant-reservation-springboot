
package com.codetrack.restaurantreservationspringboot.service.impl;

import com.codetrack.restaurantreservationspringboot.entity.Restaurant;
import com.codetrack.restaurantreservationspringboot.entity.RestaurantTable;
import com.codetrack.restaurantreservationspringboot.exception.ResourceNotFoundException;
import com.codetrack.restaurantreservationspringboot.repository.RestaurantRepository;
import com.codetrack.restaurantreservationspringboot.repository.RestaurantTableRepository;
import com.codetrack.restaurantreservationspringboot.service.RestaurantTableService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RestaurantTableServiceImpl implements RestaurantTableService {

    private final RestaurantTableRepository tableRepository;
    private final RestaurantRepository restaurantRepository;

    public RestaurantTableServiceImpl(
            RestaurantTableRepository tableRepository,
            RestaurantRepository restaurantRepository) {
        this.tableRepository = tableRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantTable> getTablesByRestaurant(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException(
                    "Restaurant not found with id: " + restaurantId);
        }

        return tableRepository.findByRestaurantIdAndActiveTrue(restaurantId);
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantTable getTableById(Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Table not found with id: " + id));
    }

    @Override
    public RestaurantTable createTable(
            Long restaurantId, RestaurantTable table) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Restaurant not found with id: " + restaurantId));

        table.setId(null);
        table.setRestaurant(restaurant);
        table.setActive(true);
        table.setTableNumber(table.getTableNumber().trim());

        return tableRepository.save(table);
    }

    @Override
    public RestaurantTable updateTable(
            Long id, RestaurantTable updatedTable) {

        RestaurantTable existing = getTableById(id);

        existing.setTableNumber(updatedTable.getTableNumber().trim());
        existing.setCapacity(updatedTable.getCapacity());
        existing.setActive(updatedTable.isActive());

        return tableRepository.save(existing);
    }

    @Override
    public void deleteTable(Long id) {
        RestaurantTable existing = getTableById(id);
        existing.setActive(false);
        tableRepository.save(existing);
    }
}
