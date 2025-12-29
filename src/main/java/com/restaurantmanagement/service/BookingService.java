package com.restaurantmanagement.service;

import com.restaurantmanagement.model.Booking;
import com.restaurantmanagement.repository.InMemoryBookingRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Booking business logic.
 */
public class BookingService {
    private final InMemoryBookingRepository repository;

    public BookingService() {
        this.repository = new InMemoryBookingRepository();
    }

    /**
     * Add a new booking.
     */
    public Booking addBooking(Booking booking) {
        validateBooking(booking);
        return repository.save(booking);
    }

    /**
     * Update an existing booking.
     */
    public Booking updateBooking(Booking booking) {
        if (booking.getId() == null || booking.getId().isEmpty()) {
            throw new IllegalArgumentException("Booking ID is required for update");
        }
        if (!repository.existsById(booking.getId())) {
            throw new IllegalArgumentException("Booking with ID " + booking.getId() + " not found");
        }
        validateBooking(booking);
        return repository.save(booking);
    }

    /**
     * Delete a booking by ID.
     */
    public boolean deleteBooking(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Booking ID cannot be empty");
        }
        return repository.deleteById(id);
    }

    /**
     * Get booking by ID.
     */
    public Optional<Booking> getBookingById(String id) {
        return repository.findById(id);
    }

    /**
     * Get all bookings.
     */
    public List<Booking> getAllBookings() {
        return repository.findAll();
    }

    /**
     * Get bookings by date.
     */
    public List<Booking> getBookingsByDate(LocalDate date) {
        return repository.findByDate(date);
    }

    /**
     * Get bookings by status.
     */
    public List<Booking> getBookingsByStatus(String status) {
        return repository.findByStatus(status);
    }

    /**
     * Search bookings by customer name.
     */
    public List<Booking> searchBookingsByName(String name) {
        return repository.findByCustomerName(name);
    }

    /**
     * Cancel a booking.
     */
    public Booking cancelBooking(String id) {
        Optional<Booking> bookingOpt = repository.findById(id);
        if (!bookingOpt.isPresent()) {
            throw new IllegalArgumentException("Booking with ID " + id + " not found");
        }

        Booking booking = bookingOpt.get();
        if (!booking.canCancel()) {
            throw new IllegalArgumentException("Booking cannot be cancelled. Current status: " + booking.getStatus());
        }

        booking.setStatus("CANCELLED");
        return repository.save(booking);
    }

    /**
     * Seat a customer (change status from CONFIRMED to SEATED).
     */
    public Booking seatCustomer(String id) {
        Optional<Booking> bookingOpt = repository.findById(id);
        if (!bookingOpt.isPresent()) {
            throw new IllegalArgumentException("Booking with ID " + id + " not found");
        }

        Booking booking = bookingOpt.get();
        if (!booking.canSeat()) {
            throw new IllegalArgumentException("Booking cannot be seated. Current status: " + booking.getStatus());
        }

        booking.setStatus("SEATED");
        return repository.save(booking);
    }

    /**
     * Validate booking data.
     */
    private void validateBooking(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null");
        }
        if (booking.getCustomerName() == null || booking.getCustomerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        if (booking.getPhoneNumber() == null || booking.getPhoneNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required");
        }
        if (booking.getNumberOfGuests() <= 0) {
            throw new IllegalArgumentException("Number of guests must be greater than 0");
        }
        if (booking.getDate() == null) {
            throw new IllegalArgumentException("Date is required");
        }
        if (booking.getStartTime() == null) {
            throw new IllegalArgumentException("Time is required");
        }
        if (booking.getTableId() == null || booking.getTableId().trim().isEmpty()) {
            throw new IllegalArgumentException("Table ID is required");
        }
        if (booking.getStatus() == null || booking.getStatus().trim().isEmpty()) {
            throw new IllegalArgumentException("Status is required");
        }
    }
}



















