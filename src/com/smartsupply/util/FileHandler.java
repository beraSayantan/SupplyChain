package com.smartsupply.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.smartsupply.model.Product;
import com.smartsupply.model.Order;

/**
 * Utility class for file operations
 */
public class FileHandler {
    
    // Method to export products to CSV
    public static boolean exportProductsToCSV(List<Product> products, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write CSV header
            writer.write("ProductID,Name,Price,Category,SupplierID,Barcode,Active\n");
            
            // Write product data
            for (Product product : products) {
                writer.write(String.format("%s,%s,%.2f,%s,%s,%s,%b\n",
                    product.getProductId(),
                    product.getName().replace(",", ";"), // Escape commas
                    product.getPrice(),
                    product.getCategory(),
                    product.getSupplierUserId(),
                    product.getBarcode(),
                    product.isActive()));
            }
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Method to import products from CSV
    public static List<Product> importProductsFromCSV(String filePath) {
        List<Product> products = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;
            
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue; // Skip header
                }
                
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    String productId = parts[0].trim();
                    String name = parts[1].trim();
                    double price = Double.parseDouble(parts[2].trim());
                    String category = parts[3].trim();
                    String supplierId = parts[4].trim();
                    String barcode = parts[5].trim();
                    boolean active = Boolean.parseBoolean(parts[6].trim());
                    
                    Product product = new Product(productId, name, price);
                    product.setCategory(category);
                    product.setSupplierUserId(supplierId);
                    if (barcode != null && !barcode.isEmpty()) {
                        // In a real implementation, you'd set the barcode directly
                        // For simplicity, we'll just generate a new one
                        product.generateBarcode();
                    }
                    product.setActive(active);
                    
                    products.add(product);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return products;
    }
    
    // Method to export orders to CSV
    public static boolean exportOrdersToCSV(List<Order> orders, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write CSV header
            writer.write("OrderID,PlacedBy,Supplier,OrderDate,Status,TotalAmount,ItemCount\n");
            
            // Write order data
            for (Order order : orders) {
                writer.write(String.format("%s,%s,%s,%s,%s,%.2f,%d\n",
                    order.getOrderId(),
                    order.getPlacedByUserId(),
                    order.getSupplierUserId(),
                    order.getOrderDate(),
                    order.getStatus(),
                    order.getTotalAmount(),
                    order.getOrderItems().size()));
            }
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Method to serialize an object to file
    public static boolean serializeObject(Object obj, String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(obj);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Method to deserialize an object from file
    @SuppressWarnings("unchecked")
    public static <T> T deserializeObject(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Method to save a report to a text file
    public static boolean saveReportToFile(Map<String, Object> report, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("========= REPORT =========\n");
            writer.write("Generated: " + new java.util.Date() + "\n\n");
            
            for (Map.Entry<String, Object> entry : report.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
            }
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Method to ensure a directory exists
    public static boolean ensureDirectoryExists(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            return directory.mkdirs();
        }
        return true;
    }
    
    // Varargs method to delete multiple files
    public static boolean deleteFiles(String... filePaths) {
        boolean allDeleted = true;
        
        for (String filePath : filePaths) {
            File file = new File(filePath);
            if (file.exists()) {
                if (!file.delete()) {
                    allDeleted = false;
                }
            }
        }
        
        return allDeleted;
    }
}
