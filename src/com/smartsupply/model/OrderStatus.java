package com.smartsupply.model;

/**
 * Enum representing different possible states of an order
 */
public enum OrderStatus {
    PLACED("Order has been placed"),
    PROCESSING("Order is being processed"),
    SHIPPED("Order has been shipped"),
    IN_TRANSIT("Order is in transit"),
    DELIVERED("Order has been delivered"),
    CANCELLED("Order has been cancelled"),
    RETURNED("Order has been returned");
    
    private final String description;
    
    private OrderStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return description;
    }
}