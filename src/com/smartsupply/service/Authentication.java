package com.smartsupply.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import com.smartsupply.exception.AuthenticationException;
import com.smartsupply.model.User;

/**
 * Service for user authentication and management
 */
public class Authentication {
    private Map<String, User> users;
    private String userDataFile;
    
    // Default constructor
    public Authentication() {
        this.users = new HashMap<>();
        this.userDataFile = "users.dat";
    }
    
    // Constructor with custom data file
    public Authentication(String userDataFile) {
        this.users = new HashMap<>();
        this.userDataFile = userDataFile;
        loadUsers();
    }
    
    // Method to register a new user
    public boolean registerUser(User user) throws AuthenticationException {
        if (users.containsKey(user.getUserId())) {
            throw new AuthenticationException("User ID already exists: " + user.getUserId());
        }
        
        users.put(user.getUserId(), user);
        saveUsers();
        return true;
    }
    
    // Method to authenticate a user
    public User login(String userId, String password) throws AuthenticationException {
        User user = users.get(userId);
        
        if (user == null) {
            throw new AuthenticationException("User not found: " + userId);
        }
        
        if (user.login(password)) {
            return user;
        }
        
        return null; // Should not reach here as login throws exception on failure
    }
    
    // Method to log out a user
    public void logout(String userId) {
        User user = users.get(userId);
        if (user != null) {
            user.logout();
        }
    }
    
    // Method to get a user by ID
    public User getUser(String userId) {
        return users.get(userId);
    }
    
    // Method to update a user
    public boolean updateUser(User user) {
        if (!users.containsKey(user.getUserId())) {
            return false;
        }
        
        users.put(user.getUserId(), user);
        saveUsers();
        return true;
    }
    
    // Method to remove a user
    public boolean removeUser(String userId) {
        if (!users.containsKey(userId)) {
            return false;
        }
        
        users.remove(userId);
        saveUsers();
        return true;
    }
    
    // Method to load users from file
    @SuppressWarnings("unchecked")
    private void loadUsers() {
        File file = new File(userDataFile);
        if (!file.exists()) {
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                users = (Map<String, User>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            // In production, log the error properly
        }
    }
    
    // Method to save users to file
    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(userDataFile))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
            // In production, log the error properly
        }
    }
    
    // Getter for users map
    public Map<String, User> getUsers() {
        return users;
    }
}