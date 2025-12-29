package com.restaurantmanagement.repository;

import com.restaurantmanagement.model.InventoryTransaction;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory repository implementation for InventoryTransaction entities.
 */
public class InMemoryInventoryTransactionRepository {
    private final List<InventoryTransaction> transactions;
    private int nextId = 1;

    public InMemoryInventoryTransactionRepository() {
        this.transactions = new ArrayList<>();
    }

    /**
     * Save a new transaction.
     */
    public InventoryTransaction save(InventoryTransaction transaction) {
        if (transaction.getId() == null || transaction.getId().isEmpty()) {
            transaction.setId("TXN" + String.format("%04d", nextId++));
        }
        transactions.add(transaction);
        return transaction;
    }

    /**
     * Find transaction by ID.
     */
    public Optional<InventoryTransaction> findById(String id) {
        return transactions.stream()
                .filter(txn -> txn.getId().equals(id))
                .findFirst();
    }

    /**
     * Get all transactions.
     */
    public List<InventoryTransaction> findAll() {
        return new ArrayList<>(transactions);
    }

    /**
     * Get transactions for a specific item.
     */
    public List<InventoryTransaction> findByItemId(String itemId) {
        return transactions.stream()
                .filter(txn -> txn.getItemId().equals(itemId))
                .collect(Collectors.toList());
    }

    /**
     * Get transactions by type (IN or OUT).
     */
    public List<InventoryTransaction> findByType(String type) {
        return transactions.stream()
                .filter(txn -> txn.getType().equals(type))
                .collect(Collectors.toList());
    }

    /**
     * Get transactions by staff ID.
     */
    public List<InventoryTransaction> findByStaffId(String staffId) {
        return transactions.stream()
                .filter(txn -> txn.getStaffId() != null && txn.getStaffId().equals(staffId))
                .collect(Collectors.toList());
    }
}



















