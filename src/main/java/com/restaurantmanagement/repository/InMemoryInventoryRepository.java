package com.restaurantmanagement.repository;

import com.restaurantmanagement.model.InventoryItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory repository implementation for InventoryItem entities.
 */
public class InMemoryInventoryRepository {
    private final List<InventoryItem> items;
    private int nextId = 1;

    public InMemoryInventoryRepository() {
        this.items = new ArrayList<>();
    }

    /**
     * Save a new inventory item or update an existing one.
     */
    public InventoryItem save(InventoryItem item) {
        if (item.getId() == null || item.getId().isEmpty()) {
            // New item - assign ID
            item.setId("INV" + String.format("%04d", nextId++));
            items.add(item);
            return item;
        } else {
            // Update existing item
            Optional<InventoryItem> existing = findById(item.getId());
            if (existing.isPresent()) {
                int index = items.indexOf(existing.get());
                items.set(index, item);
                return item;
            } else {
                items.add(item);
                return item;
            }
        }
    }

    /**
     * Find inventory item by ID.
     */
    public Optional<InventoryItem> findById(String id) {
        return items.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst();
    }

    /**
     * Get all inventory items.
     */
    public List<InventoryItem> findAll() {
        return new ArrayList<>(items);
    }

    /**
     * Delete inventory item by ID.
     */
    public boolean deleteById(String id) {
        return items.removeIf(item -> item.getId().equals(id));
    }

    /**
     * Check if inventory item exists by ID.
     */
    public boolean existsById(String id) {
        return items.stream().anyMatch(item -> item.getId().equals(id));
    }

    /**
     * Find items by name (case-insensitive partial match).
     */
    public List<InventoryItem> findByName(String name) {
        String searchName = name.toLowerCase();
        return items.stream()
                .filter(item -> item.getName().toLowerCase().contains(searchName))
                .collect(Collectors.toList());
    }

    /**
     * Find items by category.
     */
    public List<InventoryItem> findByCategory(String category) {
        return items.stream()
                .filter(item -> item.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    /**
     * Find items that are low in stock.
     */
    public List<InventoryItem> findLowStockItems() {
        return items.stream()
                .filter(InventoryItem::isLowStock)
                .collect(Collectors.toList());
    }
}



















