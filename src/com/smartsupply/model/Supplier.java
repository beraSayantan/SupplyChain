package com.smartsupply.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Supplier class representing product suppliers in the supply chain
 */
public class Supplier extends User {
    private String companyName;
    private String address;
    private List<String> productCategories;
    private Map<String, Double> productPrices;
    private double reliabilityScore; // 0-100 scale
    
    // Default constructor
    public Supplier() {
        super();
        this.setRole(Role.SUPPLIER);
        this.productCategories = new ArrayList<>();
        this.productPrices = new HashMap<>();
        this.reliabilityScore = 50.0; // Default neutral score
    }
    
    // Parameterized constructor
    public Supplier(String userId, String name, String password) {
        super(userId, name, password, Role.SUPPLIER);
        this.productCategories = new ArrayList<>();
        this.productPrices = new HashMap<>();
        this.reliabilityScore = 50.0;
    }
    
    // Constructor with company details
    public Supplier(String userId, String name, String password, 
                   String companyName, String address) {
        super(userId, name, password, Role.SUPPLIER);
        this.companyName = companyName;
        this.address = address;
        this.productCategories = new ArrayList<>();
        this.productPrices = new HashMap<>();
        this.reliabilityScore = 50.0;
    }
    
    // Varargs constructor for product categories
    public Supplier(String userId, String name, String password, 
                   String companyName, String address, String... categories) {
        this(userId, name, password, companyName, address);
        for (String category : categories) {
            this.productCategories.add(category);
        }
    }
    
    // Method to process a purchase order
    public boolean processPurchaseOrder(Order order) {
        System.out.println("Supplier " + getName() + " processing order: " + order.getOrderId());
        // Implementation would process the purchase order
        return true;
    }
    
    // Method to update product pricing
    public void updateProductPrice(String productId, double newPrice) {
        productPrices.put(productId, newPrice);
        System.out.println("Updated price of product " + productId + " to " + newPrice);
    }
    
    // Varargs method to update multiple product prices at once
    public void updateProductPrices(Object... productInfos) {
        for (int i = 0; i < productInfos.length; i += 2) {
            if (i + 1 < productInfos.length) {
                String productId = (String) productInfos[i];
                Double price = (Double) productInfos[i + 1];
                productPrices.put(productId, price);
            }
        }
        System.out.println("Updated " + (productInfos.length / 2) + " product prices");
    }
    
    // Method to view current orders
    public List<Order> viewOrders() {
        // Implementation would retrieve and return orders
        return new ArrayList<>(); // Placeholder
    }
    
    // Method to check if supplier provides a specific product category
    public boolean providesCategory(String category) {
        return productCategories.contains(category);
    }
    
    @Override
    public boolean hasPermission(String operation) {
        // Supplier-specific permissions
        switch (operation) {
            case "UPDATE_PRICE":
            case "PROCESS_ORDER":
            case "VIEW_ORDERS":
                return true;
            default:
                return false;
        }
    }
    
    // Getters and setters
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public List<String> getProductCategories() {
        return productCategories;
    }
    
    public void addProductCategory(String category) {
        if (!productCategories.contains(category)) {
            productCategories.add(category);
        }
    }
    
    public void removeProductCategory(String category) {
        productCategories.remove(category);
    }
    
    public double getReliabilityScore() {
        return reliabilityScore;
    }
    
    public void setReliabilityScore(double reliabilityScore) {
        if (reliabilityScore >= 0 && reliabilityScore <= 100) {
            this.reliabilityScore = reliabilityScore;
        }
    }
    
    public Map<String, Double> getProductPrices() {
        return productPrices;
    }
    
    @Override
    public String toString() {
        return String.format("Supplier[id=%s, name=%s, company=%s, reliability=%.1f]", 
                             getUserId(), getName(), companyName, reliabilityScore);
    }
}