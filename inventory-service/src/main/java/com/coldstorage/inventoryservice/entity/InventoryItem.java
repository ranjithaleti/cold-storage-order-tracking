package com.coldstorage.inventoryservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "item_id")
    private String itemId;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "warehouse_location", nullable = false)
    private String warehouseLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "temperature_zone", nullable = false)
    private TemperatureZone temperatureZone;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    private int reservedQuantity;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    @PrePersist
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getWarehouseLocation() { return warehouseLocation; }
    public void setWarehouseLocation(String warehouseLocation) { this.warehouseLocation = warehouseLocation; }

    public TemperatureZone getTemperatureZone() { return temperatureZone; }
    public void setTemperatureZone(TemperatureZone temperatureZone) { this.temperatureZone = temperatureZone; }

    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }

    public int getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(int reservedQuantity) { this.reservedQuantity = reservedQuantity; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
}