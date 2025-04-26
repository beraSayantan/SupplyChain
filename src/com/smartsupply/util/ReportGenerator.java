package com.smartsupply.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.smartsupply.model.Inventory;
import com.smartsupply.model.Order;
import com.smartsupply.model.Product;

/**
 * Utility class for generating various reports
 */
public class ReportGenerator {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    // Method to generate inventory report
    public static String generateInventoryReport(Inventory inventory) {
        StringBuilder report = new StringBuilder();
        
        report.append("======= INVENTORY REPORT =======\n");
        report.append("Location: ").append(inventory.getLocationId()).append(" (").append(inventory.getLocationType()).append(")\n");
        report.append("Report Date: ").append(DATE_FORMAT.format(new Date())).append("\n");
        report.append("Last Inventory Update: ").append(DATE_FORMAT.format(inventory.getLastUpdated())).append("\n\n");
        
        report.append("PRODUCT INVENTORY:\n");
        report.append(String.format("%-40s %-10s %-15s %-15s\n", "Product", "Stock", "Price", "Value"));
        report.append("------------------------------------------------------------------------------\n");
        
        double totalValue = 0.0;
        
        for (Map.Entry<Product, Integer> entry : inventory.getStockLevels().entrySet()) {
            Product product = entry.getKey();
            Integer stock = entry.getValue();
            double value = product.getPrice() * stock;
            totalValue += value;
            
            report.append(String.format("%-40s %-10d $%-14.2f $%-14.2f\n", 
                                      product.getName(), stock, product.getPrice(), value));
        }
        
        report.append("------------------------------------------------------------------------------\n");
        report.append(String.format("TOTAL INVENTORY VALUE: $%.2f\n", totalValue));
        
        return report.toString();
    }
    
    // Method to generate sales report
    public static String generateSalesReport(List<Order> orders, Date startDate, Date endDate) {
        StringBuilder report = new StringBuilder();
        
        report.append("======= SALES REPORT =======\n");
        report.append("Period: ").append(DATE_FORMAT.format(startDate))
              .append(" to ").append(DATE_FORMAT.format(endDate)).append("\n");
        report.append("Report Date: ").append(DATE_FORMAT.format(new Date())).append("\n\n");
        
        report.append("ORDERS SUMMARY:\n");
        report.append(String.format("%-15s %-15s %-15s %-10s %-15s\n", 
                                  "Order ID", "Date", "Customer", "Items", "Total"));
        report.append("------------------------------------------------------------------\n");
        
        double totalSales = 0.0;
        int totalItems = 0;
        int orderCount = 0;
        
        for (Order order : orders) {
            Date orderDate = order.getOrderDate();
            
            // Only include orders within the date range
            if (orderDate.after(startDate) && orderDate.before(endDate)) {
                int itemCount = 0;
                for (Integer qty : order.getOrderItems().values()) {
                    itemCount += qty;
                }
                
                totalItems += itemCount;
                totalSales += order.getTotalAmount();
                orderCount++;
                
                report.append(String.format("%-15s %-15s %-15s %-10d $%-14.2f\n", 
                                         order.getOrderId(), 
                                         DATE_FORMAT.format(orderDate).substring(0, 10), 
                                         order.getPlacedByUserId().substring(0, 10), 
                                         itemCount, 
                                         order.getTotalAmount()));
            }
        }
        
        report.append("------------------------------------------------------------------\n");
        report.append(String.format("Orders: %d   Items: %d   Total Sales: $%.2f\n", 
                                  orderCount, totalItems, totalSales));
        
        if (orderCount > 0) {
            report.append(String.format("Average Order Value: $%.2f\n", totalSales / orderCount));
        }
        
        return report.toString();
    }
    
    // Method to generate low stock alert report
    public static String generateLowStockReport(Inventory inventory) {
        StringBuilder report = new StringBuilder();
        
        report.append("======= LOW STOCK ALERT REPORT =======\n");
        report.append("Location: ").append(inventory.getLocationId()).append(" (").append(inventory.getLocationType()).append(")\n");
        report.append("Report Date: ").append(DATE_FORMAT.format(new Date())).append("\n\n");
        
        List<Product> lowStockProducts = inventory.checkLowStock();
        
        if (lowStockProducts.isEmpty()) {
            report.append("No products are below their reorder threshold.\n");
        } else {
            report.append("The following products are below their reorder threshold:\n");
            report.append(String.format("%-40s %-10s %-15s\n", "Product", "Stock", "Threshold"));
            report.append("----------------------------------------------------------\n");
            
            for (Product product : lowStockProducts) {
                int stock = inventory.getStockCount(product);
                int threshold = inventory.getReorderThresholds().getOrDefault(product, 0);
                
                report.append(String.format("%-40s %-10d %-15d\n", 
                                          product.getName(), stock, threshold));
            }
        }
        
        return report.toString();
    }
    
    // Method to save report to file
    public static boolean saveReportToFile(String report, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(report);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Method to generate and save inventory report
    public static String generateAndSaveInventoryReport(Inventory inventory, String directory) {
        String report = generateInventoryReport(inventory);
        
        // Ensure directory exists
        FileHandler.ensureDirectoryExists(directory);
        
        // Create filename with timestamp
        String filename = String.format("inventory_report_%s_%s.txt", 
                                       inventory.getLocationId(), 
                                       new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
        
        String filePath = directory + File.separator + filename;
        
        if (saveReportToFile(report, filePath)) {
            return filePath;
        } else {
            return null;
        }
    }
    
    // Method to read user input for report generation
    public static Map<String, Object> getReportParameters() {
        Map<String, Object> params = new HashMap<>();
        try (Scanner scanner = new Scanner(System.in)) {
        
            System.out.println("===== Report Generation =====");
            System.out.println("1. Inventory Report");
            System.out.println("2. Sales Report");
            System.out.println("3. Low Stock Report");
            System.out.print("Select report type (1-3): ");
            
            int reportType = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            params.put("reportType", reportType);
            
            if (reportType == 2) { // Sales Report
                System.out.print("Enter start date (YYYY-MM-DD): ");
                String startDateStr = scanner.nextLine();
                
                System.out.print("Enter end date (YYYY-MM-DD): ");
                String endDateStr = scanner.nextLine();
                
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date startDate = dateFormat.parse(startDateStr);
                    Date endDate = dateFormat.parse(endDateStr);
                    
                    params.put("startDate", startDate);
                    params.put("endDate", endDate);
                } catch (Exception e) {
                    System.out.println("Invalid date format. Using current date range.");
                    // Use default date range (last 30 days)
                    Date endDate = new Date();
                    Date startDate = new Date(endDate.getTime() - 30 * 24 * 60 * 60 * 1000L);
                    params.put("startDate", startDate);
                    params.put("endDate", endDate);
                }
            }
            
            System.out.print("Enter output directory: ");
            String directory = scanner.nextLine();
            params.put("directory", directory);
            
            return params;
        }
    }
}