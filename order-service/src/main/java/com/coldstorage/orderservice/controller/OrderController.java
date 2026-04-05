package com.coldstorage.orderservice.controller;

import com.coldstorage.orderservice.dto.CreateOrderRequest;
import com.coldstorage.orderservice.dto.OrderResponse;
import com.coldstorage.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Management", description = "APIs for managing cold storage orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create a new order",
            description = "Submit a new shipment order for cold storage")
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        log.info("Received create order request for product: {}", request.getProductId());
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID",
            description = "Retrieve order details by order ID")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID orderId) {
        log.info("Received get order request for ID: {}", orderId);
        OrderResponse response = orderService.getOrder(orderId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel an order",
            description = "Cancel an existing order if it hasn't been shipped")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable UUID orderId) {
        log.info("Received cancel order request for ID: {}", orderId);
        OrderResponse response = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(response);
    }
}