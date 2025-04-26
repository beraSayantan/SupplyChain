package com.smartsupply.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * WarehouseManager class representing warehouse managers in the supply chain
 */
public class WarehouseManager extends User {
    private String warehouseId;
    private String location;
    private double warehouseCapacity; // in cubic meters
    private double currentUtilization; // percentage of capacity used
    private Map<String, Double> sectionCapacities; // capacities of different warehouse sections
    
    // Default constructor
    public WarehouseManager() {
        super();
        this.setRole(Role.WAREHOUSE_MANAGER);
        this.sectionCapacities = new HashMap<>();
        this.currentUtilization = 0.0;
    }
    
    // Parameterized constructor
    public WarehouseManager(String userId, String name, String password) {
        super(userId, name, password, Role.WAREHOUSE_MANAGER);
        this.sectionCapacities = new HashMap<>();
        this.currentUtilization = 0.0;
    }
    
    // Constructor with warehouse details
    public WarehouseManager(String userId, String name, String password, 
                           String warehouseId, String location, double capacity) {
        super(userId, name, password, Role.WAREHOUSE_MANAGER);
        this.warehouseId = warehouseId;
        this.location = location;
        this.warehouseCapacity = capacity;
        this.sectionCapacities = new HashMap<>();
        this.currentUtilization = 0.0;
    }
    
    // Varargs constructor for warehouse sections
    public WarehouseManager(String userId, String name, String password, 
                           String warehouseId, String location, double capacity, 
                           Object... sectionInfo) {
        this(userId, name, password, warehouseId, location, capacity);
        
        for (int i = 0; i < sectionInfo.length; i += 2) {
            if (i + 1 < sectionInfo.length) {
                String section = (String) sectionInfo[i];
                Double sectionCapacity = (Double) sectionInfo[i + 1];
                sectionCapacities.put(section, sectionCapacity);
            }
        }
    }
    
    // Method to receive shipment
    public boolean receiveShipment(Order order) {
        System.out.println("Warehouse " + warehouseId + " receiving shipment for order: " + order.getOrderId());
        // Implementation would handle inventory updates, etc.
        return true;
    }
    
    // Method to allocate warehouse space
    public boolean allocateSpace(String productCategory, double spaceNeeded) {
        if (currentUtilization + spaceNeeded <= warehouseCapacity) {
            currentUtilization += spaceNeeded;
            System.out.println("Space allocated for " + productCategory + ": " + spaceNeeded + " cubic meters");
            return true;
        }
        return false;
    }
    
    // Overloaded method to allocate space in a specific section
    public boolean allocateSpace(String section, String productCategory, double spaceNeeded) {
        Double sectionCapacity = sectionCapacities.get(section);
        if (sectionCapacity != null && spaceNeeded <= sectionCapacity) {
            sectionCapacities.put(section, sectionCapacity - spaceNeeded);
            System.out.println("Space allocated in section " + section + " for " + 
                              productCategory + ": " + spaceNeeded + " cubic meters");
            return true;
        }
        return false;
    }
    
    // Method to manage inventory
    public Map<String, Integer> checkInventory() {
        // Implementation would check current inventory levels
        Map<String, Integer> inventory = new HashMap<>();
        // Placeholder data
        inventory.put("Electronics", 150);
        inventory.put("Clothing", 300);
        inventory.put("Food", 200);
        return inventory;
    }
    
    // Method to process outgoing orders
    public boolean processOutgoingOrder(Order order) {
        System.out.println("Processing outgoing order: " + order.getOrderId());
        // Implementation would handle updating inventory, etc.
        return true;
    }
    
    @Override
    public boolean hasPermission(String operation) {
        // Warehouse manager specific permissions
        switch (operation) {
            case "RECEIVE_SHIPMENT":
            case "ALLOCATE_SPACE":
            case "CHECK_INVENTORY":
            case "PROCESS_ORDER":
                return true;
            default:
                return false;
        }
    }
    
    // Getters and setters
    public String getWarehouseId() {
        return warehouseId;
    }
    
    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public double getWarehouseCapacity() {
        return warehouseCapacity;
    }
    
    public void setWarehouseCapacity(double warehouseCapacity) {
        this.warehouseCapacity = warehouseCapacity;
    }
    
    public double getCurrentUtilization() {
        return currentUtilization;
    }
    
    public void setCurrentUtilization(double currentUtilization) {
        this.currentUtilization = currentUtilization;
    }
    
    public Map<String, Double> getSectionCapacities() {
        return sectionCapacities;
    }
    
    @Override
    public String toString() {
        return String.format("WarehouseManager[id=%s, name=%s, warehouse=%s, utilization=%.1f%%]", 
                             getUserId(), getName(), warehouseId, currentUtilization);
    }
}