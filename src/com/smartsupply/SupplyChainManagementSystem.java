package com.smartsupply;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.smartsupply.exception.AuthenticationException;
import com.smartsupply.exception.InventoryException;
import com.smartsupply.model.Admin;
import com.smartsupply.model.Inventory;
import com.smartsupply.model.Order;
import com.smartsupply.model.OrderStatus;
import com.smartsupply.model.Product;
import com.smartsupply.model.Retailer;
import com.smartsupply.model.Role;
import com.smartsupply.model.Supplier;
import com.smartsupply.model.User;
import com.smartsupply.model.WarehouseManager;
import com.smartsupply.service.Analytics;
import com.smartsupply.service.AnalyticsImpl;
import com.smartsupply.service.Authentication;
import com.smartsupply.service.NotificationService;
import com.smartsupply.util.BarcodeScanner;
import com.smartsupply.util.FileHandler;
import com.smartsupply.util.ReportGenerator;

/**
 * Main class for the Smart Supply Chain Management System
 * Demonstrates how all components work together
 */
public class SupplyChainManagementSystem {
    private Authentication authService;
    private NotificationService notificationService;
    private Analytics analyticsService;
    private Map<String, Inventory> inventories;
    private List<Order> orders;
    private static User currentUser;
    private static final String DATA_DIR = "data";
    private static Scanner scanner;
    
    // Default constructor
    public SupplyChainManagementSystem() {
        this.authService = new Authentication(DATA_DIR + File.separator + "users.dat");
        this.notificationService = new NotificationService();
        this.analyticsService = new AnalyticsImpl();
        this.inventories = new HashMap<>();
        this.orders = new ArrayList<>();
        scanner = new Scanner(System.in);
        
        // Ensure data directory exists
        FileHandler.ensureDirectoryExists(DATA_DIR);
        
        // Initialize with sample data
        initializeSampleData();
    }
    
    // Method to initialize sample data
    private void initializeSampleData() {
        try {
            // Create admin user
            Admin admin = new Admin("admin1", "System Admin", "password123", 1);
            authService.registerUser(admin);
            
            // Create supplier
            Supplier supplier = new Supplier("supplier1", "Tech Supplies Inc.", "password123", 
                                          "Tech Supplies Inc.", "123 Supplier St.", "Electronics", "Computers");
            authService.registerUser(supplier);
            
            // Create warehouse manager
            WarehouseManager warehouseManager = new WarehouseManager("warehouse1", "Main Warehouse", "password123",
                                                                 "WH-001", "456 Warehouse Blvd.", 5000.0);
            authService.registerUser(warehouseManager);
            
            // Create retailer
            Retailer retailer = new Retailer("retailer1", "Downtown Electronics", "password123",
                                          "ST-001", "789 Retail Ave.", 50000.0, "Electronics", "Computers");
            authService.registerUser(retailer);
            
            // Create products
            Product laptop = new Product("P-001", "High-Performance Laptop", 1200.00,
                                      "15.6-inch laptop with 16GB RAM", "Electronics", "supplier1");
            laptop.generateBarcode();
            laptop.generateQRCode();
            
            Product smartphone = new Product("P-002", "Smartphone X20", 800.00,
                                          "Latest smartphone model", "Electronics", "supplier1");
            smartphone.generateBarcode();
            smartphone.generateQRCode();
            
            Product tablet = new Product("P-003", "Tablet Pro", 600.00,
                                      "10-inch professional tablet", "Electronics", "supplier1");
            tablet.generateBarcode();
            tablet.generateQRCode();
            
            // Create warehouse inventory
            Inventory warehouseInventory = new Inventory("WH-001", "warehouse");
            warehouseInventory.addStock(laptop, 50);
            warehouseInventory.addStock(smartphone, 100);
            warehouseInventory.addStock(tablet, 75);
            
            // Set reorder thresholds
            warehouseInventory.setReorderThreshold(laptop, 10);
            warehouseInventory.setReorderThreshold(smartphone, 20);
            warehouseInventory.setReorderThreshold(tablet, 15);
            
            // Create store inventory
            Inventory storeInventory = new Inventory("ST-001", "store");
            storeInventory.addStock(laptop, 10);
            storeInventory.addStock(smartphone, 25);
            storeInventory.addStock(tablet, 15);
            
            // Set reorder thresholds
            storeInventory.setReorderThreshold(laptop, 3);
            storeInventory.setReorderThreshold(smartphone, 5);
            storeInventory.setReorderThreshold(tablet, 4);
            
            // Add inventories to the system
            inventories.put("WH-001", warehouseInventory);
            inventories.put("ST-001", storeInventory);
            
            // Register barcodes
            BarcodeScanner.registerBarcode(laptop.getBarcode(), laptop);
            BarcodeScanner.registerBarcode(smartphone.getBarcode(), smartphone);
            BarcodeScanner.registerBarcode(tablet.getBarcode(), tablet);
            
            // Create sample order
            Map<Product, Integer> orderItems = new HashMap<>();
            orderItems.put(laptop, 2);
            orderItems.put(smartphone, 5);
            
            Order order = new Order("ORD-001", orderItems, "retailer1", "supplier1",
                                 "789 Retail Ave.", new Date(System.currentTimeMillis() + 86400000));
            order.updateStatus(OrderStatus.PROCESSING);
            
            orders.add(order);
            
            // Subscribe users to notifications
            notificationService.subscribe(admin);
            notificationService.subscribe(warehouseManager);
            notificationService.subscribe(retailer);
            
            System.out.println("Sample data initialized successfully.");
            
        } catch (AuthenticationException e) {
            System.err.println("Error initializing sample data: " + e.getMessage());
        }
    }
    
    // Method to run the system
    public void run() {
        System.out.println("======================================================");
        System.out.println("    SMART SUPPLY CHAIN & INVENTORY MANAGEMENT SYSTEM   ");
        System.out.println("======================================================");
        
        boolean running = true;
        
        while (running) {
            try {
                if (currentUser == null) {
                    // Not logged in
                    showLoginMenu();
                } else {
                    // Logged in
                    showMainMenu();
                }
                
                System.out.print("\nEnter your choice: ");
                int choice = Integer.parseInt(scanner.nextLine());
                
                if (currentUser == null) {
                    switch (choice) {
                        case 1:
                            login();
                            break;
                        case 2:
                            running = false;
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                } else {
                    // Handle main menu selection based on user role
                    handleMainMenuSelection(choice);
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("Thank you for using the Smart Supply Chain Management System.");
    }
    
    // Method to show login menu
    private void showLoginMenu() {
        System.out.println("\n--- Login Menu ---");
        System.out.println("1. Login");
        System.out.println("2. Exit");
    }
    
    // Method to handle login
    private void login() {
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();
        
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        
        try {
            User user = authService.login(userId, password);
            currentUser = user;
            System.out.println("Login successful. Welcome, " + user.getName() + " (" + user.getRole() + ")");
        } catch (AuthenticationException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }
    
    // Method to show main menu based on user role
    private void showMainMenu() {
        System.out.println("\n--- Main Menu (" + currentUser.getRole() + ") ---");
        
        switch (currentUser.getRole()) {
            case ADMIN:
                System.out.println("1. Manage Users");
                System.out.println("2. Generate System Reports");
                System.out.println("3. View Analytics");
                System.out.println("4. Configure System");
                System.out.println("5. Logout");
                break;
                
            case SUPPLIER:
                System.out.println("1. View Orders");
                System.out.println("2. Update Product Prices");
                System.out.println("3. Process Orders");
                System.out.println("4. View Analytics");
                System.out.println("5. Logout");
                break;
                
            case WAREHOUSE_MANAGER:
                System.out.println("1. Manage Inventory");
                System.out.println("2. Receive Shipments");
                System.out.println("3. Process Outgoing Orders");
                System.out.println("4. Generate Inventory Reports");
                System.out.println("5. Logout");
                break;
                
            case RETAILER:
                System.out.println("1. View Inventory");
                System.out.println("2. Place Order");
                System.out.println("3. Scan Products");
                System.out.println("4. Record Sales");
                System.out.println("5. Logout");
                break;
                
            default:
                System.out.println("1. View Profile");
                System.out.println("2. Change Password");
                System.out.println("3. Logout");
        }
    }
    
    // Method to handle main menu selection
    private void handleMainMenuSelection(int choice) {
        switch (currentUser.getRole()) {
            case ADMIN:
                handleAdminMenuSelection(choice);
                break;
                
            case SUPPLIER:
                handleSupplierMenuSelection(choice);
                break;
                
            case WAREHOUSE_MANAGER:
                handleWarehouseManagerMenuSelection(choice);
                break;
                
            case RETAILER:
                handleRetailerMenuSelection(choice);
                break;
                
            default:
                handleDefaultMenuSelection(choice);
        }
    }
    
    // Method to handle admin menu selection
    private void handleAdminMenuSelection(int choice) {
        Admin admin = (Admin) currentUser;
        
        switch (choice) {
            case 1: // Manage Users
                manageUsers(admin);
                break;
                
            case 2: // Generate System Reports
                generateSystemReports(admin);
                break;
                
            case 3: // View Analytics
                viewAnalytics();
                break;
                
            case 4: // Configure System
                configureSystem(admin);
                break;
                
            case 5: // Logout
                logout();
                break;
                
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
    
    // Method to handle supplier menu selection
    private void handleSupplierMenuSelection(int choice) {
        Supplier supplier = (Supplier) currentUser;
        
        switch (choice) {
            case 1: // View Orders
                viewSupplierOrders(supplier);
                break;
                
            case 2: // Update Product Prices
                updateProductPrices(supplier);
                break;
                
            case 3: // Process Orders
                processSupplierOrders(supplier);
                break;
                
            case 4: // View Analytics
                viewAnalytics();
                break;
                
            case 5: // Logout
                logout();
                break;
                
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
    
    // Method to handle warehouse manager menu selection
    private void handleWarehouseManagerMenuSelection(int choice) {
        WarehouseManager warehouseManager = (WarehouseManager) currentUser;
        
        switch (choice) {
            case 1: // Manage Inventory
                manageWarehouseInventory(warehouseManager);
                break;
                
            case 2: // Receive Shipments
                receiveShipments(warehouseManager);
                break;
                
            case 3: // Process Outgoing Orders
                processOutgoingOrders(warehouseManager);
                break;
                
            case 4: // Generate Inventory Reports
                generateInventoryReports(warehouseManager);
                break;
                
            case 5: // Logout
                logout();
                break;
                
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
    
    // Method to handle retailer menu selection
    private void handleRetailerMenuSelection(int choice) {
        Retailer retailer = (Retailer) currentUser;
        
        switch (choice) {
            case 1: // View Inventory
                viewStoreInventory(retailer);
                break;
                
            case 2: // Place Order
                placeOrder(retailer);
                break;
                
            case 3: // Scan Products
                scanProducts(retailer);
                break;
                
            case 4: // Record Sales
                recordSales(retailer);
                break;
                
            case 5: // Logout
                logout();
                break;
                
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
    
    // Method to handle default menu selection
    private void handleDefaultMenuSelection(int choice) {
        switch (choice) {
            case 1: // View Profile
                viewUserProfile();
                break;
                
            case 2: // Change Password
                changePassword();
                break;
                
            case 3: // Logout
                logout();
                break;
                
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
    
    // Method to manage users (Admin)
    private void manageUsers(Admin admin) {
        System.out.println("\n--- User Management ---");
        System.out.println("1. List All Users");
        System.out.println("2. Add New User");
        System.out.println("3. Deactivate User");
        System.out.println("4. Back to Main Menu");
        
        System.out.print("Enter your choice: ");
        int choice = Integer.parseInt(scanner.nextLine());
        
        switch (choice) {
            case 1: // List All Users
                System.out.println("\nAll Users:");
                for (User user : authService.getUsers().values()) {
                    System.out.println(user);
                }
                break;
                
            case 2: // Add New User
                addNewUser(admin);
                break;
                
            case 3: // Deactivate User
                deactivateUser(admin);
                break;
                
            case 4: // Back to Main Menu
                // Do nothing, return to main menu
                break;
                
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
    
    // Method to add a new user
    private void addNewUser(Admin admin) {
        System.out.println("\n--- Add New User ---");
        
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();
        
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        
        System.out.println("Select Role:");
        System.out.println("1. Admin");
        System.out.println("2. Supplier");
        System.out.println("3. Warehouse Manager");
        System.out.println("4. Retailer");
        
        System.out.print("Enter role choice: ");
        int roleChoice = Integer.parseInt(scanner.nextLine());
        
        Role role;
        switch (roleChoice) {
            case 1:
                role = Role.ADMIN;
                break;
            case 2:
                role = Role.SUPPLIER;
                break;
            case 3:
                role = Role.WAREHOUSE_MANAGER;
                break;
            case 4:
                role = Role.RETAILER;
                break;
            default:
                System.out.println("Invalid role. Using Retailer as default.");
                role = Role.RETAILER;
        }
        
        try {
            User newUser = admin.createUser(userId, name, password, role);
            authService.registerUser(newUser);
            System.out.println("User created successfully: " + newUser);
        } catch (AuthenticationException e) {
            System.out.println("Error creating user: " + e.getMessage());
        }
    }
    
    // Method to deactivate a user
    private void deactivateUser(Admin admin) {
        System.out.println("\n--- Deactivate User ---");
        
        System.out.print("Enter User ID to deactivate: ");
        String userId = scanner.nextLine();
        
        User user = authService.getUser(userId);
        if (user != null) {
            user.setActive(false);
            authService.updateUser(user);
            System.out.println("User deactivated: " + user.getName());
            
            // If the user is the current user, log them out
            if (currentUser.getUserId().equals(userId)) {
                logout();
            }
        } else {
            System.out.println("User not found: " + userId);
        }
    }
    
    // Method to generate system reports (Admin)
    private void generateSystemReports(Admin admin) {
        System.out.println("\n--- Generate System Reports ---");
        System.out.println("1. Inventory Summary Report");
        System.out.println("2. Sales Summary Report");
        System.out.println("3. User Activity Report");
        System.out.println("4. Back to Main Menu");
        
        System.out.print("Enter your choice: ");
        int choice = Integer.parseInt(scanner.nextLine());
        
        switch (choice) {
            case 1: // Inventory Summary Report
                generateInventorySummaryReport();
                break;
                
            case 2: // Sales Summary Report
                generateSalesSummaryReport();
                break;
                
            case 3: // User Activity Report
                System.out.println("User Activity Report functionality coming soon.");
                break;
                
            case 4: // Back to Main Menu
                // Do nothing, return to main menu
                break;
                
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
    
    // Method to generate inventory summary report
    private void generateInventorySummaryReport() {
        System.out.println("\n--- Inventory Summary Report ---");
        
        StringBuilder report = new StringBuilder();
        report.append("======= INVENTORY SUMMARY REPORT =======\n");
        report.append("Report Date: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n\n");
        
        double totalValue = 0.0;
        int totalProducts = 0;
        
        for (Map.Entry<String, Inventory> entry : inventories.entrySet()) {
            String locationId = entry.getKey();
            Inventory inventory = entry.getValue();
            
            double locationValue = inventory.getTotalInventoryValue();
            int productCount = inventory.getStockLevels().size();
            
            totalValue += locationValue;
            totalProducts += productCount;
            
            report.append(String.format("Location: %s (%s)\n", locationId, inventory.getLocationType()));
            report.append(String.format("Products: %d\n", productCount));
            report.append(String.format("Total Value: $%.2f\n\n", locationValue));
        }
        
        report.append("=== SUMMARY ===\n");
        report.append(String.format("Total Locations: %d\n", inventories.size()));
        report.append(String.format("Total Products: %d\n", totalProducts));
        report.append(String.format("Total Inventory Value: $%.2f\n", totalValue));
        
        System.out.println(report.toString());
        
        // Save the report to file
        String filename = "inventory_summary_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt";
        if (ReportGenerator.saveReportToFile(report.toString(), DATA_DIR + File.separator + filename)) {
            System.out.println("Report saved to: " + DATA_DIR + File.separator + filename);
        } else {
            System.out.println("Failed to save report to file.");
        }
    }
    
    // Method to generate sales summary report
    private void generateSalesSummaryReport() {
        System.out.println("\n--- Sales Summary Report ---");
        
        System.out.print("Enter start date (YYYY-MM-DD): ");
        String startDateStr = scanner.nextLine();
        
        System.out.print("Enter end date (YYYY-MM-DD): ");
        String endDateStr = scanner.nextLine();
        
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);
            
            String report = ReportGenerator.generateSalesReport(orders, startDate, endDate);
            System.out.println(report);
            
            // Save the report to file
            String filename = "sales_report_" + startDateStr + "_to_" + endDateStr + ".txt";
            if (ReportGenerator.saveReportToFile(report, DATA_DIR + File.separator + filename)) {
                System.out.println("Report saved to: " + DATA_DIR + File.separator + filename);
            } else {
                System.out.println("Failed to save report to file.");
            }
        } catch (Exception e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }
    
    // Additional methods for system operation would be here...
    // (viewAnalytics, configureSystem, viewSupplierOrders, updateProductPrices, etc.)
    
    // Method to view analytics data
    private void viewAnalytics() {
        System.out.println("\n--- Analytics Dashboard ---");
        
        // Generate current date for default end date
        Date endDate = new Date();
        
        // Generate date 30 days ago for default start date
        Date startDate = new Date(endDate.getTime() - 30 * 24 * 60 * 60 * 1000L);
        
        // Analyze sales
        Map<String, Object> salesAnalysis = analyticsService.analyzeSales(startDate, endDate);
        
        // Display results
        System.out.println("Sales Analysis (Last 30 Days):");
        System.out.println(String.format("Total Sales: $%.2f", salesAnalysis.get("totalSales")));
        System.out.println(String.format("Average Daily Sales: $%.2f", salesAnalysis.get("averageDailySales")));
        
        @SuppressWarnings("unchecked")
        Map<String, Double> salesByCategory = (Map<String, Double>) salesAnalysis.get("salesByCategory");
        System.out.println("\nSales by Category:");
        for (Map.Entry<String, Double> entry : salesByCategory.entrySet()) {
            System.out.println(String.format("- %s: $%.2f", entry.getKey(), entry.getValue()));
        }
        
        // Key Performance Indicators
        Map<String, Double> kpis = analyticsService.generateKPIs();
        System.out.println("\nKey Performance Indicators:");
        System.out.println(String.format("Inventory Turnover Rate: %.1f", kpis.get("inventoryTurnoverRate")));
        System.out.println(String.format("Order Fulfillment Rate: %.1f%%", kpis.get("orderFulfillmentRate")));
        System.out.println(String.format("Average Order Value: $%.2f", kpis.get("averageOrderValue")));
        System.out.println(String.format("Out of Stock Rate: %.1f%%", kpis.get("outOfStockRate")));
        System.out.println(String.format("Return Rate: %.1f%%", kpis.get("returnRate")));
        System.out.println(String.format("Average Delivery Time: %.1f days", kpis.get("averageDeliveryTime")));
        
        // Anomalies
        List<String> anomalies = analyticsService.detectAnomalies();
        if (!anomalies.isEmpty()) {
            System.out.println("\nDetected Anomalies:");
            for (String anomaly : anomalies) {
                System.out.println("- " + anomaly);
            }
        }
    }
    
    // Method to configure system settings (Admin)
    private void configureSystem(Admin admin) {
        System.out.println("\n--- System Configuration ---");
        System.out.println("1. Configure Notifications");
        System.out.println("2. Configure Report Settings");
        System.out.println("3. Configure Data Backup");
        System.out.println("4. Back to Main Menu");
        
        System.out.print("Enter your choice: ");
        int choice = Integer.parseInt(scanner.nextLine());
        
        Map<String, Object> settings = new HashMap<>();
        
        switch (choice) {
            case 1: // Configure Notifications
                System.out.println("\n--- Notification Settings ---");
                System.out.print("Enable low stock alerts (true/false): ");
                boolean enableLowStock = Boolean.parseBoolean(scanner.nextLine());
                
                System.out.print("Enable order status notifications (true/false): ");
                boolean enableOrderStatus = Boolean.parseBoolean(scanner.nextLine());
                
                settings.put("enableLowStockAlerts", enableLowStock);
                settings.put("enableOrderStatusNotifications", enableOrderStatus);
                break;
                
            case 2: // Configure Report Settings
                System.out.println("\n--- Report Settings ---");
                System.out.print("Default report format (txt/pdf/csv): ");
                String reportFormat = scanner.nextLine();
                
                System.out.print("Auto-generate daily reports (true/false): ");
                boolean autoGenerate = Boolean.parseBoolean(scanner.nextLine());
                
                settings.put("defaultReportFormat", reportFormat);
                settings.put("autoGenerateReports", autoGenerate);
                break;
                
            case 3: // Configure Data Backup
                System.out.println("\n--- Data Backup Settings ---");
                System.out.print("Backup directory path: ");
                String backupPath = scanner.nextLine();
                
                System.out.print("Backup frequency in hours (24 for daily): ");
                int backupFrequency = Integer.parseInt(scanner.nextLine());
                
                settings.put("backupPath", backupPath);
                settings.put("backupFrequencyHours", backupFrequency);
                break;
                
            case 4: // Back to Main Menu
                return;
                
            default:
                System.out.println("Invalid choice. Please try again.");
                return;
        }
        
        if (admin.configureSystem(settings)) {
            System.out.println("System configuration updated successfully.");
        } else {
            System.out.println("Failed to update system configuration.");
        }
    }
    
    // Method to view supplier orders
    private void viewSupplierOrders(Supplier supplier) {
        System.out.println("\n--- Supplier Orders ---");
        
        boolean found = false;
        
        for (Order order : orders) {
            if (supplier.getUserId().equals(order.getSupplierUserId())) {
                found = true;
                
                System.out.println("\nOrder ID: " + order.getOrderId());
                System.out.println("Status: " + order.getStatus());
                System.out.println("Placed By: " + order.getPlacedByUserId());
                System.out.println("Order Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getOrderDate()));
                System.out.println("Delivery Date: " + (order.getDeliveryDate() != null ? 
                    new SimpleDateFormat("yyyy-MM-dd").format(order.getDeliveryDate()) : "Not set"));
                System.out.println("Total Amount: $" + String.format("%.2f", order.getTotalAmount()));
                
                System.out.println("\nOrder Items:");
                for (Map.Entry<Product, Integer> item : order.getOrderItems().entrySet()) {
                    Product product = item.getKey();
                    int quantity = item.getValue();
                    System.out.println(String.format("- %s (ID: %s) x %d = $%.2f", 
                        product.getName(), product.getProductId(), quantity, product.getPrice() * quantity));
                }
            }
        }
        
        if (!found) {
            System.out.println("No orders found for this supplier.");
        }
    }
    
    // Method to update product prices
    private void updateProductPrices(Supplier supplier) {
        System.out.println("\n--- Update Product Prices ---");
        
        // Find products supplied by this supplier
        List<Product> supplierProducts = new ArrayList<>();
        
        for (Inventory inventory : inventories.values()) {
            for (Product product : inventory.getStockLevels().keySet()) {
                if (supplier.getUserId().equals(product.getSupplierUserId()) && !supplierProducts.contains(product)) {
                    supplierProducts.add(product);
                }
            }
        }
        
        if (supplierProducts.isEmpty()) {
            System.out.println("No products found for this supplier.");
            return;
        }
        
        // Display products
        System.out.println("Products supplied by " + supplier.getName() + ":");
        for (int i = 0; i < supplierProducts.size(); i++) {
            Product product = supplierProducts.get(i);
            System.out.println((i + 1) + ". " + product.getName() + " (ID: " + product.getProductId() + 
                             ") - Current Price: $" + String.format("%.2f", product.getPrice()));
        }
        
        System.out.print("\nEnter product number to update price (0 to cancel): ");
        int productIndex = Integer.parseInt(scanner.nextLine()) - 1;
        
        if (productIndex < 0 || productIndex >= supplierProducts.size()) {
            System.out.println("Operation cancelled or invalid selection.");
            return;
        }
        
        Product selectedProduct = supplierProducts.get(productIndex);
        
        System.out.print("Enter new price for " + selectedProduct.getName() + ": $");
        double newPrice = Double.parseDouble(scanner.nextLine());
        
        if (newPrice <= 0) {
            System.out.println("Invalid price. Price must be greater than zero.");
            return;
        }
        
        // Update the price
        selectedProduct.setPrice(newPrice);
        supplier.updateProductPrice(selectedProduct.getProductId(), newPrice);
        
        System.out.println("Price updated successfully. New price: $" + String.format("%.2f", newPrice));
    }
    
    // Method to process supplier orders
    private void processSupplierOrders(Supplier supplier) {
        System.out.println("\n--- Process Orders ---");
        
        // Find pending orders for this supplier
        List<Order> supplierOrders = new ArrayList<>();
        
        for (Order order : orders) {
            if (supplier.getUserId().equals(order.getSupplierUserId()) && 
                (order.getStatus() == OrderStatus.PLACED || order.getStatus() == OrderStatus.PROCESSING)) {
                supplierOrders.add(order);
            }
        }
        
        if (supplierOrders.isEmpty()) {
            System.out.println("No pending orders found for processing.");
            return;
        }
        
        // Display orders
        System.out.println("Pending orders for " + supplier.getName() + ":");
        for (int i = 0; i < supplierOrders.size(); i++) {
            Order order = supplierOrders.get(i);
            System.out.println((i + 1) + ". Order ID: " + order.getOrderId() + 
                             " - Status: " + order.getStatus() + 
                             " - Total: $" + String.format("%.2f", order.getTotalAmount()));
        }
        
        System.out.print("\nEnter order number to process (0 to cancel): ");
        int orderIndex = Integer.parseInt(scanner.nextLine()) - 1;
        
        if (orderIndex < 0 || orderIndex >= supplierOrders.size()) {
            System.out.println("Operation cancelled or invalid selection.");
            return;
        }
        
        Order selectedOrder = supplierOrders.get(orderIndex);
        
        System.out.println("\nSelect new status for order " + selectedOrder.getOrderId() + ":");
        System.out.println("1. Processing");
        System.out.println("2. Shipped");
        System.out.println("3. Cancelled");
        
        System.out.print("Enter choice: ");
        int statusChoice = Integer.parseInt(scanner.nextLine());
        
        OrderStatus newStatus;
        switch (statusChoice) {
            case 1:
                newStatus = OrderStatus.PROCESSING;
                break;
            case 2:
                newStatus = OrderStatus.SHIPPED;
                break;
            case 3:
                newStatus = OrderStatus.CANCELLED;
                break;
            default:
                System.out.println("Invalid choice. Operation cancelled.");
                return;
        }
        
        // Update order status
        selectedOrder.updateStatus(newStatus);
        
        // Send notification
        notificationService.sendOrderUpdate(selectedOrder, 
            "Order has been updated to " + newStatus + " by supplier " + supplier.getName());
        
        // If order is shipped, update expected delivery date
        if (newStatus == OrderStatus.SHIPPED) {
            // Set delivery date to 3 days from now
            Date deliveryDate = new Date(System.currentTimeMillis() + (3 * 24 * 60 * 60 * 1000L));
            selectedOrder.setDeliveryDate(deliveryDate);
            System.out.println("Expected delivery date set to: " + 
                             new SimpleDateFormat("yyyy-MM-dd").format(deliveryDate));
        }
        
        System.out.println("Order " + selectedOrder.getOrderId() + " has been updated to " + newStatus);
        
        // Process order if supplier agrees
        if (newStatus == OrderStatus.PROCESSING || newStatus == OrderStatus.SHIPPED) {
            boolean processed = supplier.processPurchaseOrder(selectedOrder);
            if (processed) {
                System.out.println("Order processed successfully.");
            } else {
                System.out.println("Error processing order.");
            }
        }
    }
    
    // Method to manage warehouse inventory
    private void manageWarehouseInventory(WarehouseManager warehouseManager) {
        System.out.println("\n--- Manage Warehouse Inventory ---");
        
        // Find the inventory for this warehouse manager
        String warehouseId = warehouseManager.getWarehouseId();
        Inventory inventory = inventories.get(warehouseId);
        
        // Create a new inventory if it doesn't exist
        if (inventory == null) {
            inventory = new Inventory(warehouseId, "warehouse");
            inventories.put(warehouseId, inventory);
            System.out.println("New inventory created for warehouse ID: " + warehouseId);
        }
        
        System.out.println("Warehouse: " + warehouseId + " (" + warehouseManager.getLocation() + ")");
        System.out.println("Total Items: " + inventory.getStockLevels().size());
        System.out.println("Total Value: $" + String.format("%.2f", inventory.getTotalInventoryValue()));
        
        System.out.println("\n1. View Inventory");
        System.out.println("2. Add Stock");
        System.out.println("3. Remove Stock");
        System.out.println("4. Set Reorder Thresholds");
        System.out.println("5. Check Low Stock Items");
        System.out.println("6. Back to Main Menu");
        
        System.out.print("\nEnter your choice: ");
        int choice = Integer.parseInt(scanner.nextLine());
        
        switch (choice) {
            case 1: // View Inventory
                System.out.println("\nCurrent Inventory:");
                System.out.println(String.format("%-40s %-10s %-15s %-15s", "Product", "Stock", "Price", "Value"));
                System.out.println("------------------------------------------------------------------");
                
                for (Map.Entry<Product, Integer> entry : inventory.getStockLevels().entrySet()) {
                    Product product = entry.getKey();
                    Integer stock = entry.getValue();
                    double value = product.getPrice() * stock;
                    
                    System.out.println(String.format("%-40s %-10d $%-14.2f $%-14.2f", 
                        product.getName(), stock, product.getPrice(), value));
                }
                break;
                
            case 2: // Add Stock
                addStock(inventory);
                break;
                
            case 3: // Remove Stock
                removeStock(inventory);
                break;
                
            case 4: // Set Reorder Thresholds
                setReorderThresholds(inventory);
                break;
                
            case 5: // Check Low Stock Items
                checkLowStock(inventory);
                break;
                
            case 6: // Back to Main Menu
                return;
                
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
    
    // Method to add stock to inventory
    private void addStock(Inventory inventory) {
        System.out.println("\n--- Add Stock ---");
        
        // Display current products
        List<Product> products = new ArrayList<>(inventory.getStockLevels().keySet());
        
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            System.out.println((i + 1) + ". " + product.getName());
        }
        
        System.out.print("\nEnter product number (0 for new product): ");
        int productChoice = Integer.parseInt(scanner.nextLine());
        
        Product selectedProduct;
        
        if (productChoice == 0) {
            // Create new product
            System.out.print("Enter Product ID: ");
            String productId = scanner.nextLine();
            
            System.out.print("Enter Product Name: ");
            String name = scanner.nextLine();
            
            System.out.print("Enter Price: $");
            double price = Double.parseDouble(scanner.nextLine());
            
            System.out.print("Enter Category: ");
            String category = scanner.nextLine();
            
            System.out.print("Enter Supplier ID: ");
            String supplierId = scanner.nextLine();
            
            selectedProduct = new Product(productId, name, price, "", category, supplierId);
            selectedProduct.generateBarcode();
            selectedProduct.generateQRCode();
            
            // Register barcode
            BarcodeScanner.registerBarcode(selectedProduct.getBarcode(), selectedProduct);
        } else if (productChoice > 0 && productChoice <= products.size()) {
            selectedProduct = products.get(productChoice - 1);
        } else {
            System.out.println("Invalid selection. Operation cancelled.");
            return;
        }
        
        System.out.print("Enter quantity to add: ");
        int quantity = Integer.parseInt(scanner.nextLine());
        
        if (quantity <= 0) {
            System.out.println("Quantity must be greater than zero. Operation cancelled.");
            return;
        }
        
        // Add stock
        inventory.addStock(selectedProduct, quantity);
        System.out.println("Stock added successfully.");
    }
    
    // Method to remove stock from inventory
    private void removeStock(Inventory inventory) {
        System.out.println("\n--- Remove Stock ---");
        
        // Display current products
        List<Product> products = new ArrayList<>(inventory.getStockLevels().keySet());
        
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int stock = inventory.getStockCount(product);
            System.out.println((i + 1) + ". " + product.getName() + " (Current stock: " + stock + ")");
        }
        
        System.out.print("\nEnter product number: ");
        int productChoice = Integer.parseInt(scanner.nextLine());
        
        if (productChoice < 1 || productChoice > products.size()) {
            System.out.println("Invalid selection. Operation cancelled.");
            return;
        }
        
        Product selectedProduct = products.get(productChoice - 1);
        int currentStock = inventory.getStockCount(selectedProduct);
        
        System.out.print("Enter quantity to remove (max " + currentStock + "): ");
        int quantity = Integer.parseInt(scanner.nextLine());
        
        if (quantity <= 0) {
            System.out.println("Quantity must be greater than zero. Operation cancelled.");
            return;
        }
        
        if (quantity > currentStock) {
            System.out.println("Cannot remove more than current stock. Operation cancelled.");
            return;
        }
        
        try {
            // Remove stock
            inventory.removeStock(selectedProduct, quantity);
            System.out.println("Stock removed successfully.");
        } catch (InventoryException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    // Method to set reorder thresholds
    private void setReorderThresholds(Inventory inventory) {
        System.out.println("\n--- Set Reorder Thresholds ---");
        
        // Display current products
        List<Product> products = new ArrayList<>(inventory.getStockLevels().keySet());
        
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int stock = inventory.getStockCount(product);
            int threshold = inventory.getReorderThresholds().getOrDefault(product, 0);
            System.out.println((i + 1) + ". " + product.getName() + 
                             " (Current stock: " + stock + 
                             ", Reorder threshold: " + threshold + ")");
        }
        
        System.out.print("\nEnter product number: ");
        int productChoice = Integer.parseInt(scanner.nextLine());
        
        if (productChoice < 1 || productChoice > products.size()) {
            System.out.println("Invalid selection. Operation cancelled.");
            return;
        }
        
        Product selectedProduct = products.get(productChoice - 1);
        
        System.out.print("Enter new reorder threshold: ");
        int threshold = Integer.parseInt(scanner.nextLine());
        
        if (threshold < 0) {
            System.out.println("Threshold cannot be negative. Operation cancelled.");
            return;
        }
        
        // Set threshold
        inventory.setReorderThreshold(selectedProduct, threshold);
        System.out.println("Reorder threshold set successfully.");
    }
    
    // Method to check low stock items
    private void checkLowStock(Inventory inventory) {
        System.out.println("\n--- Low Stock Items ---");
        
        List<Product> lowStockProducts = inventory.checkLowStock();
        
        if (lowStockProducts.isEmpty()) {
            System.out.println("No products are below their reorder threshold.");
        } else {
            System.out.println("The following products are below their reorder threshold:");
            System.out.println(String.format("%-40s %-10s %-15s", "Product", "Stock", "Threshold"));
            System.out.println("----------------------------------------------------------");
            
            for (Product product : lowStockProducts) {
                int stock = inventory.getStockCount(product);
                int threshold = inventory.getReorderThresholds().getOrDefault(product, 0);
                
                System.out.println(String.format("%-40s %-10d %-15d", 
                    product.getName(), stock, threshold));
            }
            
            // Send notification for low stock items
            notificationService.sendLowStockAlert(lowStockProducts, inventory.getStockLevels());
        }
    }
    
    // Method to receive shipments
    private void receiveShipments(WarehouseManager warehouseManager) {
        System.out.println("\n--- Receive Shipments ---");
        
        // Find orders with SHIPPED status
        List<Order> shippedOrders = new ArrayList<>();
        
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.SHIPPED) {
                shippedOrders.add(order);
            }
        }
        
        if (shippedOrders.isEmpty()) {
            System.out.println("No shipments pending for receipt.");
            return;
        }
        
        // Display orders
        System.out.println("Pending shipments:");
        for (int i = 0; i < shippedOrders.size(); i++) {
            Order order = shippedOrders.get(i);
            System.out.println((i + 1) + ". Order ID: " + order.getOrderId() + 
                             " - Supplier: " + order.getSupplierUserId() + 
                             " - Items: " + order.getOrderItems().size());
        }
        
        System.out.print("\nEnter shipment number to receive (0 to cancel): ");
        int orderIndex = Integer.parseInt(scanner.nextLine());
        
        if (orderIndex < 1 || orderIndex > shippedOrders.size()) {
            System.out.println("Operation cancelled or invalid selection.");
            return;
        }
        
        Order selectedOrder = shippedOrders.get(orderIndex - 1);
        
        // Update order status
        selectedOrder.updateStatus(OrderStatus.DELIVERED);
        
        // Add items to inventory
        String warehouseId = warehouseManager.getWarehouseId();
        Inventory inventory = inventories.get(warehouseId);
        
        if (inventory != null) {
            for (Map.Entry<Product, Integer> entry : selectedOrder.getOrderItems().entrySet()) {
                Product product = entry.getKey();
                Integer quantity = entry.getValue();
                
                inventory.addStock(product, quantity);
            }
        }
        
        // Log receipt of shipment
        boolean result = warehouseManager.receiveShipment(selectedOrder);
        
        if (result) {
            System.out.println("Shipment received successfully. Order status updated to DELIVERED.");
            
            // Send notification
            notificationService.sendOrderUpdate(selectedOrder, 
                "Order has been received at warehouse " + warehouseId);
        } else {
            System.out.println("Error processing shipment receipt.");
        }
    }
    
    // Method to process outgoing orders
    private void processOutgoingOrders(WarehouseManager warehouseManager) {
        System.out.println("\n--- Process Outgoing Orders ---");
        
        // Find orders with PROCESSING status that were placed by retailers
        List<Order> processingOrders = new ArrayList<>();
        
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.PROCESSING) {
                User placedBy = authService.getUser(order.getPlacedByUserId());
                if (placedBy != null && placedBy.getRole() == Role.RETAILER) {
                    processingOrders.add(order);
                }
            }
        }
        
        if (processingOrders.isEmpty()) {
            System.out.println("No outgoing orders to process.");
            return;
        }
        
        // Display orders
        System.out.println("Orders to process:");
        for (int i = 0; i < processingOrders.size(); i++) {
            Order order = processingOrders.get(i);
            System.out.println((i + 1) + ". Order ID: " + order.getOrderId() + 
                             " - Retailer: " + order.getPlacedByUserId() + 
                             " - Items: " + order.getOrderItems().size());
        }
        
        System.out.print("\nEnter order number to process (0 to cancel): ");
        int orderIndex = Integer.parseInt(scanner.nextLine());
        
        if (orderIndex < 1 || orderIndex > processingOrders.size()) {
            System.out.println("Operation cancelled or invalid selection.");
            return;
        }
        
        Order selectedOrder = processingOrders.get(orderIndex - 1);
        
        // Check if items are available in warehouse inventory
        String warehouseId = warehouseManager.getWarehouseId();
        Inventory inventory = inventories.get(warehouseId);
        
        if (inventory == null) {
            System.out.println("No inventory found for warehouse ID: " + warehouseId);
            return;
        }
        
        // Check inventory levels
        boolean sufficientStock = true;
        StringBuilder insufficientItems = new StringBuilder();
        
        for (Map.Entry<Product, Integer> entry : selectedOrder.getOrderItems().entrySet()) {
            Product product = entry.getKey();
            Integer quantityNeeded = entry.getValue();
            
            if (!inventory.isInStock(product, quantityNeeded)) {
                sufficientStock = false;
                insufficientItems.append("- ").append(product.getName())
                                .append(" (Required: ").append(quantityNeeded)
                                .append(", In stock: ").append(inventory.getStockCount(product))
                                .append(")\n");
            }
        }
        
        if (!sufficientStock) {
            System.out.println("Insufficient stock for the following items:");
            System.out.println(insufficientItems.toString());
            System.out.println("Cannot process order at this time.");
            return;
        }
        
        // Update order status
        selectedOrder.updateStatus(OrderStatus.SHIPPED);
        
        // Remove items from inventory
        try {
            for (Map.Entry<Product, Integer> entry : selectedOrder.getOrderItems().entrySet()) {
                Product product = entry.getKey();
                Integer quantity = entry.getValue();
                
                inventory.removeStock(product, quantity);
            }
        } catch (InventoryException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }
        
        // Process the outgoing order
        boolean result = warehouseManager.processOutgoingOrder(selectedOrder);
        
        if (result) {
            System.out.println("Order processed successfully. Order status updated to SHIPPED.");
            
            // Set expected delivery date
            Date deliveryDate = new Date(System.currentTimeMillis() + (2 * 24 * 60 * 60 * 1000L));
            selectedOrder.setDeliveryDate(deliveryDate);
            
            // Send notification
            notificationService.sendOrderUpdate(selectedOrder, 
                "Order has been shipped from warehouse " + warehouseId + 
                ". Expected delivery: " + new SimpleDateFormat("yyyy-MM-dd").format(deliveryDate));
        } else {
            System.out.println("Error processing order.");
        }
    }
    
    // Method to generate inventory reports
    private void generateInventoryReports(WarehouseManager warehouseManager) {
        System.out.println("\n--- Generate Inventory Reports ---");
        
        String warehouseId = warehouseManager.getWarehouseId();
        Inventory inventory = inventories.get(warehouseId);
        
        if (inventory == null) {
            System.out.println("No inventory found for warehouse ID: " + warehouseId);
            return;
        }
        
        System.out.println("\n1. General Inventory Report");
        System.out.println("2. Low Stock Report");
        System.out.println("3. Back to Main Menu");
        
        System.out.print("\nEnter your choice: ");
        int choice = Integer.parseInt(scanner.nextLine());
        
        String report = null;
        String filename = null;
        
        switch (choice) {
            case 1: // General Inventory Report
                report = ReportGenerator.generateInventoryReport(inventory);
                filename = "inventory_report_" + warehouseId + "_" + 
                         new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt";
                break;
                
            case 2: // Low Stock Report
                report = ReportGenerator.generateLowStockReport(inventory);
                filename = "low_stock_report_" + warehouseId + "_" + 
                         new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt";
                break;
                
            case 3: // Back to Main Menu
                return;
                
            default:
                System.out.println("Invalid choice. Please try again.");
                return;
        }
        
        if (report != null) {
            System.out.println(report);
            
            // Save the report to file
            if (ReportGenerator.saveReportToFile(report, DATA_DIR + File.separator + filename)) {
                System.out.println("Report saved to: " + DATA_DIR + File.separator + filename);
            } else {
                System.out.println("Failed to save report to file.");
            }
        }
    }
    
    // Method to view store inventory (Retailer)
    private void viewStoreInventory(Retailer retailer) {
        System.out.println("\n--- Store Inventory ---");
        
        String storeId = retailer.getStoreId();
        Inventory inventory = inventories.get(storeId);
        
        if (inventory == null) {
            System.out.println("No inventory found for store ID: " + storeId);
            return;
        }
        
        System.out.println("Store: " + storeId + " (" + retailer.getLocation() + ")");
        System.out.println("Total Items: " + inventory.getStockLevels().size());
        System.out.println("Total Value: $" + String.format("%.2f", inventory.getTotalInventoryValue()));
        
        System.out.println("\n1. View All Products");
        System.out.println("2. View by Category");
        System.out.println("3. Check Low Stock");
        System.out.println("4. Back to Main Menu");
        
        System.out.print("\nEnter your choice: ");
        int choice = Integer.parseInt(scanner.nextLine());
        
        switch (choice) {
            case 1: // View All Products
                System.out.println("\nAll Products:");
                System.out.println(String.format("%-40s %-10s %-15s %-15s", "Product", "Stock", "Price", "Value"));
                System.out.println("------------------------------------------------------------------");
                
                for (Map.Entry<Product, Integer> entry : inventory.getStockLevels().entrySet()) {
                    Product product = entry.getKey();
                    Integer stock = entry.getValue();
                    double value = product.getPrice() * stock;
                    
                    System.out.println(String.format("%-40s %-10d $%-14.2f $%-14.2f", 
                        product.getName(), stock, product.getPrice(), value));
                }
                break;
                
            case 2: // View by Category
                System.out.print("\nEnter category name: ");
                String category = scanner.nextLine();
                
                Map<Product, Integer> categoryProducts = inventory.getProductsByCategory(category);
                
                if (categoryProducts.isEmpty()) {
                    System.out.println("No products found in category: " + category);
                } else {
                    System.out.println("\nProducts in category " + category + ":");
                    System.out.println(String.format("%-40s %-10s %-15s %-15s", "Product", "Stock", "Price", "Value"));
                    System.out.println("------------------------------------------------------------------");
                    
                    for (Map.Entry<Product, Integer> entry : categoryProducts.entrySet()) {
                        Product product = entry.getKey();
                        Integer stock = entry.getValue();
                        double value = product.getPrice() * stock;
                        
                        System.out.println(String.format("%-40s %-10d $%-14.2f $%-14.2f", 
                            product.getName(), stock, product.getPrice(), value));
                    }
                }
                break;
                
            case 3: // Check Low Stock
                checkLowStock(inventory);
                break;
                
            case 4: // Back to Main Menu
                return;
                
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
    
    // Method to place an order (Retailer)
    private void placeOrder(Retailer retailer) {
        System.out.println("\n--- Place New Order ---");
        
        // Get available suppliers
        Map<String, User> users = authService.getUsers();
        List<Supplier> suppliers = new ArrayList<>();
        
        for (User user : users.values()) {
            if (user instanceof Supplier && user.isActive()) {
                suppliers.add((Supplier) user);
            }
        }
        
        if (suppliers.isEmpty()) {
            System.out.println("No active suppliers found in the system.");
            return;
        }
        
        // Display suppliers
        System.out.println("Available suppliers:");
        for (int i = 0; i < suppliers.size(); i++) {
            Supplier supplier = suppliers.get(i);
            System.out.println((i + 1) + ". " + supplier.getName() + " (" + supplier.getCompanyName() + ")");
        }
        
        System.out.print("\nSelect supplier (0 to cancel): ");
        int supplierIndex = Integer.parseInt(scanner.nextLine());
        
        if (supplierIndex < 1 || supplierIndex > suppliers.size()) {
            System.out.println("Operation cancelled or invalid selection.");
            return;
        }
        
        Supplier selectedSupplier = suppliers.get(supplierIndex - 1);
        
        // Get all products from this supplier
        List<Product> allProducts = new ArrayList<>();
        
        for (Inventory inventory : inventories.values()) {
            for (Product product : inventory.getStockLevels().keySet()) {
                if (selectedSupplier.getUserId().equals(product.getSupplierUserId()) && 
                    !allProducts.contains(product)) {
                    allProducts.add(product);
                }
            }
        }
        
        if (allProducts.isEmpty()) {
            System.out.println("No products available from this supplier.");
            return;
        }
        
        // Create order
        Map<Product, Integer> orderItems = new HashMap<>();
        boolean addingItems = true;
        
        while (addingItems) {
            // Display products
            System.out.println("\nAvailable products from " + selectedSupplier.getName() + ":");
            for (int i = 0; i < allProducts.size(); i++) {
                Product product = allProducts.get(i);
                System.out.println((i + 1) + ". " + product.getName() + 
                                 " - $" + String.format("%.2f", product.getPrice()) + 
                                 (orderItems.containsKey(product) ? 
                                    " (Selected: " + orderItems.get(product) + ")" : ""));
            }
            
            System.out.print("\nSelect product to add (0 to finish): ");
            int productIndex = Integer.parseInt(scanner.nextLine());
            
            if (productIndex == 0) {
                if (orderItems.isEmpty()) {
                    System.out.println("Order is empty. Operation cancelled.");
                    return;
                }
                addingItems = false;
            } else if (productIndex >= 1 && productIndex <= allProducts.size()) {
                Product selectedProduct = allProducts.get(productIndex - 1);
                
                System.out.print("Enter quantity: ");
                int quantity = Integer.parseInt(scanner.nextLine());
                
                if (quantity > 0) {
                    orderItems.put(selectedProduct, quantity);
                    System.out.println("Added to order: " + selectedProduct.getName() + " x " + quantity);
                } else {
                    System.out.println("Invalid quantity. Product not added.");
                }
            } else {
                System.out.println("Invalid selection.");
            }
        }
        
        // Shipping address
        System.out.print("\nEnter shipping address (press Enter for store address): ");
        String shippingAddress = scanner.nextLine();
        if (shippingAddress.isEmpty()) {
            shippingAddress = retailer.getLocation();
        }
        
        // Urgent order?
        System.out.print("Is this an urgent order? (true/false): ");
        boolean urgent = Boolean.parseBoolean(scanner.nextLine());
        
        // Generate order ID
        String orderId = "ORD-" + retailer.getStoreId() + "-" + System.currentTimeMillis();
        
        // Create and place order
        Order order;
        if (urgent) {
            order = retailer.placeOrder(orderItems, selectedSupplier, true);
        } else {
            order = retailer.placeOrder(orderItems, selectedSupplier);
        }
        
        if (order != null) {
            order.setShippingAddress(shippingAddress);
            orders.add(order);
            
            // Calculate total
            double total = order.calculateTotal();
            
            // Display order confirmation
            System.out.println("\nOrder placed successfully:");
            System.out.println("Order ID: " + order.getOrderId());
            System.out.println("Supplier: " + selectedSupplier.getName());
            System.out.println("Items: " + orderItems.size());
            System.out.println("Total Amount: $" + String.format("%.2f", total));
            System.out.println("Status: " + order.getStatus());
            
            // Send notification
            notificationService.sendOrderUpdate(order, 
                "New order placed by " + retailer.getName() + 
                (urgent ? " (URGENT)" : ""));
        } else {
            System.out.println("Error creating order.");
        }
    }
    
    // Method to scan products (Retailer)
    private void scanProducts(Retailer retailer) {
        System.out.println("\n--- Scan Products ---");
        System.out.println("1. Scan Single Product");
        System.out.println("2. Bulk Scan");
        System.out.println("3. Back to Main Menu");
        
        System.out.print("\nEnter your choice: ");
        int choice = Integer.parseInt(scanner.nextLine());
        
        switch (choice) {
            case 1: // Scan Single Product
                System.out.print("\nEnter barcode to scan: ");
                String barcode = scanner.nextLine();
                
                Product product = retailer.scanBarcode(barcode);
                
                if (product != null) {
                    System.out.println("\nProduct found:");
                    System.out.println("ID: " + product.getProductId());
                    System.out.println("Name: " + product.getName());
                    System.out.println("Price: $" + String.format("%.2f", product.getPrice()));
                    System.out.println("Category: " + product.getCategory());
                    
                    // Check inventory
                    String storeId = retailer.getStoreId();
                    Inventory inventory = inventories.get(storeId);
                    
                    if (inventory != null) {
                        int stock = inventory.getStockCount(product);
                        System.out.println("In Stock: " + stock);
                    }
                } else {
                    System.out.println("No product found with barcode: " + barcode);
                }
                break;
                
            case 2: // Bulk Scan
                System.out.println("\nEnter barcodes separated by spaces:");
                String barcodeInput = scanner.nextLine();
                String[] barcodes = barcodeInput.split("\\s+");
                
                Map<Product, Integer> scannedProducts = BarcodeScanner.bulkScan(barcodes);
                
                if (scannedProducts.isEmpty()) {
                    System.out.println("No valid products scanned.");
                } else {
                    System.out.println("\nScanned Products:");
                    System.out.println(String.format("%-40s %-10s %-15s", "Product", "Quantity", "Price"));
                    System.out.println("--------------------------------------------------");
                    
                    double total = 0.0;
                    
                    for (Map.Entry<Product, Integer> entry : scannedProducts.entrySet()) {
                        Product p = entry.getKey();
                        int qty = entry.getValue();
                        double itemTotal = p.getPrice() * qty;
                        total += itemTotal;
                        
                        System.out.println(String.format("%-40s %-10d $%-14.2f", 
                            p.getName(), qty, p.getPrice()));
                    }
                    
                    System.out.println("--------------------------------------------------");
                    System.out.println(String.format("Total: $%.2f", total));
                }
                break;
                
            case 3: // Back to Main Menu
                return;
                
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
    
    // Method to record sales (Retailer)
    private void recordSales(Retailer retailer) {
        System.out.println("\n--- Record Sales ---");
        System.out.println("1. Record Individual Sale");
        System.out.println("2. Record Multiple Sales");
        System.out.println("3. Back to Main Menu");
        
        System.out.print("\nEnter your choice: ");
        int choice = Integer.parseInt(scanner.nextLine());
        
        switch (choice) {
            case 1: // Record Individual Sale
                // Get store inventory
                String storeId = retailer.getStoreId();
                Inventory inventory = inventories.get(storeId);
                
                if (inventory == null) {
                    System.out.println("No inventory found for store ID: " + storeId);
                    return;
                }
                
                // Create a new order for the sale
                Map<Product, Integer> orderItems = new HashMap<>();
                boolean addingItems = true;
                
                while (addingItems) {
                    // Display inventory
                    List<Product> products = new ArrayList<>(inventory.getStockLevels().keySet());
                    
                    System.out.println("\nStore Products:");
                    for (int i = 0; i < products.size(); i++) {
                        Product product = products.get(i);
                        int stock = inventory.getStockCount(product);
                        System.out.println((i + 1) + ". " + product.getName() + 
                                         " - $" + String.format("%.2f", product.getPrice()) + 
                                         " (In stock: " + stock + ")" + 
                                         (orderItems.containsKey(product) ? 
                                            " (Selected: " + orderItems.get(product) + ")" : ""));
                    }
                    
                    System.out.print("\nSelect product to add (0 to finish): ");
                    int productIndex = Integer.parseInt(scanner.nextLine());
                    
                    if (productIndex == 0) {
                        if (orderItems.isEmpty()) {
                            System.out.println("Sale is empty. Operation cancelled.");
                            return;
                        }
                        addingItems = false;
                    } else if (productIndex >= 1 && productIndex <= products.size()) {
                        Product selectedProduct = products.get(productIndex - 1);
                        int maxStock = inventory.getStockCount(selectedProduct);
                        
                        System.out.print("Enter quantity (max " + maxStock + "): ");
                        int quantity = Integer.parseInt(scanner.nextLine());
                        
                        if (quantity > 0 && quantity <= maxStock) {
                            orderItems.put(selectedProduct, quantity);
                            System.out.println("Added to sale: " + selectedProduct.getName() + " x " + quantity);
                        } else {
                            System.out.println("Invalid quantity. Product not added.");
                        }
                    } else {
                        System.out.println("Invalid selection.");
                    }
                }
                
                // Create a sale order
                String orderId = "SALE-" + storeId + "-" + System.currentTimeMillis();
                Order saleOrder = new Order(orderId, orderItems, "CUSTOMER", retailer.getUserId());
                
                // Update inventory
                try {
                    for (Map.Entry<Product, Integer> entry : orderItems.entrySet()) {
                        Product product = entry.getKey();
                        int quantity = entry.getValue();
                        
                        inventory.removeStock(product, quantity);
                    }
                    
                    // Record the sale
                    retailer.recordSale(saleOrder);
                    
                    // Calculate total
                    double total = saleOrder.calculateTotal();
                    
                    // Display sale confirmation
                    System.out.println("\nSale recorded successfully:");
                    System.out.println("Sale ID: " + saleOrder.getOrderId());
                    System.out.println("Items: " + orderItems.size());
                    System.out.println("Total Amount: $" + String.format("%.2f", total));
                    
                    // Check if any products need reordering
                    checkLowStock(inventory);
                } catch (InventoryException e) {
                    System.out.println("Error: " + e.getMessage());
                }
                break;
                
            case 2: // Record Multiple Sales
                System.out.println("\nEnter sales amounts separated by spaces:");
                String salesInput = scanner.nextLine();
                String[] salesStrings = salesInput.split("\\s+");
                
                double[] salesAmounts = new double[salesStrings.length];
                for (int i = 0; i < salesStrings.length; i++) {
                    try {
                        salesAmounts[i] = Double.parseDouble(salesStrings[i]);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid amount: " + salesStrings[i]);
                        return;
                    }
                }
                
                // Record the sales
                retailer.recordSales(salesAmounts);
                break;
                
            case 3: // Back to Main Menu
                return;
                
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
    
    // Method to view user profile
    private void viewUserProfile() {
        System.out.println("\n--- User Profile ---");
        System.out.println("User ID: " + currentUser.getUserId());
        System.out.println("Name: " + currentUser.getName());
        System.out.println("Role: " + currentUser.getRole());
        System.out.println("Email: " + (currentUser.getEmail() != null ? currentUser.getEmail() : "Not set"));
        System.out.println("Phone: " + (currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "Not set"));
        System.out.println("Last Login: " + (currentUser.getLastLogin() != null ? 
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentUser.getLastLogin()) : "Never"));
        System.out.println("Active: " + currentUser.isActive());
        
        // Role-specific details
        if (currentUser instanceof Admin) {
            Admin admin = (Admin) currentUser;
            System.out.println("Admin Level: " + admin.getAdminLevel());
            System.out.println("Managed Modules: " + admin.getManagedModules());
        } else if (currentUser instanceof Supplier) {
            Supplier supplier = (Supplier) currentUser;
            System.out.println("Company Name: " + supplier.getCompanyName());
            System.out.println("Address: " + supplier.getAddress());
            System.out.println("Product Categories: " + supplier.getProductCategories());
            System.out.println("Reliability Score: " + supplier.getReliabilityScore());
        } else if (currentUser instanceof WarehouseManager) {
            WarehouseManager warehouseManager = (WarehouseManager) currentUser;
            System.out.println("Warehouse ID: " + warehouseManager.getWarehouseId());
            System.out.println("Location: " + warehouseManager.getLocation());
            System.out.println("Warehouse Capacity: " + warehouseManager.getWarehouseCapacity() + " cubic meters");
            System.out.println("Current Utilization: " + warehouseManager.getCurrentUtilization() + "%");
        } else if (currentUser instanceof Retailer) {
            Retailer retailer = (Retailer) currentUser;
            System.out.println("Store ID: " + retailer.getStoreId());
            System.out.println("Location: " + retailer.getLocation());
            System.out.println("Sales Target: $" + String.format("%.2f", retailer.getSalesTarget()));
            System.out.println("Current Sales: $" + String.format("%.2f", retailer.getCurrentSales()));
            System.out.println("Specializations: " + retailer.getSpecializations());
        }
    }
    
    // Method to change password
    private void changePassword() {
        System.out.println("\n--- Change Password ---");
        
        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine();
        
        try {
            // Verify current password
            if (currentUser.login(currentPassword)) {
                System.out.print("Enter new password: ");
                String newPassword = scanner.nextLine();
                
                System.out.print("Confirm new password: ");
                String confirmPassword = scanner.nextLine();
                
                if (newPassword.equals(confirmPassword)) {
                    currentUser.setPassword(newPassword);
                    authService.updateUser(currentUser);
                    System.out.println("Password changed successfully.");
                } else {
                    System.out.println("Passwords do not match. Password not changed.");
                }
            }
        } catch (AuthenticationException e) {
            System.out.println("Current password is incorrect. Password not changed.");
        }
    }
    
    // Method to logout
    private void logout() {
        if (currentUser != null) {
            currentUser.logout();
            System.out.println(currentUser.getName() + " has been logged out.");
            currentUser = null;
        }
    }
    
    // Main method
    public static void main(String[] args) {
        try {
            // Create and run the system
            SupplyChainManagementSystem system = new SupplyChainManagementSystem();
            system.run();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred:");
            e.printStackTrace();
            System.err.println("Please restart the application.");
        } finally {
            // Ensure scanner is closed properly
            if (scanner != null) {
                scanner.close();
            }
            System.out.println("Application has terminated.");
        }
    }
}