package com.coldstorage.inventoryservice.event;

public class InventoryReservationFailedEvent {
    private String orderId;
    private String productId;
    private String reason;
    private String correlationId;

    public InventoryReservationFailedEvent() {}

    public InventoryReservationFailedEvent(String orderId, String productId,
                                           String reason, String correlationId) {
        this.orderId = orderId;
        this.productId = productId;
        this.reason = reason;
        this.correlationId = correlationId;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
}