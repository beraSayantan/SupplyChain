package com.smartsupply.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.smartsupply.exception.InventoryException;

/**
 * Class representing inventory at a specific location
 */
public class Inventory implements Serializable {
    private String locationId;
    private String locationType; // "warehouse", "store", etc.
    private Map<Product, Integer> stockLevels;
    private Map<Product, Integer> reorderThresholds;
    private Map<Product, Integer> recommendedStockLevels;
    private Date lastUpdated;
    
    // Default constructor
    public Inventory() {
        this.stockLevels = new HashMap<>();
        this.reorderThresholds = new HashMap<>();
        this.recommendedStockLevels = new HashMap<>();
        this.lastUpdated = new Date();
    }
    
    // Constructor with location details
    public Inventory(String locationId, String locationType) {
        this();
        this.locationId = locationId;
        this.locationType = locationType;
    }
    
    // Method to add stock
    public void addStock(Product product, int quantity) {
        int currentStock = stockLevels.getOrDefault(product, 0);
        stockLevels.put(product, currentStock + quantity);
        lastUpdated = new Date();
        System.out.println("Added " + quantity + " units of " + product.getName() + 
                         " to " + locationId + ". New stock: " + (currentStock + quantity));
    }
    
    // Varargs method to add multiple products at once
    public void addStock(Object... productQuantities) {
        for (int i = 0; i < productQuantities.length; i += 2) {
            if (i + 1 < productQuantities.length) {
                Product product = (Product) productQuantities[i];
                Integer quantity = (Integer) productQuantities[i + 1];
                addStock(product, quantity);
            }
        }
    }
    
    // Method to remove stock
    public boolean removeStock(Product product, int quantity) throws InventoryException {
        int currentStock = stockLevels.getOrDefault(product, 0);
        
        if (quantity > currentStock) {
            throw new InventoryException("Insufficient stock for product: " + product.getName());
        }
        
        stockLevels.put(product, currentStock - quantity);
        lastUpdated = new Date();
        System.out.println("Removed " + quantity + " units of " + product.getName() + 
                         " from " + locationId + ". New stock: " + (currentStock - quantity));
        
        return true;
    }
    
    // Method to check if a product is in stock
    public boolean isInStock(Product product, int quantityNeeded) {
        int currentStock = stockLevels.getOrDefault(product, 0);
        return currentStock >= quantityNeeded;
    }
    
    // Method to set reorder threshold
    public void setReorderThreshold(Product product, int threshold) {
        reorderThresholds.put(product, threshold);
    }
    
    // Method to check products that need reordering
    public List<Product> checkLowStock() {
        List<Product> lowStockProducts = new ArrayList<>();
        
        for (Map.Entry<Product, Integer> entry : stockLevels.entrySet()) {
            Product product = entry.getKey();
            int currentStock = entry.getValue();
            int threshold = reorderThresholds.getOrDefault(product, 0);
            
            if (currentStock <= threshold) {
                lowStockProducts.add(product);
            }
        }
        
        return lowStockProducts;
    }
    
    // Method to get current stock count
    public int getStockCount(Product product) {
        return stockLevels.getOrDefault(product, 0);
    }
    
    // Method to get products by category
    public Map<Product, Integer> getProductsByCategory(String category) {
        Map<Product, Integer> result = new HashMap<>();
        
        for (Map.Entry<Product, Integer> entry : stockLevels.entrySet()) {
                            Product product = entry.getKey();
            
            if (category.equals(product.getCategory())) {
                result.put(product, entry.getValue());
            }
        }
        
        return result;
    }
    
    // Get total inventory value
    public double getTotalInventoryValue() {
        double total = 0.0;
        
        for (Map.Entry<Product, Integer> entry : stockLevels.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            total += product.getPrice() * quantity;
        }
        
        return total;
    }
    
    // Getters and setters
    public String getLocationId() {
        return locationId;
    }
    
    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }
    
    public String getLocationType() {
        return locationType;
    }
    
    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }
    
    public Map<Product, Integer> getStockLevels() {
        return stockLevels;
    }
    
    public Map<Product, Integer> getReorderThresholds() {
        return reorderThresholds;
    }
    
    public Date getLastUpdated() {
        return lastUpdated;
    }
    
    @Override
    public String toString() {
        return String.format("Inventory[location=%s, type=%s, products=%d, lastUpdated=%s]", 
                            locationId, locationType, stockLevels.size(), lastUpdated);
    }
}