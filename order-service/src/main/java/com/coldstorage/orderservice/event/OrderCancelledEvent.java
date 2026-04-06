package com.coldstorage.orderservice.event;

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
public class OrderCancelledEvent {

    private UUID orderId;
    private String productId;
    private String warehouseLocation;
    private Integer quantity;
    private String correlationId;
    private LocalDateTime cancelledAt;
}