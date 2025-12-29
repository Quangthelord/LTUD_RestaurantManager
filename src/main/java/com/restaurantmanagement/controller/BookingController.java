package com.restaurantmanagement.controller;

import com.restaurantmanagement.model.Booking;
import com.restaurantmanagement.service.BookingService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Controller for Booking management UI.
 * Handles user interactions and updates the view.
 */
public class BookingController {
    private final BookingService bookingService;
    private final ObservableList<Booking> bookingList;
    private TableView<Booking> tableView;

    // UI Components (for form inputs)
    private TextField idField;
    private TextField customerNameField;
    private TextField phoneNumberField;
    private TextField numberOfGuestsField;
    private DatePicker datePicker;
    private ComboBox<Integer> hourComboBox;
    private ComboBox<Integer> minuteComboBox;
    private TextField tableIdField;
    private ComboBox<String> statusComboBox;

    public BookingController() {
        this.bookingService = new BookingService();
        this.bookingList = FXCollections.observableArrayList();
        loadBookings();
    }

    /**
     * Set the TableView reference.
     */
    public void setTableView(TableView<Booking> tableView) {
        this.tableView = tableView;
        tableView.setItems(bookingList);
    }

    /**
     * Set form input fields.
     */
    public void setFormFields(TextField idField, TextField customerNameField, TextField phoneNumberField,
                             TextField numberOfGuestsField, DatePicker datePicker,
                             ComboBox<Integer> hourComboBox, ComboBox<Integer> minuteComboBox,
                             TextField tableIdField, ComboBox<String> statusComboBox) {
        this.idField = idField;
        this.customerNameField = customerNameField;
        this.phoneNumberField = phoneNumberField;
        this.numberOfGuestsField = numberOfGuestsField;
        this.datePicker = datePicker;
        this.hourComboBox = hourComboBox;
        this.minuteComboBox = minuteComboBox;
        this.tableIdField = tableIdField;
        this.statusComboBox = statusComboBox;

        // Populate hour combo box (0-23)
        for (int i = 0; i < 24; i++) {
            hourComboBox.getItems().add(i);
        }

        // Populate minute combo box (0, 15, 30, 45)
        for (int i = 0; i < 60; i += 15) {
            minuteComboBox.getItems().add(i);
        }

        // Set default values
        hourComboBox.setValue(18);
        minuteComboBox.setValue(0);

        // Populate status combo box
        statusComboBox.getItems().addAll("CONFIRMED", "SEATED", "CANCELLED");
        statusComboBox.setValue("CONFIRMED");
    }

    /**
     * Load all bookings into the table.
     */
    public void loadBookings() {
        bookingList.clear();
        bookingList.addAll(bookingService.getAllBookings());
    }

    /**
     * Handle add button click.
     */
    public void handleAdd() {
        try {
            Booking booking = createBookingFromForm();
            bookingService.addBooking(booking);
            loadBookings();
            clearForm();
            showSuccessAlert("Booking added successfully!");
        } catch (IllegalArgumentException e) {
            showErrorAlert("Error adding booking", e.getMessage());
        }
    }

    /**
     * Handle update button click.
     */
    public void handleUpdate() {
        try {
            Booking booking = createBookingFromForm();
            if (booking.getId() == null || booking.getId().isEmpty()) {
                showErrorAlert("Error", "Please select a booking to update");
                return;
            }
            bookingService.updateBooking(booking);
            loadBookings();
            clearForm();
            showSuccessAlert("Booking updated successfully!");
        } catch (IllegalArgumentException e) {
            showErrorAlert("Error updating booking", e.getMessage());
        }
    }

    /**
     * Handle cancel button click.
     */
    public void handleCancel() {
        Booking selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorAlert("Error", "Please select a booking to cancel");
            return;
        }

        try {
            bookingService.cancelBooking(selected.getId());
            loadBookings();
            clearForm();
            showSuccessAlert("Booking cancelled successfully!");
        } catch (IllegalArgumentException e) {
            showErrorAlert("Error cancelling booking", e.getMessage());
        }
    }

    /**
     * Handle seat customer button click.
     */
    public void handleSeatCustomer() {
        Booking selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorAlert("Error", "Please select a booking to seat");
            return;
        }

        try {
            bookingService.seatCustomer(selected.getId());
            loadBookings();
            populateForm(selected);
            showSuccessAlert("Customer seated successfully!");
        } catch (IllegalArgumentException e) {
            showErrorAlert("Error seating customer", e.getMessage());
        }
    }

    /**
     * Handle table row selection.
     */
    public void handleTableSelection() {
        Booking selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            populateForm(selected);
        }
    }

    /**
     * Create Booking object from form fields.
     */
    private Booking createBookingFromForm() {
        Booking booking = new Booking();
        booking.setId(idField.getText().trim());
        booking.setCustomerName(customerNameField.getText().trim());
        booking.setPhoneNumber(phoneNumberField.getText().trim());

        try {
            booking.setNumberOfGuests(Integer.parseInt(numberOfGuestsField.getText().trim()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Number of guests must be a valid integer");
        }

        booking.setDate(datePicker.getValue());

        // Create LocalTime from hour and minute combo boxes
        Integer hour = hourComboBox.getValue();
        Integer minute = minuteComboBox.getValue();
        if (hour != null && minute != null) {
            booking.setStartTime(LocalTime.of(hour, minute));
        }

        booking.setTableId(tableIdField.getText().trim());
        booking.setStatus(statusComboBox.getValue());

        return booking;
    }

    /**
     * Populate form fields with selected booking data.
     */
    private void populateForm(Booking booking) {
        idField.setText(booking.getId());
        customerNameField.setText(booking.getCustomerName());
        phoneNumberField.setText(booking.getPhoneNumber());
        numberOfGuestsField.setText(String.valueOf(booking.getNumberOfGuests()));
        datePicker.setValue(booking.getDate());

        if (booking.getStartTime() != null) {
            hourComboBox.setValue(booking.getStartTime().getHour());
            minuteComboBox.setValue(booking.getStartTime().getMinute());
        }

        tableIdField.setText(booking.getTableId());
        statusComboBox.setValue(booking.getStatus());
    }

    /**
     * Clear form fields.
     */
    private void clearForm() {
        idField.clear();
        customerNameField.clear();
        phoneNumberField.clear();
        numberOfGuestsField.clear();
        datePicker.setValue(LocalDate.now());
        hourComboBox.setValue(18);
        minuteComboBox.setValue(0);
        tableIdField.clear();
        statusComboBox.setValue("CONFIRMED");
        if (tableView != null) {
            tableView.getSelectionModel().clearSelection();
        }
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
    public ObservableList<Booking> getBookingList() {
        return bookingList;
    }
}



















