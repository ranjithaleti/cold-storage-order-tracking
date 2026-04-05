package com.coldstorage.orderservice.dto;

import com.coldstorage.orderservice.entity.TemperatureZone;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequest {

    @NotBlank(message = "Warehouse location is required")
    private String warehouseLocation;

    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Temperature zone is required")
    private TemperatureZone temperatureZone;
}