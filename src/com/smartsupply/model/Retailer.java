package com.smartsupply.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Retailer class representing retail store managers in the supply chain
 */
public class Retailer extends User {
    private String storeId;
    private String location;
    private double salesTarget;
    private double currentSales;
    private List<String> specializations; // Store specializations (electronics, grocery, etc.)
    
    // Default constructor
    public Retailer() {
        super();
        this.setRole(Role.RETAILER);
        this.specializations = new ArrayList<>();
        this.currentSales = 0.0;
    }
    
    // Parameterized constructor
    public Retailer(String userId, String name, String password) {
        super(userId, name, password, Role.RETAILER);
        this.specializations = new ArrayList<>();
        this.currentSales = 0.0;
    }
    
    // Constructor with store details
    public Retailer(String userId, String name, String password, 
                   String storeId, String location, double salesTarget) {
        super(userId, name, password, Role.RETAILER);
        this.storeId = storeId;
        this.location = location;
        this.salesTarget = salesTarget;
        this.specializations = new ArrayList<>();
        this.currentSales = 0.0;
    }
    
    // Varargs constructor for store specializations
    public Retailer(String userId, String name, String password, 
                   String storeId, String location, double salesTarget,
                   String... specializations) {
        this(userId, name, password, storeId, location, salesTarget);
        for (String specialization : specializations) {
            this.specializations.add(specialization);
        }
    }
    
    // Method to place an order
    public Order placeOrder(Map<Product, Integer> products, Supplier supplier) {
        String orderId = "ORD-" + storeId + "-" + System.currentTimeMillis();
        Order order = new Order(orderId, products, this.getUserId(), supplier.getUserId());
        System.out.println("Order placed: " + orderId);
        return order;
    }
    
    // Overloaded method to place urgently needed orders
    public Order placeOrder(Map<Product, Integer> products, Supplier supplier, boolean urgent) {
        Order order = placeOrder(products, supplier);
        if (urgent) {
            order.setUrgent(true);
            order.setPriority(1); // Highest priority
            System.out.println("Urgent order placed: " + order.getOrderId());
        }
        return order;
    }
    
    // Method to scan barcode
    public Product scanBarcode(String barcode) {
        System.out.println("Scanning barcode: " + barcode);
        // Implementation would connect to inventory system
        // Placeholder implementation
        return new Product("P-" + barcode, "Scanned Product", 9.99);
    }
    
    // Method to record a sale
    public void recordSale(Order order) {
        double orderTotal = order.calculateTotal();
        currentSales += orderTotal;
        System.out.println("Sale recorded: $" + orderTotal);
    }
    
    // Varargs method to record multiple sales
    public void recordSales(double... salesAmounts) {
        double totalRecorded = 0.0;
        for (double amount : salesAmounts) {
            currentSales += amount;
            totalRecorded += amount;
        }
        System.out.println("Multiple sales recorded, total: $" + totalRecorded);
    }
    
    // Method to check if sales target has been met
    public boolean hasMetSalesTarget() {
        return currentSales >= salesTarget;
    }
    
    @Override
    public boolean hasPermission(String operation) {
        // Retailer-specific permissions
        switch (operation) {
            case "PLACE_ORDER":
            case "SCAN_BARCODE":
            case "RECORD_SALE":
                return true;
            default:
                return false;
        }
    }
    
    // Getters and setters
    public String getStoreId() {
        return storeId;
    }
    
    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public double getSalesTarget() {
        return salesTarget;
    }
    
    public void setSalesTarget(double salesTarget) {
        this.salesTarget = salesTarget;
    }
    
    public double getCurrentSales() {
        return currentSales;
    }
    
    public List<String> getSpecializations() {
        return specializations;
    }
    
    public void addSpecialization(String specialization) {
        if (!specializations.contains(specialization)) {
            specializations.add(specialization);
        }
    }
    
    public void removeSpecialization(String specialization) {
        specializations.remove(specialization);
    }
    
    @Override
    public String toString() {
        return String.format("Retailer[id=%s, name=%s, store=%s, sales=%.2f]", 
                             getUserId(), getName(), storeId, currentSales);
    }
}