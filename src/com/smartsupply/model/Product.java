package com.smartsupply.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Class representing a product in the inventory system
 */
public class Product implements Serializable {
    private String productId;
    private String name;
    private double price;
    private String description;
    private String category;
    private String supplierUserId;
    private String barcode;
    private String qrCode;
    private Date createdDate;
    private boolean active;
    private Map<String, Object> attributes; // For flexible product attributes
    
    // Nested class for product dimensions - demonstrates nested class requirement
    public static class Dimensions {
        private double length;
        private double width;
        private double height;
        private double weight;
        private String unitOfMeasure;
        
        public Dimensions() {
            this.unitOfMeasure = "cm";
        }
        
        public Dimensions(double length, double width, double height, double weight) {
            this();
            this.length = length;
            this.width = width;
            this.height = height;
            this.weight = weight;
        }
        
        public Dimensions(double length, double width, double height, double weight, String unitOfMeasure) {
            this(length, width, height, weight);
            this.unitOfMeasure = unitOfMeasure;
        }
        
        public double getVolume() {
            return length * width * height;
        }
        
        // Getters and setters
        public double getLength() {
            return length;
        }
        
        public void setLength(double length) {
            this.length = length;
        }
        
        public double getWidth() {
            return width;
        }
        
        public void setWidth(double width) {
            this.width = width;
        }
        
        public double getHeight() {
            return height;
        }
        
        public void setHeight(double height) {
            this.height = height;
        }
        
        public double getWeight() {
            return weight;
        }
        
        public void setWeight(double weight) {
            this.weight = weight;
        }
        
        public String getUnitOfMeasure() {
            return unitOfMeasure;
        }
        
        public void setUnitOfMeasure(String unitOfMeasure) {
            this.unitOfMeasure = unitOfMeasure;
        }
        
        @Override
        public String toString() {
            return String.format("%.1fx%.1fx%.1f %s, %.2f kg", 
                                length, width, height, unitOfMeasure, weight);
        }
    }
    
    private Dimensions dimensions;
    
    // Default constructor
    public Product() {
        this.productId = UUID.randomUUID().toString();
        this.createdDate = new Date();
        this.active = true;
        this.attributes = new HashMap<>();
    }
    
    // Basic constructor
    public Product(String productId, String name, double price) {
        this();
        this.productId = productId;
        this.name = name;
        this.price = price;
    }
    
    // Constructor with more details
    public Product(String productId, String name, double price, 
                  String description, String category, String supplierUserId) {
        this(productId, name, price);
        this.description = description;
        this.category = category;
        this.supplierUserId = supplierUserId;
    }
    
    // Varargs constructor for flexible attributes
    public Product(String productId, String name, double price, 
                  String description, String category, String supplierUserId,
                  Object... attributePairs) {
        this(productId, name, price, description, category, supplierUserId);
        
        for (int i = 0; i < attributePairs.length; i += 2) {
            if (i + 1 < attributePairs.length) {
                String key = attributePairs[i].toString();
                Object value = attributePairs[i + 1];
                this.attributes.put(key, value);
            }
        }
    }
    
    // Method to generate a barcode
    public String generateBarcode() {
        if (this.barcode == null) {
            this.barcode = "BAR-" + productId.replace("-", "").substring(0, 8).toUpperCase();
        }
        return this.barcode;
    }
    
    // Method to generate a QR code
    public String generateQRCode() {
        if (this.qrCode == null) {
            this.qrCode = "QR-" + productId.replace("-", "").substring(0, 12).toUpperCase();
        }
        return this.qrCode;
    }
    
    // Method to get complete product details
    public Map<String, Object> getProductDetails() {
        Map<String, Object> details = new HashMap<>();
        details.put("productId", productId);
        details.put("name", name);
        details.put("price", price);
        details.put("description", description);
        details.put("category", category);
        details.put("supplierUserId", supplierUserId);
        details.put("barcode", generateBarcode());
        details.put("qrCode", generateQRCode());
        details.put("createdDate", createdDate);
        details.put("active", active);
        
        if (dimensions != null) {
            details.put("dimensions", dimensions.toString());
            details.put("volume", dimensions.getVolume());
        }
        
        // Include all custom attributes
        details.putAll(attributes);
        
        return details;
    }
    
    // Getters and setters
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getSupplierUserId() {
        return supplierUserId;
    }
    
    public void setSupplierUserId(String supplierUserId) {
        this.supplierUserId = supplierUserId;
    }
    
    public String getBarcode() {
        return (barcode != null) ? barcode : generateBarcode();
    }
    
    public String getQrCode() {
        return (qrCode != null) ? qrCode : generateQRCode();
    }
    
    public Date getCreatedDate() {
        return createdDate;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public Dimensions getDimensions() {
        return dimensions;
    }
    
    public void setDimensions(Dimensions dimensions) {
        this.dimensions = dimensions;
    }
    
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    public void addAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }
    
    public Object getAttribute(String key) {
        return this.attributes.get(key);
    }
    
    public void removeAttribute(String key) {
        this.attributes.remove(key);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return Objects.equals(productId, product.productId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
    
    @Override
    public String toString() {
        return String.format("Product[id=%s, name=%s, price=%.2f, category=%s]", 
                            productId, name, price, category);
    }
}
