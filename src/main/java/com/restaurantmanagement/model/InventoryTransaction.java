package com.restaurantmanagement.model;

import java.time.LocalDateTime;

/**
 * Inventory Transaction entity representing stock in/out operations.
 */
public class InventoryTransaction {
    private String id;
    private String itemId;
    private String itemName;
    private double quantity;
    private String type; // IN or OUT
    private String reason; // Purchase, Sale, Waste, Adjustment
    private LocalDateTime timestamp;
    private String staffId;
    private String staffName;

    public InventoryTransaction() {
        this.timestamp = LocalDateTime.now();
    }

    public InventoryTransaction(String id, String itemId, String itemName, double quantity,
                               String type, String reason, String staffId, String staffName) {
        this.id = id;
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.type = type;
        this.reason = reason;
        this.staffId = staffId;
        this.staffName = staffName;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    @Override
    public String toString() {
        return "InventoryTransaction{" +
                "id='" + id + '\'' +
                ", itemName='" + itemName + '\'' +
                ", quantity=" + quantity +
                ", type='" + type + '\'' +
                ", reason='" + reason + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}



















