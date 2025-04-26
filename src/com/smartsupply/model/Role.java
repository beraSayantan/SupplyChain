package com.smartsupply.model;

/**
 * Enum representing different user roles in the supply chain system
 */
public enum Role {
    ADMIN("Administrator", 4),
    SUPPLIER("Product Supplier", 3),
    WAREHOUSE_MANAGER("Warehouse Manager", 2),
    RETAILER("Retail Store Manager", 1);
    
    private final String description;
    private final int accessLevel;
    
    // Constructor for Role enum
    private Role(String description, int accessLevel) {
        this.description = description;
        this.accessLevel = accessLevel;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getAccessLevel() {
        return accessLevel;
    }
    
    @Override
    public String toString() {
        return description;
    }
}