
package com.codetrack.restaurantreservationspringboot.controller;

import com.codetrack.restaurantreservationspringboot.entity.RestaurantTable;
import com.codetrack.restaurantreservationspringboot.service.RestaurantTableService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RestaurantTableController {

    private final RestaurantTableService tableService;

    public RestaurantTableController(RestaurantTableService tableService) {
        this.tableService = tableService;
    }

    public record TableRequest(
            @NotBlank(message = "Table number is required")
            String tableNumber,

            @Min(value = 1, message = "Capacity must be at least 1")
            int capacity,

            Boolean active
    ) {}

    public record TableResponse(
            Long id,
            Long restaurantId,
            String tableNumber,
            int capacity,
            boolean active
    ) {}

    private TableResponse toResponse(RestaurantTable table) {
        return new TableResponse(
                table.getId(),
                table.getRestaurant().getId(),
                table.getTableNumber(),
                table.getCapacity(),
                table.isActive()
        );
    }

    @GetMapping("/restaurants/{restaurantId}/tables")
    public List<TableResponse> getTables(
            @PathVariable Long restaurantId) {

        return tableService.getTablesByRestaurant(restaurantId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/tables/{tableId}")
    public TableResponse getTable(@PathVariable Long tableId) {
        return toResponse(tableService.getTableById(tableId));
    }

    @PostMapping("/restaurants/{restaurantId}/tables")
    @ResponseStatus(HttpStatus.CREATED)
    public TableResponse createTable(
            @PathVariable Long restaurantId,
            @Valid @RequestBody TableRequest request) {

        RestaurantTable table = RestaurantTable.builder()
                .tableNumber(request.tableNumber())
                .capacity(request.capacity())
                .build();

        return toResponse(
                tableService.createTable(restaurantId, table));
    }

    @PutMapping("/tables/{tableId}")
    public TableResponse updateTable(
            @PathVariable Long tableId,
            @Valid @RequestBody TableRequest request) {

        RestaurantTable updated = tableService.getTableById(tableId);

        updated.setTableNumber(request.tableNumber());
        updated.setCapacity(request.capacity());

        if (request.active() != null) {
            updated.setActive(request.active());
        }

        return toResponse(
                tableService.updateTable(tableId, updated));
    }

    @PatchMapping("/tables/{tableId}/deactivate")
    public TableResponse deactivateTable(
            @PathVariable Long tableId) {

        tableService.deleteTable(tableId);
        return toResponse(tableService.getTableById(tableId));
    }
}
