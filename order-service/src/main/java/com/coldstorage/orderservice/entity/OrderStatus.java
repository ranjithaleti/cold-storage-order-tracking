package com.coldstorage.orderservice.entity;

public enum OrderStatus {
    CREATED,
    INVENTORY_RESERVED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}