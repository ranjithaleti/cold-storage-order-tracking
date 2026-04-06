package com.coldstorage.orderservice.service;

import com.coldstorage.orderservice.dto.CreateOrderRequest;
import com.coldstorage.orderservice.dto.OrderResponse;
import com.coldstorage.orderservice.entity.Order;
import com.coldstorage.orderservice.entity.OrderStatus;
import com.coldstorage.orderservice.event.OrderCancelledEvent;
import com.coldstorage.orderservice.event.OrderCreatedEvent;
import com.coldstorage.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order for warehouse: {}, product: {}",
                request.getWarehouseLocation(), request.getProductId());

        Order order = Order.builder()
                .warehouseLocation(request.getWarehouseLocation())
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .temperatureZone(request.getTemperatureZone())
                .status(OrderStatus.CREATED)
                .build();

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {}", savedOrder.getOrderId());

        // Publish event to Kafka
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(savedOrder.getOrderId())
                .productId(savedOrder.getProductId())
                .warehouseLocation(savedOrder.getWarehouseLocation())
                .quantity(savedOrder.getQuantity())
                .temperatureZone(savedOrder.getTemperatureZone())
                .correlationId(savedOrder.getCorrelationId())
                .createdAt(savedOrder.getCreatedAt())
                .build();

        eventPublisher.publishOrderCreated(event);

        return mapToResponse(savedOrder);
    }

    public OrderResponse getOrder(UUID orderId) {
        log.info("Fetching order with ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        return mapToResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(UUID orderId) {
        log.info("Cancelling order with ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        if (order.getStatus() == OrderStatus.SHIPPED ||
                order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel order in status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);

        // Publish event to Kafka
        OrderCancelledEvent event = OrderCancelledEvent.builder()
                .orderId(savedOrder.getOrderId())
                .productId(savedOrder.getProductId())
                .warehouseLocation(savedOrder.getWarehouseLocation())
                .quantity(savedOrder.getQuantity())
                .correlationId(savedOrder.getCorrelationId())
                .cancelledAt(LocalDateTime.now())
                .build();

        eventPublisher.publishOrderCancelled(event);

        return mapToResponse(savedOrder);
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .warehouseLocation(order.getWarehouseLocation())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .temperatureZone(order.getTemperatureZone())
                .status(order.getStatus())
                .correlationId(order.getCorrelationId())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}