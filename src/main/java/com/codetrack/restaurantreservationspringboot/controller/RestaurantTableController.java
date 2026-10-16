
package com.codetrack.restaurantreservationspringboot.controller;

import com.codetrack.restaurantreservationspringboot.entity.Restaurant;
import com.codetrack.restaurantreservationspringboot.entity.RestaurantTable;
import com.codetrack.restaurantreservationspringboot.repository.RestaurantRepository;
import com.codetrack.restaurantreservationspringboot.repository.RestaurantTableRepository;
import com.codetrack.restaurantreservationspringboot.service.TableAvailabilityService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/tables")
public class RestaurantTableController {

    private final RestaurantTableRepository tableRepository;
    private final RestaurantRepository restaurantRepository;
    private final TableAvailabilityService availabilityService;

    public RestaurantTableController(
            RestaurantTableRepository tableRepository,
            RestaurantRepository restaurantRepository,
            TableAvailabilityService availabilityService) {
        this.tableRepository = tableRepository;
        this.restaurantRepository = restaurantRepository;
        this.availabilityService = availabilityService;
    }

    public record TableRequest(
            Long restaurantId,
            String tableNumber,
            int capacity,
            Boolean active) {
    }

    public record ActiveRequest(Boolean active) {
    }

    public record TableResponse(
            Long id,
            Long restaurantId,
            String tableNumber,
            int capacity,
            boolean active,
            boolean available) {
    }

    private TableResponse toResponse(
            RestaurantTable table,
            Set<Long> availableIds) {

        return new TableResponse(
                table.getId(),
                table.getRestaurant().getId(),
                table.getTableNumber(),
                table.getCapacity(),
                table.isActive(),
                availableIds.contains(table.getId())
        );
    }

    // Helper method: retrieves a table by ID.
    // A distinct name avoids conflicting with the GET endpoint method.
    private RestaurantTable findTableById(Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Table not found"
                ));
    }

    private void validateTableDetails(
            String tableNumber,
            int capacity) {

        if (tableNumber == null || tableNumber.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Table number is required"
            );
        }

        if (capacity < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Table capacity must be at least 1"
            );
        }
    }

    // GET /api/tables
    // GET /api/tables?restaurantId=1
    @GetMapping
    public List<TableResponse> getTables(
            @RequestParam(required = false) Long restaurantId) {

        Set<Long> availableIds =
                availabilityService.getAvailableTableIds();

        List<RestaurantTable> tables;

        if (restaurantId != null) {
            if (!restaurantRepository.existsById(restaurantId)) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Restaurant not found"
                );
            }

            tables = tableRepository.findAll().stream()
                    .filter(table -> table.getRestaurant()
                            .getId().equals(restaurantId))
                    .toList();
        } else {
            tables = tableRepository.findAll();
        }

        return tables.stream()
                .map(table -> toResponse(table, availableIds))
                .toList();
    }

    // GET /api/tables/{id}
    @GetMapping("/{id}")
    public TableResponse getTable(@PathVariable Long id) {

        RestaurantTable table = findTableById(id);

        return toResponse(
                table,
                availabilityService.getAvailableTableIds()
        );
    }

    // POST /api/tables
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TableResponse createTable(
            @RequestBody TableRequest request) {

        if (request == null || request.restaurantId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Restaurant ID is required"
            );
        }

        if (request.tableNumber() == null
                || request.tableNumber().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Table number is required"
            );
        }

        validateTableDetails(
                request.tableNumber(),
                request.capacity()
        );

        Restaurant restaurant = restaurantRepository
                .findById(request.restaurantId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Restaurant not found"
                ));

        RestaurantTable table = RestaurantTable.builder()
                .restaurant(restaurant)
                .tableNumber(request.tableNumber().trim())
                .capacity(request.capacity())
                .active(request.active() == null || request.active())
                .build();

        RestaurantTable saved = tableRepository.save(table);

        return toResponse(
                saved,
                availabilityService.getAvailableTableIds()
        );
    }

    // PUT /api/tables/{id}
    @PutMapping("/{id}")
    public TableResponse updateTable(
            @PathVariable Long id,
            @RequestBody TableRequest request) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Table details are required"
            );
        }

        RestaurantTable table = findTableById(id);

        validateTableDetails(
                request.tableNumber(),
                request.capacity()
        );

        if (request.restaurantId() != null) {
            Restaurant restaurant = restaurantRepository
                    .findById(request.restaurantId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Restaurant not found"
                    ));

            table.setRestaurant(restaurant);
        }

        table.setTableNumber(request.tableNumber().trim());
        table.setCapacity(request.capacity());

        if (request.active() != null) {
            table.setActive(request.active());
        }

        RestaurantTable saved = tableRepository.save(table);

        return toResponse(
                saved,
                availabilityService.getAvailableTableIds()
        );
    }

    // PATCH /api/tables/{id}/status
    @PatchMapping("/{id}/status")
    public TableResponse updateTableStatus(
            @PathVariable Long id,
            @RequestBody ActiveRequest request) {

        if (request == null || request.active() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Active status is required"
            );
        }

        RestaurantTable table = findTableById(id);
        table.setActive(request.active());

        RestaurantTable saved = tableRepository.save(table);

        return toResponse(
                saved,
                availabilityService.getAvailableTableIds()
        );
    }
}
