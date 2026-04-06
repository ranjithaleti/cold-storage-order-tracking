package com.coldstorage.inventoryservice.event;

public class OrderCreatedEvent {
    private String orderId;
    private String productId;
    private String warehouseLocation;
    private int quantity;
    private String temperatureZone;
    private String correlationId;

    public OrderCreatedEvent() {}

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getWarehouseLocation() { return warehouseLocation; }
    public void setWarehouseLocation(String warehouseLocation) { this.warehouseLocation = warehouseLocation; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getTemperatureZone() { return temperatureZone; }
    public void setTemperatureZone(String temperatureZone) { this.temperatureZone = temperatureZone; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
}