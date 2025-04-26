package com.smartsupply.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import com.smartsupply.model.Product;

/**
 * Interface for analytics functionality
 * Demonstrates the use of interface requirement
 */
public interface Analytics {
    
    /**
     * Analyzes sales data for the given time period
     * @param startDate Starting date for analysis
     * @param endDate Ending date for analysis
     * @return Map containing sales analysis results
     */
    Map<String, Object> analyzeSales(Date startDate, Date endDate);
    
    /**
     * Forecasts demand for products based on historical data
     * @param productCategories Categories to include in forecast
     * @param forecastPeriodDays Number of days to forecast
     * @return List of products with forecasted demand quantities
     */
    List<Map<String, Object>> forecastDemand(List<String> productCategories, int forecastPeriodDays);
    
    /**
     * Identifies trends in sales data
     * @param productCategories Categories to analyze
     * @param periodDays Number of past days to analyze
     * @return Map containing trend analysis data
     */
    Map<String, Object> identifyTrends(List<String> productCategories, int periodDays);
    
    /**
     * Generates key performance metrics for the supply chain
     * @return Map containing various KPI values
     */
    Map<String, Double> generateKPIs();
    
    /**
     * Detects anomalies in inventory or sales data
     * @return List of detected anomalies
     */
    List<String> detectAnomalies();
    
    /**
     * Nested interface for advanced analytics
     * Demonstrates nested interface requirement
     */
    interface AdvancedAnalytics {
        /**
         * Performs predictive analytics for business planning
         * @param factors Factors to consider in prediction
         * @param horizon Time horizon for prediction in days
         * @return Prediction model results
         */
        Map<String, Object> predictiveAnalytics(List<String> factors, int horizon);
        
        /**
         * Optimizes inventory levels based on various factors
         * @param locationIds Locations to optimize
         * @return Optimization recommendations
         */
        Map<String, Object> optimizeInventory(List<String> locationIds);
    }
}