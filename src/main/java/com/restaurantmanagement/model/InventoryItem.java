package com.restaurantmanagement.model;

import java.time.LocalDate;

/**
 * Inventory Item entity representing a stock item in the restaurant.
 */
public class InventoryItem {
    private String id;
    private String name;
    private String category; // Food, Beverage, Ingredient, Cleaning, Others
    private String unit; // kg, liter, piece, box, etc.
    private double quantity;
    private double minimumThreshold;
    private String supplierName;
    private LocalDate lastUpdated;
    private String storageLocation; // Kitchen, Bar, Warehouse, Freezer

    public InventoryItem() {
        this.lastUpdated = LocalDate.now();
    }

    public InventoryItem(String id, String name, String category, String unit, 
                        double quantity, double minimumThreshold, String supplierName,
                        String storageLocation) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.quantity = quantity;
        this.minimumThreshold = minimumThreshold;
        this.supplierName = supplierName;
        this.storageLocation = storageLocation;
        this.lastUpdated = LocalDate.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getMinimumThreshold() {
        return minimumThreshold;
    }

    public void setMinimumThreshold(double minimumThreshold) {
        this.minimumThreshold = minimumThreshold;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDate lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    /**
     * Check if item is low in stock.
     */
    public boolean isLowStock() {
        return quantity <= minimumThreshold;
    }

    @Override
    public String toString() {
        return "InventoryItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", quantity=" + quantity +
                ", unit='" + unit + '\'' +
                '}';
    }
}



















