package com.smartsupply.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Class representing an order in the system
 */
public class Order implements Serializable {
    private String orderId;
    private Map<Product, Integer> orderItems; // Product and quantity
    private String placedByUserId;
    private String supplierUserId;
    private Date orderDate;
    private Date deliveryDate;
    private OrderStatus status;
    private boolean isPaid;
    private double totalAmount;
    private String shippingAddress;
    private int priority;
    private boolean isUrgent;
    private String notes;
    
    // Default constructor
    public Order() {
        this.orderId = UUID.randomUUID().toString();
        this.orderItems = new HashMap<>();
        this.orderDate = new Date();
        this.status = OrderStatus.PLACED;
        this.isPaid = false;
        this.priority = 3; // Default priority (1=highest, 5=lowest)
        this.isUrgent = false;
    }
    
    // Basic constructor
    public Order(String orderId, Map<Product, Integer> orderItems, 
                String placedByUserId, String supplierUserId) {
        this();
        this.orderId = orderId;
        this.orderItems = orderItems;
        this.placedByUserId = placedByUserId;
        this.supplierUserId = supplierUserId;
        this.calculateTotal(); // Calculate total when creating the order
    }
    
    // Constructor with delivery details
    public Order(String orderId, Map<Product, Integer> orderItems, 
                String placedByUserId, String supplierUserId,
                String shippingAddress, Date deliveryDate) {
        this(orderId, orderItems, placedByUserId, supplierUserId);
        this.shippingAddress = shippingAddress;
        this.deliveryDate = deliveryDate;
    }
    
    // Varargs constructor for additional options
    public Order(String orderId, Map<Product, Integer> orderItems, 
                String placedByUserId, String supplierUserId,
                Object... options) {
        this(orderId, orderItems, placedByUserId, supplierUserId);
        
        for (int i = 0; i < options.length; i += 2) {
            if (i + 1 < options.length) {
                String key = options[i].toString();
                Object value = options[i + 1];
                
                switch (key) {
                    case "shippingAddress":
                        this.shippingAddress = (String) value;
                        break;
                    case "deliveryDate":
                        this.deliveryDate = (Date) value;
                        break;
                    case "priority":
                        this.priority = (Integer) value;
                        break;
                    case "urgent":
                        this.isUrgent = (Boolean) value;
                        break;
                    case "notes":
                        this.notes = (String) value;
                        break;
                }
            }
        }
    }
    
    // Method to calculate total order amount
    public double calculateTotal() {
        totalAmount = 0.0;
        for (Map.Entry<Product, Integer> entry : orderItems.entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();
            totalAmount += product.getPrice() * quantity;
        }
        return totalAmount;
    }
    
    // Method to update order status
    public void updateStatus(OrderStatus newStatus) {
        OrderStatus oldStatus = this.status;
        this.status = newStatus;
        System.out.println("Order " + orderId + " status updated from " + 
                         oldStatus + " to " + newStatus);
        
        // Additional logic based on the new status
        if (newStatus == OrderStatus.DELIVERED) {
            // Mark as complete in system, etc.
        }
    }
    
    // Method to add item to order
    public void addItem(Product product, int quantity) {
        int currentQuantity = orderItems.getOrDefault(product, 0);
        orderItems.put(product, currentQuantity + quantity);
        calculateTotal(); // Recalculate total
    }
    
    // Method to remove item from order
    public void removeItem(Product product) {
        orderItems.remove(product);
        calculateTotal(); // Recalculate total
    }
    
    // Method to update item quantity
    public void updateItemQuantity(Product product, int newQuantity) {
        if (newQuantity <= 0) {
            orderItems.remove(product);
        } else {
            orderItems.put(product, newQuantity);
        }
        calculateTotal(); // Recalculate total
    }
    
    // Method to mark order as paid
    public void markAsPaid() {
        this.isPaid = true;
        System.out.println("Order " + orderId + " marked as paid");
    }
    
    // Method to generate invoice
    public Map<String, Object> generateInvoice() {
        Map<String, Object> invoice = new HashMap<>();
        invoice.put("invoiceId", "INV-" + orderId);
        invoice.put("orderId", orderId);
        invoice.put("orderDate", orderDate);
        invoice.put("customerUserId", placedByUserId);
        invoice.put("supplierUserId", supplierUserId);
        invoice.put("items", orderItems);
        invoice.put("totalAmount", totalAmount);
        invoice.put("status", status);
        invoice.put("isPaid", isPaid);
        
        return invoice;
    }
    
    // Getters and setters
    public String getOrderId() {
        return orderId;
    }
    
    public Map<Product, Integer> getOrderItems() {
        return orderItems;
    }
    
    public String getPlacedByUserId() {
        return placedByUserId;
    }
    
    public void setPlacedByUserId(String placedByUserId) {
        this.placedByUserId = placedByUserId;
    }
    
    public String getSupplierUserId() {
        return supplierUserId;
    }
    
    public void setSupplierUserId(String supplierUserId) {
        this.supplierUserId = supplierUserId;
    }
    
    public Date getOrderDate() {
        return orderDate;
    }
    
    public Date getDeliveryDate() {
        return deliveryDate;
    }
    
    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public boolean isPaid() {
        return isPaid;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public String getShippingAddress() {
        return shippingAddress;
    }
    
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        if (priority >= 1 && priority <= 5) {
            this.priority = priority;
        }
    }
    
    public boolean isUrgent() {
        return isUrgent;
    }
    
    public void setUrgent(boolean urgent) {
        isUrgent = urgent;
        if (urgent) {
            this.priority = 1; // Set to highest priority if urgent
        }
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public String toString() {
        return String.format("Order[id=%s, items=%d, total=%.2f, status=%s]",
                            orderId, orderItems.size(), totalAmount, status);
    }
}