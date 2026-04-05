package com.coldstorage.orderservice.dto;

import com.coldstorage.orderservice.entity.OrderStatus;
import com.coldstorage.orderservice.entity.TemperatureZone;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class OrderResponse {

    private UUID orderId;
    private String warehouseLocation;
    private String productId;
    private Integer quantity;
    private TemperatureZone temperatureZone;
    private OrderStatus status;
    private String correlationId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}