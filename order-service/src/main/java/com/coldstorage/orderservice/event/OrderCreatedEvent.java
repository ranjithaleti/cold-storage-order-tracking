package com.coldstorage.orderservice.event;

import com.coldstorage.orderservice.entity.TemperatureZone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {

    private UUID orderId;
    private String productId;
    private String warehouseLocation;
    private Integer quantity;
    private TemperatureZone temperatureZone;
    private String correlationId;
    private LocalDateTime createdAt;
}