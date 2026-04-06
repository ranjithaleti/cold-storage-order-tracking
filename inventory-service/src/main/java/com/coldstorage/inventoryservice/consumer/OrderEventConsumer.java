package com.coldstorage.inventoryservice.consumer;

import com.coldstorage.inventoryservice.event.OrderCancelledEvent;
import com.coldstorage.inventoryservice.event.OrderCreatedEvent;
import com.coldstorage.inventoryservice.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);
    private static final String IDEMPOTENCY_PREFIX = "inventory:processed:";
    private static final Duration IDEMPOTENCY_TTL = Duration.ofHours(24);

    private final InventoryService inventoryService;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public OrderEventConsumer(InventoryService inventoryService,
                              StringRedisTemplate redisTemplate,
                              ObjectMapper objectMapper) {
        this.inventoryService = inventoryService;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "order.created", groupId = "inventory-service-group")
    public void handleOrderCreated(Map<String, Object> payload,
                                   @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        String idempotencyKey = IDEMPOTENCY_PREFIX + "created:" + orderId;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(idempotencyKey))) {
            log.info("[inventory-service] Duplicate order.created event for order {} - skipping", orderId);
            return;
        }

        try {
            OrderCreatedEvent event = objectMapper.convertValue(payload, OrderCreatedEvent.class);
            log.info("[inventory-service] Received order.created event for order {}", orderId);
            inventoryService.reserveStock(event);
            redisTemplate.opsForValue().set(idempotencyKey, "processed", IDEMPOTENCY_TTL);
        } catch (Exception e) {
            log.error("[inventory-service] Failed to process order.created for order {}: {}",
                    orderId, e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(topics = "order.cancelled", groupId = "inventory-service-group")
    public void handleOrderCancelled(Map<String, Object> payload,
                                     @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        String idempotencyKey = IDEMPOTENCY_PREFIX + "cancelled:" + orderId;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(idempotencyKey))) {
            log.info("[inventory-service] Duplicate order.cancelled event for order {} - skipping", orderId);
            return;
        }

        try {
            OrderCancelledEvent event = objectMapper.convertValue(payload, OrderCancelledEvent.class);
            log.info("[inventory-service] Received order.cancelled event for order {}", orderId);
            inventoryService.releaseStock(event);
            redisTemplate.opsForValue().set(idempotencyKey, "processed", IDEMPOTENCY_TTL);
        } catch (Exception e) {
            log.error("[inventory-service] Failed to process order.cancelled for order {}: {}",
                    orderId, e.getMessage(), e);
            throw e;
        }
    }
}