package com.restaurantmanagement.controller;

import com.restaurantmanagement.model.InventoryItem;
import com.restaurantmanagement.model.InventoryTransaction;
import com.restaurantmanagement.service.InventoryService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

/**
 * Controller for Inventory management UI.
 * Handles user interactions and updates the view.
 */
public class InventoryController {
    private final InventoryService inventoryService;
    private final ObservableList<InventoryItem> itemList;
    private final ObservableList<InventoryTransaction> transactionList;
    private TableView<InventoryItem> itemTableView;
    private TableView<InventoryTransaction> transactionTableView;

    // UI Components (for form inputs)
    private TextField idField;
    private TextField nameField;
    private ComboBox<String> categoryComboBox;
    private ComboBox<String> unitComboBox;
    private TextField quantityField;
    private TextField minimumThresholdField;
    private TextField supplierNameField;
    private ComboBox<String> storageLocationComboBox;

    // Stock In/Out fields
    private ComboBox<String> stockItemComboBox;
    private TextField stockQuantityField;
    private ComboBox<String> stockReasonComboBox;
    private ComboBox<String> stockTypeComboBox;

    public InventoryController() {
        this.inventoryService = new InventoryService();
        this.itemList = FXCollections.observableArrayList();
        this.transactionList = FXCollections.observableArrayList();
        loadItems();
        loadTransactions();
    }

    /**
     * Set the item TableView reference.
     */
    public void setItemTableView(TableView<InventoryItem> tableView) {
        this.itemTableView = tableView;
        tableView.setItems(itemList);
    }

    /**
     * Set the transaction TableView reference.
     */
    public void setTransactionTableView(TableView<InventoryTransaction> tableView) {
        this.transactionTableView = tableView;
        tableView.setItems(transactionList);
    }

    /**
     * Set form input fields for item management.
     */
    public void setItemFormFields(TextField idField, TextField nameField, ComboBox<String> categoryComboBox,
                                 ComboBox<String> unitComboBox, TextField quantityField,
                                 TextField minimumThresholdField, TextField supplierNameField,
                                 ComboBox<String> storageLocationComboBox) {
        this.idField = idField;
        this.nameField = nameField;
        this.categoryComboBox = categoryComboBox;
        this.unitComboBox = unitComboBox;
        this.quantityField = quantityField;
        this.minimumThresholdField = minimumThresholdField;
        this.supplierNameField = supplierNameField;
        this.storageLocationComboBox = storageLocationComboBox;

        // Populate combo boxes
        categoryComboBox.getItems().addAll("Food", "Beverage", "Ingredient", "Cleaning", "Others");
        unitComboBox.getItems().addAll("kg", "liter", "piece", "box", "pack", "bottle", "can", "bag");
        storageLocationComboBox.getItems().addAll("Kitchen", "Bar", "Warehouse", "Freezer");
    }

    /**
     * Set form input fields for stock in/out.
     */
    public void setStockFormFields(ComboBox<String> stockItemComboBox, TextField stockQuantityField,
                                   ComboBox<String> stockReasonComboBox, ComboBox<String> stockTypeComboBox) {
        this.stockItemComboBox = stockItemComboBox;
        this.stockQuantityField = stockQuantityField;
        this.stockReasonComboBox = stockReasonComboBox;
        this.stockTypeComboBox = stockTypeComboBox;

        // Populate combo boxes
        stockReasonComboBox.getItems().addAll("Purchase", "Sale", "Waste", "Adjustment", "Return");
        stockTypeComboBox.getItems().addAll("IN", "OUT");
        stockTypeComboBox.setValue("IN");

        // Populate item combo box
        refreshItemComboBox();
    }

    /**
     * Refresh item combo box for stock operations.
     */
    public void refreshItemComboBox() {
        if (stockItemComboBox == null) {
            return;
        }
        stockItemComboBox.getItems().clear();
        for (InventoryItem item : itemList) {
            stockItemComboBox.getItems().add(item.getId() + " - " + item.getName());
        }
    }

    /**
     * Load all items into the table.
     */
    public void loadItems() {
        itemList.clear();
        itemList.addAll(inventoryService.getAllItems());
        refreshItemComboBox();
    }

    /**
     * Load all transactions into the table.
     */
    public void loadTransactions() {
        transactionList.clear();
        transactionList.addAll(inventoryService.getAllTransactions());
    }

    /**
     * Handle add button click.
     */
    public void handleAdd() {
        try {
            InventoryItem item = createItemFromForm();
            inventoryService.addItem(item);
            loadItems();
            clearForm();
            showSuccessAlert("Inventory item added successfully!");
        } catch (IllegalArgumentException e) {
            showErrorAlert("Error adding item", e.getMessage());
        }
    }

    /**
     * Handle update button click.
     */
    public void handleUpdate() {
        try {
            InventoryItem item = createItemFromForm();
            if (item.getId() == null || item.getId().isEmpty()) {
                showErrorAlert("Error", "Please select an item to update");
                return;
            }
            inventoryService.updateItem(item);
            loadItems();
            clearForm();
            showSuccessAlert("Inventory item updated successfully!");
        } catch (IllegalArgumentException e) {
            showErrorAlert("Error updating item", e.getMessage());
        }
    }

    /**
     * Handle delete button click.
     */
    public void handleDelete() {
        InventoryItem selected = itemTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorAlert("Error", "Please select an item to delete");
            return;
        }

        try {
            inventoryService.deleteItem(selected.getId());
            loadItems();
            loadTransactions();
            clearForm();
            showSuccessAlert("Inventory item deleted successfully!");
        } catch (IllegalArgumentException e) {
            showErrorAlert("Error deleting item", e.getMessage());
        }
    }

    /**
     * Handle table row selection.
     */
    public void handleTableSelection() {
        InventoryItem selected = itemTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            populateForm(selected);
        }
    }

    /**
     * Handle stock in/out button click.
     */
    public void handleStockOperation(String staffId, String staffName) {
        try {
            String itemSelection = stockItemComboBox.getValue();
            if (itemSelection == null || itemSelection.isEmpty()) {
                showErrorAlert("Error", "Please select an item");
                return;
            }

            String itemId = itemSelection.split(" - ")[0];
            double quantity = Double.parseDouble(stockQuantityField.getText().trim());
            String reason = stockReasonComboBox.getValue();
            String type = stockTypeComboBox.getValue();

            if (reason == null || reason.isEmpty()) {
                showErrorAlert("Error", "Please select a reason");
                return;
            }

            if (type == null || type.isEmpty()) {
                showErrorAlert("Error", "Please select operation type (IN/OUT)");
                return;
            }

            InventoryTransaction transaction;
            if ("IN".equals(type)) {
                transaction = inventoryService.stockIn(itemId, quantity, reason, staffId, staffName);
            } else {
                transaction = inventoryService.stockOut(itemId, quantity, reason, staffId, staffName);
            }

            loadItems();
            loadTransactions();
            clearStockForm();
            showSuccessAlert("Stock " + type + " operation completed successfully!");
        } catch (NumberFormatException e) {
            showErrorAlert("Error", "Please enter a valid quantity");
        } catch (IllegalArgumentException e) {
            showErrorAlert("Error", e.getMessage());
        }
    }

    /**
     * Create InventoryItem object from form fields.
     */
    private InventoryItem createItemFromForm() {
        InventoryItem item = new InventoryItem();
        item.setId(idField.getText().trim());
        item.setName(nameField.getText().trim());
        item.setCategory(categoryComboBox.getValue());
        item.setUnit(unitComboBox.getValue());
        
        try {
            item.setQuantity(Double.parseDouble(quantityField.getText().trim()));
            item.setMinimumThreshold(Double.parseDouble(minimumThresholdField.getText().trim()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Quantity and threshold must be valid numbers");
        }
        
        item.setSupplierName(supplierNameField.getText().trim());
        item.setStorageLocation(storageLocationComboBox.getValue());
        
        return item;
    }

    /**
     * Populate form fields with selected item data.
     */
    private void populateForm(InventoryItem item) {
        idField.setText(item.getId());
        nameField.setText(item.getName());
        categoryComboBox.setValue(item.getCategory());
        unitComboBox.setValue(item.getUnit());
        quantityField.setText(String.valueOf(item.getQuantity()));
        minimumThresholdField.setText(String.valueOf(item.getMinimumThreshold()));
        supplierNameField.setText(item.getSupplierName());
        storageLocationComboBox.setValue(item.getStorageLocation());
    }

    /**
     * Clear form fields.
     */
    private void clearForm() {
        idField.clear();
        nameField.clear();
        categoryComboBox.setValue(null);
        unitComboBox.setValue(null);
        quantityField.clear();
        minimumThresholdField.clear();
        supplierNameField.clear();
        storageLocationComboBox.setValue(null);
        if (itemTableView != null) {
            itemTableView.getSelectionModel().clearSelection();
        }
    }

    /**
     * Clear stock form fields.
     */
    private void clearStockForm() {
        stockItemComboBox.setValue(null);
        stockQuantityField.clear();
        stockReasonComboBox.setValue(null);
        stockTypeComboBox.setValue("IN");
    }

    /**
     * Filter items by search criteria.
     */
    public FilteredList<InventoryItem> filterItems(String searchText, String categoryFilter, boolean lowStockOnly) {
        FilteredList<InventoryItem> filtered = new FilteredList<>(itemList);
        
        filtered.setPredicate(item -> {
            boolean matchesSearch = searchText == null || searchText.isEmpty() ||
                    item.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                    item.getId().toLowerCase().contains(searchText.toLowerCase());
            
            boolean matchesCategory = categoryFilter == null || categoryFilter.isEmpty() ||
                    item.getCategory().equals(categoryFilter);
            
            boolean matchesLowStock = !lowStockOnly || item.isLowStock();
            
            return matchesSearch && matchesCategory && matchesLowStock;
        });
        
        return filtered;
    }

    /**
     * Show success alert.
     */
    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show error alert.
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Get observable list for binding.
     */
    public ObservableList<InventoryItem> getItemList() {
        return itemList;
    }

    /**
     * Get observable list for transactions.
     */
    public ObservableList<InventoryTransaction> getTransactionList() {
        return transactionList;
    }
}



















