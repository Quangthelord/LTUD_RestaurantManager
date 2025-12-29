package com.restaurantmanagement.service;

import com.restaurantmanagement.model.InventoryItem;
import com.restaurantmanagement.model.InventoryTransaction;
import com.restaurantmanagement.repository.InMemoryInventoryRepository;
import com.restaurantmanagement.repository.InMemoryInventoryTransactionRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Inventory business logic.
 */
public class InventoryService {
    private final InMemoryInventoryRepository itemRepository;
    private final InMemoryInventoryTransactionRepository transactionRepository;

    public InventoryService() {
        this.itemRepository = new InMemoryInventoryRepository();
        this.transactionRepository = new InMemoryInventoryTransactionRepository();
    }

    /**
     * Add a new inventory item.
     */
    public InventoryItem addItem(InventoryItem item) {
        validateItem(item);
        item.setLastUpdated(LocalDate.now());
        return itemRepository.save(item);
    }

    /**
     * Update an existing inventory item.
     */
    public InventoryItem updateItem(InventoryItem item) {
        if (item.getId() == null || item.getId().isEmpty()) {
            throw new IllegalArgumentException("Item ID is required for update");
        }
        if (!itemRepository.existsById(item.getId())) {
            throw new IllegalArgumentException("Item with ID " + item.getId() + " not found");
        }
        validateItem(item);
        item.setLastUpdated(LocalDate.now());
        return itemRepository.save(item);
    }

    /**
     * Delete an inventory item by ID.
     */
    public boolean deleteItem(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Item ID cannot be empty");
        }
        return itemRepository.deleteById(id);
    }

    /**
     * Get inventory item by ID.
     */
    public Optional<InventoryItem> getItemById(String id) {
        return itemRepository.findById(id);
    }

    /**
     * Get all inventory items.
     */
    public List<InventoryItem> getAllItems() {
        return itemRepository.findAll();
    }

    /**
     * Search items by name.
     */
    public List<InventoryItem> searchItemsByName(String name) {
        return itemRepository.findByName(name);
    }

    /**
     * Get items by category.
     */
    public List<InventoryItem> getItemsByCategory(String category) {
        return itemRepository.findByCategory(category);
    }

    /**
     * Get items that are low in stock.
     */
    public List<InventoryItem> getLowStockItems() {
        return itemRepository.findLowStockItems();
    }

    /**
     * Stock In: Add quantity to an item.
     */
    public InventoryTransaction stockIn(String itemId, double quantity, String reason, 
                                        String staffId, String staffName) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Optional<InventoryItem> itemOpt = itemRepository.findById(itemId);
        if (!itemOpt.isPresent()) {
            throw new IllegalArgumentException("Item with ID " + itemId + " not found");
        }

        InventoryItem item = itemOpt.get();
        item.setQuantity(item.getQuantity() + quantity);
        item.setLastUpdated(LocalDate.now());
        itemRepository.save(item);

        // Create transaction record
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setItemId(itemId);
        transaction.setItemName(item.getName());
        transaction.setQuantity(quantity);
        transaction.setType("IN");
        transaction.setReason(reason);
        transaction.setStaffId(staffId);
        transaction.setStaffName(staffName);

        return transactionRepository.save(transaction);
    }

    /**
     * Stock Out: Deduct quantity from an item.
     */
    public InventoryTransaction stockOut(String itemId, double quantity, String reason,
                                        String staffId, String staffName) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Optional<InventoryItem> itemOpt = itemRepository.findById(itemId);
        if (!itemOpt.isPresent()) {
            throw new IllegalArgumentException("Item with ID " + itemId + " not found");
        }

        InventoryItem item = itemOpt.get();
        if (item.getQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + 
                item.getQuantity() + " " + item.getUnit());
        }

        item.setQuantity(item.getQuantity() - quantity);
        item.setLastUpdated(LocalDate.now());
        itemRepository.save(item);

        // Create transaction record
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setItemId(itemId);
        transaction.setItemName(item.getName());
        transaction.setQuantity(quantity);
        transaction.setType("OUT");
        transaction.setReason(reason);
        transaction.setStaffId(staffId);
        transaction.setStaffName(staffName);

        return transactionRepository.save(transaction);
    }

    /**
     * Get all transactions.
     */
    public List<InventoryTransaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /**
     * Get transactions for a specific item.
     */
    public List<InventoryTransaction> getTransactionsByItemId(String itemId) {
        return transactionRepository.findByItemId(itemId);
    }

    /**
     * Validate inventory item data.
     */
    private void validateItem(InventoryItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Item name is required");
        }
        if (item.getCategory() == null || item.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Category is required");
        }
        if (item.getUnit() == null || item.getUnit().trim().isEmpty()) {
            throw new IllegalArgumentException("Unit is required");
        }
        if (item.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (item.getMinimumThreshold() < 0) {
            throw new IllegalArgumentException("Minimum threshold cannot be negative");
        }
    }
}



















