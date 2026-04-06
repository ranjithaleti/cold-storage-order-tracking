package com.coldstorage.orderservice.service;

import com.coldstorage.orderservice.event.OrderCancelledEvent;
import com.coldstorage.orderservice.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.order-created}")
    private String orderCreatedTopic;

    @Value("${kafka.topics.order-cancelled}")
    private String orderCancelledTopic;

    public void publishOrderCreated(OrderCreatedEvent event) {
        log.info("Publishing OrderCreatedEvent for orderId: {}", event.getOrderId());

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(orderCreatedTopic, event.getOrderId().toString(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("OrderCreatedEvent published successfully for orderId: {} to partition: {}",
                        event.getOrderId(),
                        result.getRecordMetadata().partition());
            } else {
                log.error("Failed to publish OrderCreatedEvent for orderId: {}",
                        event.getOrderId(), ex);
            }
        });
    }

    public void publishOrderCancelled(OrderCancelledEvent event) {
        log.info("Publishing OrderCancelledEvent for orderId: {}", event.getOrderId());

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(orderCancelledTopic, event.getOrderId().toString(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("OrderCancelledEvent published successfully for orderId: {} to partition: {}",
                        event.getOrderId(),
                        result.getRecordMetadata().partition());
            } else {
                log.error("Failed to publish OrderCancelledEvent for orderId: {}",
                        event.getOrderId(), ex);
            }
        });
    }
}