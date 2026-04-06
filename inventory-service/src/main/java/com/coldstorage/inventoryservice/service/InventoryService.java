package com.coldstorage.inventoryservice.service;

import com.coldstorage.inventoryservice.entity.InventoryItem;
import com.coldstorage.inventoryservice.entity.TemperatureZone;
import com.coldstorage.inventoryservice.event.InventoryReservationFailedEvent;
import com.coldstorage.inventoryservice.event.InventoryReservedEvent;
import com.coldstorage.inventoryservice.event.OrderCancelledEvent;
import com.coldstorage.inventoryservice.event.OrderCreatedEvent;
import com.coldstorage.inventoryservice.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public InventoryService(InventoryRepository inventoryRepository,
                            KafkaTemplate<String, Object> kafkaTemplate) {
        this.inventoryRepository = inventoryRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public void reserveStock(OrderCreatedEvent event) {
        String orderId = event.getOrderId();
        String productId = event.getProductId();
        String warehouseLocation = event.getWarehouseLocation();
        TemperatureZone temperatureZone = TemperatureZone.valueOf(event.getTemperatureZone());
        int requestedQty = event.getQuantity();

        log.info("[inventory-service] Attempting to reserve {} units of product {} " +
                        "in {} zone at {} for order {}",
                requestedQty, productId, temperatureZone, warehouseLocation, orderId);

        Optional<InventoryItem> itemOpt = inventoryRepository
                .findByProductIdAndWarehouseLocationAndTemperatureZoneForUpdate(
                        productId, warehouseLocation, temperatureZone);

        if (itemOpt.isEmpty()) {
            log.warn("[inventory-service] No inventory record found for product {} in {} at {}",
                    productId, temperatureZone, warehouseLocation);
            publishReservationFailed(orderId, productId,
                    "No inventory record found", event.getCorrelationId());
            return;
        }

        InventoryItem item = itemOpt.get();

        if (item.getAvailableQuantity() < requestedQty) {
            log.warn("[inventory-service] Insufficient stock for product {}. " +
                            "Available: {}, Requested: {}", productId,
                    item.getAvailableQuantity(), requestedQty);
            publishReservationFailed(orderId, productId,
                    "Insufficient stock: available=" + item.getAvailableQuantity()
                            + " requested=" + requestedQty, event.getCorrelationId());
            return;
        }

        item.setAvailableQuantity(item.getAvailableQuantity() - requestedQty);
        item.setReservedQuantity(item.getReservedQuantity() + requestedQty);
        inventoryRepository.save(item);

        log.info("[inventory-service] Reserved {} units for order {}. " +
                "Remaining available: {}", requestedQty, orderId, item.getAvailableQuantity());

        InventoryReservedEvent reservedEvent = new InventoryReservedEvent(
                orderId, productId, warehouseLocation, requestedQty, event.getCorrelationId());

        kafkaTemplate.send("inventory.reserved", orderId, reservedEvent);
        log.info("[inventory-service] Published InventoryReservedEvent for order {}", orderId);
    }

    @Transactional
    public void releaseStock(OrderCancelledEvent event) {
        String orderId = event.getOrderId();
        String productId = event.getProductId();
        TemperatureZone temperatureZone = TemperatureZone.valueOf(event.getTemperatureZone());
        int qty = event.getQuantity();

        log.info("[inventory-service] Releasing {} units for cancelled order {}", qty, orderId);

        Optional<InventoryItem> itemOpt = inventoryRepository
                .findByProductIdAndWarehouseLocationAndTemperatureZoneForUpdate(
                        productId, event.getWarehouseLocation(), temperatureZone);

        if (itemOpt.isEmpty()) {
            log.warn("[inventory-service] Cannot release stock - no inventory record for product {}",
                    productId);
            return;
        }

        InventoryItem item = itemOpt.get();
        item.setReservedQuantity(Math.max(0, item.getReservedQuantity() - qty));
        item.setAvailableQuantity(item.getAvailableQuantity() + qty);
        inventoryRepository.save(item);

        log.info("[inventory-service] Released {} units for order {}. " +
                "Available now: {}", qty, orderId, item.getAvailableQuantity());
    }

    private void publishReservationFailed(String orderId, String productId,
                                          String reason, String correlationId) {
        InventoryReservationFailedEvent failedEvent = new InventoryReservationFailedEvent(
                orderId, productId, reason, correlationId);
        kafkaTemplate.send("inventory.reservation-failed", orderId, failedEvent);
        log.info("[inventory-service] Published InventoryReservationFailedEvent for order {}", orderId);
    }
}