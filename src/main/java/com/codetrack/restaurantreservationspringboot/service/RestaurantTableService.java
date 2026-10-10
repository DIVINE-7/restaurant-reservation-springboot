
package com.codetrack.restaurantreservationspringboot.service;

import com.codetrack.restaurantreservationspringboot.entity.RestaurantTable;

import java.util.List;

public interface RestaurantTableService {

    List<RestaurantTable> getTablesByRestaurant(Long restaurantId);

    RestaurantTable getTableById(Long id);

    RestaurantTable createTable(Long restaurantId, RestaurantTable table);

    RestaurantTable updateTable(Long id, RestaurantTable updatedTable);

    void deleteTable(Long id);
}
