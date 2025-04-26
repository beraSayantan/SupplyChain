package com.smartsupply.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.smartsupply.model.Order;
import com.smartsupply.model.Product;
import com.smartsupply.model.User;

/**
 * Service for sending notifications to users
 */
public class NotificationService {
    private List<User> subscribers;
    
    // Default constructor
    public NotificationService() {
        this.subscribers = new ArrayList<>();
    }
    
    // Constructor with initial subscribers
    public NotificationService(List<User> subscribers) {
        this.subscribers = subscribers;
    }
    
    // Method to send low stock alert
    public void sendLowStockAlert(Product product, int currentStock, int threshold) {
        String message = String.format(
            "LOW STOCK ALERT: %s (ID: %s) is below threshold. Current: %d, Threshold: %d",
            product.getName(), product.getProductId(), currentStock, threshold);
        
        for (User user : subscribers) {
            // In a real implementation, this would send an actual notification
            System.out.println("Sending to " + user.getName() + ": " + message);
        }
    }
    
    // Overloaded method for multiple low stock items
    public void sendLowStockAlert(List<Product> products, Map<Product, Integer> stockLevels) {
        StringBuilder message = new StringBuilder("LOW STOCK ALERT: Multiple products need reordering:\n");
        
        for (Product product : products) {
            int currentStock = stockLevels.getOrDefault(product, 0);
            message.append(String.format("- %s (ID: %s): Current: %d\n", 
                                       product.getName(), product.getProductId(), currentStock));
        }
        
        for (User user : subscribers) {
            // In a real implementation, this would send an actual notification
            System.out.println("Sending to " + user.getName() + ": " + message.toString());
        }
    }
    
    // Varargs method for low stock alerts
    public void sendLowStockAlert(Object... productInfo) {
        StringBuilder message = new StringBuilder("LOW STOCK ALERT:\n");
        
        for (int i = 0; i < productInfo.length; i += 3) {
            if (i + 2 < productInfo.length) {
                Product product = (Product) productInfo[i];
                Integer currentStock = (Integer) productInfo[i + 1];
                Integer threshold = (Integer) productInfo[i + 2];
                
                message.append(String.format("- %s (ID: %s): Current: %d, Threshold: %d\n", 
                                           product.getName(), product.getProductId(), 
                                           currentStock, threshold));
            }
        }
        
        for (User user : subscribers) {
            // In a real implementation, this would send an actual notification
            System.out.println("Sending to " + user.getName() + ": " + message.toString());
        }
    }
    
    // Method to send order update notification
    public void sendOrderUpdate(Order order, String message) {
        String notification = String.format(
            "ORDER UPDATE: Order %s status: %s. %s",
            order.getOrderId(), order.getStatus(), message);
        
        // Notify the user who placed the order
        // In a real implementation, this would retrieve the user and send notification
        System.out.println("Sending to order placer: " + notification);
    }
    
    // Method to send system notification
    public void sendSystemNotification(String subject, String message) {
        String notification = String.format("SYSTEM NOTIFICATION: %s - %s", subject, message);
        
        for (User user : subscribers) {
            // In a real implementation, this would send an actual notification
            System.out.println("Sending to " + user.getName() + ": " + notification);
        }
    }
    
    // Method to subscribe a user to notifications
    public void subscribe(User user) {
        if (!subscribers.contains(user)) {
            subscribers.add(user);
            System.out.println(user.getName() + " subscribed to notifications");
        }
    }
    
    // Method to unsubscribe a user from notifications
    public void unsubscribe(User user) {
        subscribers.remove(user);
        System.out.println(user.getName() + " unsubscribed from notifications");
    }
    
    // Getter for subscribers
    public List<User> getSubscribers() {
        return subscribers;
    }
}