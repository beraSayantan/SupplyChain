package com.smartsupply.model;

import java.io.Serializable;
import java.util.Date;
import com.smartsupply.exception.AuthenticationException;

/**
 * Abstract class representing a user in the system
 * Serves as the base class for all user types
 */
public abstract class User implements Serializable {
    // Instance variables
    private String userId;
    private String name;
    private String password;
    private Role role;
    private String email;
    private String phoneNumber;
    private Date lastLogin;
    private boolean isActive;
    
    // Static inner class for user session management
    public static class UserSession {
        private static User currentUser;
        private static Date loginTime;
        
        // Method to start a user session
        public static void startSession(User user) {
            currentUser = user;
            loginTime = new Date();
        }
        
        // Method to end a user session
        public static void endSession() {
            currentUser = null;
            loginTime = null;
        }
        
        // Method to check if a session is active
        public static boolean isSessionActive() {
            return currentUser != null;
        }
        
        // Method to get the current user
        public static User getCurrentUser() {
            return currentUser;
        }
        
        // Method to get session duration in minutes
        public static long getSessionDurationMinutes() {
            if (!isSessionActive()) {
                return 0;
            }
            return (new Date().getTime() - loginTime.getTime()) / (60 * 1000);
        }
    }
    
    // Default constructor
    public User() {
        this.isActive = true;
    }
    
    // Parameterized constructor
    public User(String userId, String name, String password, Role role) {
        this();
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.role = role;
    }
    
    // Constructor with email and phone
    public User(String userId, String name, String password, Role role, 
                String email, String phoneNumber) {
        this(userId, name, password, role);
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
    
    // Varargs constructor to handle multiple contact methods
    public User(String userId, String name, String password, Role role, String... contactInfo) {
        this(userId, name, password, role);
        if (contactInfo.length > 0) {
            this.email = contactInfo[0];
        }
        if (contactInfo.length > 1) {
            this.phoneNumber = contactInfo[1];
        }
    }
    
    // Abstract method to be implemented by subclasses
    public abstract boolean hasPermission(String operation);
    
    // Method to authenticate a user
    public boolean login(String password) throws AuthenticationException {
        if (!this.isActive) {
            throw new AuthenticationException("User account is not active");
        }
        
        if (this.password.equals(password)) {
            this.lastLogin = new Date();
            UserSession.startSession(this);
            return true;
        }
        
        throw new AuthenticationException("Invalid password");
    }
    
    // Method to log out a user
    public void logout() {
        UserSession.endSession();
    }
    
    // Overloaded login method with additional security check
    public boolean login(String password, String securityToken) throws AuthenticationException {
        // Placeholder for additional security validation
        boolean tokenValid = securityToken != null && !securityToken.isEmpty();
        
        if (!tokenValid) {
            throw new AuthenticationException("Invalid security token");
        }
        
        return login(password);
    }
    
    // Getters and setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    protected String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public Date getLastLogin() {
        return lastLogin;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    @Override
    public String toString() {
        return String.format("User[id=%s, name=%s, role=%s]", userId, name, role);
    }
}