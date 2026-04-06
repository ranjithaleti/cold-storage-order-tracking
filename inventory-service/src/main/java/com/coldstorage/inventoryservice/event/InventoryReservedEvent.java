package com.coldstorage.inventoryservice.event;

public class InventoryReservedEvent {
    private String orderId;
    private String productId;
    private String warehouseLocation;
    private int reservedQuantity;
    private String correlationId;

    public InventoryReservedEvent() {}

    public InventoryReservedEvent(String orderId, String productId,
                                  String warehouseLocation, int reservedQuantity,
                                  String correlationId) {
        this.orderId = orderId;
        this.productId = productId;
        this.warehouseLocation = warehouseLocation;
        this.reservedQuantity = reservedQuantity;
        this.correlationId = correlationId;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getWarehouseLocation() { return warehouseLocation; }
    public void setWarehouseLocation(String warehouseLocation) { this.warehouseLocation = warehouseLocation; }

    public int getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(int reservedQuantity) { this.reservedQuantity = reservedQuantity; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
}