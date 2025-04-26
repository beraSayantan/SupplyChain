package com.smartsupply.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import com.smartsupply.model.Product;

/**
 * Utility class for barcode/QR code scanning operations
 */
public class BarcodeScanner {
    private static Map<String, Product> barcodeRegistry = new HashMap<>();
    private static Map<String, Product> qrCodeRegistry = new HashMap<>();
    private static Random random = new Random();
    
    // Method to register a product barcode
    public static void registerBarcode(String barcode, Product product) {
        barcodeRegistry.put(barcode, product);
    }
    
    // Method to register a product QR code
    public static void registerQRCode(String qrCode, Product product) {
        qrCodeRegistry.put(qrCode, product);
    }
    
    // Method to scan a barcode
    public static Product scanBarcode(String barcode) {
        // In a real implementation, this would interface with hardware
        // For simulation purposes, we just look up the product
        return barcodeRegistry.get(barcode);
    }
    
    // Method to scan a QR code
    public static Product scanQRCode(String qrCode) {
        // In a real implementation, this would interface with hardware
        // For simulation purposes, we just look up the product
        return qrCodeRegistry.get(qrCode);
    }
    
    // Method to generate a random barcode
    public static String generateRandomBarcode() {
        StringBuilder barcode = new StringBuilder("BAR");
        for (int i = 0; i < 10; i++) {
            barcode.append(random.nextInt(10));
        }
        return barcode.toString();
    }
    
    // Method to generate a random QR code
    public static String generateRandomQRCode() {
        StringBuilder qrCode = new StringBuilder("QR");
        for (int i = 0; i < 12; i++) {
            qrCode.append(random.nextInt(10));
        }
        return qrCode.toString();
    }
    
    // Method to simulate scanning multiple products
    public static Map<Product, Integer> bulkScan(String... barcodes) {
        Map<Product, Integer> scannedProducts = new HashMap<>();
        
        for (String barcode : barcodes) {
            Product product = scanBarcode(barcode);
            if (product != null) {
                int count = scannedProducts.getOrDefault(product, 0);
                scannedProducts.put(product, count + 1);
            }
        }
        
        return scannedProducts;
    }
    
    // Method to clear registries (for testing purposes)
    public static void clearRegistries() {
        barcodeRegistry.clear();
        qrCodeRegistry.clear();
    }
}
