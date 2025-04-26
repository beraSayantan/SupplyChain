package com.smartsupply.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Admin class representing system administrators with highest privileges
 */
public class Admin extends User {
    private int adminLevel; // Determines the level of administrative access
    private List<String> managedModules; // List of system modules this admin manages
    
    // Default constructor
    public Admin() {
        super();
        this.setRole(Role.ADMIN);
        this.adminLevel = 1;
        this.managedModules = new ArrayList<>();
    }
    
    // Parameterized constructor
    public Admin(String userId, String name, String password) {
        super(userId, name, password, Role.ADMIN);
        this.adminLevel = 1;
        this.managedModules = new ArrayList<>();
    }
    
    // Constructor with admin level
    public Admin(String userId, String name, String password, int adminLevel) {
        super(userId, name, password, Role.ADMIN);
        this.adminLevel = adminLevel;
        this.managedModules = new ArrayList<>();
    }
    
    // Varargs constructor to specify managed modules
    public Admin(String userId, String name, String password, int adminLevel, String... modules) {
        this(userId, name, password, adminLevel);
        for (String module : modules) {
            this.managedModules.add(module);
        }
    }
    
    // Method to create a new user in the system
    public User createUser(String userId, String name, String password, Role role) {
        // Implementation would create and persist a new user
        switch (role) {
            case ADMIN:
                return new Admin(userId, name, password);
            case SUPPLIER:
                return new Supplier(userId, name, password);
            case WAREHOUSE_MANAGER:
                return new WarehouseManager(userId, name, password);
            case RETAILER:
                return new Retailer(userId, name, password);
            default:
                return null;
        }
    }
    
    // Method to generate system reports
    public Map<String, Object> generateSystemReport(Date startDate, Date endDate) {
        Map<String, Object> report = new HashMap<>();
        // Implementation would generate comprehensive system reports
        report.put("reportType", "System Performance");
        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("generatedBy", this.getName());
        
        // Sample report data
        report.put("totalUsers", 120);
        report.put("activeUsers", 98);
        report.put("totalProducts", 1500);
        report.put("lowStockItems", 42);
        
        return report;
    }
    
    // Overloaded method to generate specific type of report
    public Map<String, Object> generateSystemReport(String reportType, Date startDate, Date endDate) {
        Map<String, Object> report = generateSystemReport(startDate, endDate);
        report.put("reportType", reportType);
        
        // Additional logic based on report type
        
        return report;
    }
    
    // Method to configure system settings
    public boolean configureSystem(Map<String, Object> settings) {
        // Implementation would apply system-wide configuration changes
        System.out.println("Admin " + getName() + " updating system settings: " + settings);
        return true;
    }
    
    // Method to manage user accounts
    public boolean manageUser(String userId, boolean activate) {
        // Implementation would enable/disable user accounts
        System.out.println("Admin " + getName() + (activate ? " activating " : " deactivating ") + 
                           "user: " + userId);
        return true;
    }
    
    @Override
    public boolean hasPermission(String operation) {
        // Admins have permission to perform any operation
        return true;
    }
    
    // Getters and setters
    public int getAdminLevel() {
        return adminLevel;
    }
    
    public void setAdminLevel(int adminLevel) {
        this.adminLevel = adminLevel;
    }
    
    public List<String> getManagedModules() {
        return managedModules;
    }
    
    public void addManagedModule(String module) {
        if (!managedModules.contains(module)) {
            managedModules.add(module);
        }
    }
    
    public void removeManagedModule(String module) {
        managedModules.remove(module);
    }
    
    @Override
    public String toString() {
        return String.format("Admin[id=%s, name=%s, level=%d]", 
                             getUserId(), getName(), adminLevel);
    }
}