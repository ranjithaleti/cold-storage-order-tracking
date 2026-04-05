package com.coldstorage.orderservice.service;

import com.coldstorage.orderservice.dto.CreateOrderRequest;
import com.coldstorage.orderservice.dto.OrderResponse;
import com.coldstorage.orderservice.entity.Order;
import com.coldstorage.orderservice.entity.OrderStatus;
import com.coldstorage.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

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

        log.info("Order cancelled successfully with ID: {}", orderId);
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