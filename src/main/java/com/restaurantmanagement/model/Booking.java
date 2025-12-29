package com.restaurantmanagement.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Booking entity representing a restaurant reservation.
 */
public class Booking {
    private String id;
    private String customerName;
    private String phoneNumber;
    private int numberOfGuests;
    private LocalDate date;
    private LocalTime startTime;
    private String tableId;
    private String status; // CONFIRMED, SEATED, CANCELLED

    public Booking() {
    }

    public Booking(String id, String customerName, String phoneNumber, int numberOfGuests,
                   LocalDate date, LocalTime startTime, String tableId, String status) {
        this.id = id;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.numberOfGuests = numberOfGuests;
        this.date = date;
        this.startTime = startTime;
        this.tableId = tableId;
        this.status = status;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Check if booking can be cancelled.
     */
    public boolean canCancel() {
        return !"CANCELLED".equals(status) && !"SEATED".equals(status);
    }

    /**
     * Check if booking can be seated.
     */
    public boolean canSeat() {
        return "CONFIRMED".equals(status);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id='" + id + '\'' +
                ", customerName='" + customerName + '\'' +
                ", date=" + date +
                ", time=" + startTime +
                ", status='" + status + '\'' +
                '}';
    }
}



















