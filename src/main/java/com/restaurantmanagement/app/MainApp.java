package com.restaurantmanagement.app;

import com.restaurantmanagement.controller.EmployeeController;
import com.restaurantmanagement.controller.ShiftController;
import com.restaurantmanagement.controller.InventoryController;
import com.restaurantmanagement.controller.BookingController;
import com.restaurantmanagement.model.Employee;
import com.restaurantmanagement.model.Shift;
import com.restaurantmanagement.model.InventoryItem;
import com.restaurantmanagement.model.InventoryTransaction;
import com.restaurantmanagement.model.Booking;
import com.restaurantmanagement.service.EmployeeService;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Main JavaFX application entry point.
 */
public class MainApp extends Application {
    private EmployeeController employeeController;
    private ShiftController shiftController;
    private InventoryController inventoryController;
    private BookingController bookingController;
    private TableView<Employee> employeeTable;
    private TableView<Shift> shiftTable;
    private TableView<InventoryItem> inventoryTable;
    private TableView<InventoryTransaction> transactionTable;
    private TableView<Booking> bookingTable;
    private TextField idField, nameField, positionField, phoneField, emailField;
    private TextField shiftIdField;
    private ComboBox<String> employeeComboBox;
    private DatePicker datePicker;
    private ComboBox<Integer> startHourComboBox, startMinuteComboBox;
    private ComboBox<Integer> endHourComboBox, endMinuteComboBox;
    private ComboBox<String> shiftTypeComboBox;
    private LocalDate currentWeekStart;
    
    // Dashboard components for refresh
    private Tab dashboardTab;
    private HBox dashboardMetricsRow;
    private HBox dashboardAlertsRow;
    private VBox dashboardTodayShiftsBox;
    private VBox dashboardTodayBookingsBox;
    private VBox dashboardAlertsBox;
    private VBox dashboardRecentActivityBox;
    private VBox dashboardContent;

    @Override
    public void start(Stage primaryStage) {
        // Create shared EmployeeService instance so both controllers use the same data
        EmployeeService sharedEmployeeService = new EmployeeService();
        employeeController = new EmployeeController(sharedEmployeeService);
        shiftController = new ShiftController(sharedEmployeeService);
        inventoryController = new InventoryController();
        bookingController = new BookingController();
        
        // Initialize current week to start of current week
        currentWeekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY);

        primaryStage.setTitle("Restaurant Management System");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(700);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setResizable(true);
        primaryStage.setMaximized(false);

        VBox root = createMainLayout();
        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Create the main application layout with navigation tabs.
     */
    private VBox createMainLayout() {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Title Bar
        HBox titleBar = createTitleBar();
        
        // Tab Navigation
        TabPane tabPane = createTabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        root.getChildren().addAll(titleBar, tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        
        return root;
    }

    /**
     * Create the title bar.
     */
    private HBox createTitleBar() {
        HBox titleBar = new HBox();
        titleBar.setPadding(new Insets(15, 20, 15, 20));
        titleBar.setStyle("-fx-background-color: #2c3e50;");
        titleBar.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Restaurant Management System");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        titleBar.getChildren().add(titleLabel);
        return titleBar;
    }

    /**
     * Create the tab pane with all navigation tabs.
     */
    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-tab-min-width: 120px;");
        
        // Dashboard Tab (stored in instance variable)
        createDashboardTab();
        
        // Employee Management Tab
        Tab employeeTab = createEmployeeTab();
        
        // Shift Assignment Tab
        Tab shiftTab = createShiftTab();
        
        // Inventory Tab
        Tab inventoryTab = createInventoryTab();
        
        // Booking Management Tab
        Tab bookingTab = createBookingTab();
        
        tabPane.getTabs().addAll(dashboardTab, employeeTab, shiftTab, inventoryTab, bookingTab);
        
        // Add listener to refresh data when tabs are selected
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == shiftTab && shiftController != null) {
                shiftController.refreshEmployeeComboBox();
            }
            if (newTab == dashboardTab) {
                refreshDashboard();
            }
        });
        
        return tabPane;
    }

    /**
     * Create Dashboard tab with comprehensive insights.
     */
    private Tab createDashboardTab() {
        dashboardTab = new Tab("Dashboard");
        dashboardTab.setClosable(false);
        
        dashboardContent = new VBox(15);
        dashboardContent.setPadding(new Insets(20));
        dashboardContent.setStyle("-fx-background-color: #f5f7fa;");
        
        // Build initial dashboard
        buildDashboardContent();
        
        ScrollPane scrollPane = new ScrollPane(dashboardContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: #f5f7fa;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        dashboardTab.setContent(scrollPane);
        return dashboardTab;
    }
    
    /**
     * Build or rebuild dashboard content.
     */
    private void buildDashboardContent() {
        dashboardContent.getChildren().clear();
        
        // Header with date
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(0, 0, 10, 0));
        
        Label titleLabel = new Label("Restaurant Dashboard");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label dateLabel = new Label("Today: " + LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; -fx-padding: 0 0 0 20;");
        
        headerBox.getChildren().addAll(titleLabel, dateLabel);
        
        // Key Metrics Row
        dashboardMetricsRow = new HBox(15);
        dashboardMetricsRow.setPadding(new Insets(0, 0, 15, 0));
        updateMetricsRow();
        
        // Alerts and Quick Stats Row
        dashboardAlertsRow = new HBox(15);
        dashboardAlertsRow.setPadding(new Insets(0, 0, 15, 0));
        updateAlertsRow();
        
        // Main Content Area - Two Columns
        HBox mainContent = new HBox(15);
        
        // Left Column - Today's Schedule
        VBox leftColumn = new VBox(10);
        leftColumn.setPrefWidth(500);
        
        dashboardTodayShiftsBox = createTodayShiftsSection();
        dashboardTodayBookingsBox = createTodayBookingsSection();
        
        leftColumn.getChildren().addAll(dashboardTodayShiftsBox, dashboardTodayBookingsBox);
        VBox.setVgrow(dashboardTodayShiftsBox, Priority.ALWAYS);
        VBox.setVgrow(dashboardTodayBookingsBox, Priority.ALWAYS);
        
        // Right Column - Alerts & Recent Activity
        VBox rightColumn = new VBox(10);
        rightColumn.setPrefWidth(500);
        
        dashboardAlertsBox = createAlertsSection();
        dashboardRecentActivityBox = createRecentActivitySection();
        
        rightColumn.getChildren().addAll(dashboardAlertsBox, dashboardRecentActivityBox);
        VBox.setVgrow(dashboardAlertsBox, Priority.SOMETIMES);
        VBox.setVgrow(dashboardRecentActivityBox, Priority.ALWAYS);
        
        mainContent.getChildren().addAll(leftColumn, rightColumn);
        HBox.setHgrow(leftColumn, Priority.ALWAYS);
        HBox.setHgrow(rightColumn, Priority.ALWAYS);
        
        dashboardContent.getChildren().addAll(headerBox, dashboardMetricsRow, dashboardAlertsRow, mainContent);
    }
    
    /**
     * Update metrics row with current data.
     */
    private void updateMetricsRow() {
        dashboardMetricsRow.getChildren().clear();
        
        // Calculate real statistics from current controller data
        int totalEmployees = employeeController.getEmployeeList().size();
        long todayShifts = shiftController.getShiftList().stream()
                .filter(s -> s.getDate() != null && s.getDate().equals(LocalDate.now()))
                .count();
        int totalInventoryItems = inventoryController.getItemList().size();
        long todayBookings = bookingController.getBookingList().stream()
                .filter(b -> b.getDate() != null && b.getDate().equals(LocalDate.now()))
                .count();
        
        dashboardMetricsRow.getChildren().addAll(
            createInsightCard("Total Employees", String.valueOf(totalEmployees), "#3498db", "👥"),
            createInsightCard("Today's Shifts", String.valueOf(todayShifts), "#2ecc71", "🕐"),
            createInsightCard("Inventory Items", String.valueOf(totalInventoryItems), "#9b59b6", "📦"),
            createInsightCard("Today's Bookings", String.valueOf(todayBookings), "#e74c3c", "📅")
        );
    }
    
    /**
     * Update alerts row with current data.
     */
    private void updateAlertsRow() {
        dashboardAlertsRow.getChildren().clear();
        
        long pendingBookings = bookingController.getBookingList().stream()
                .filter(b -> b.getStatus() != null && b.getStatus().equals("CONFIRMED"))
                .count();
        long lowStockItems = inventoryController.getItemList().stream()
                .filter(InventoryItem::isLowStock)
                .count();
        
        dashboardAlertsRow.getChildren().addAll(
            createInsightCard("Pending Bookings", String.valueOf(pendingBookings), "#f39c12", "⏳"),
            createInsightCard("Low Stock Alerts", String.valueOf(lowStockItems), lowStockItems > 0 ? "#e74c3c" : "#27ae60", "⚠️")
        );
    }
    
    /**
     * Refresh the entire dashboard with latest data.
     */
    private void refreshDashboard() {
        if (dashboardContent == null) return;
        
        // Refresh all controller data
        employeeController.loadEmployees();
        shiftController.loadShifts();
        inventoryController.loadItems();
        inventoryController.loadTransactions();
        bookingController.loadBookings();
        
        // Rebuild dashboard content
        buildDashboardContent();
    }

    /**
     * Create an insightful statistics card.
     */
    private VBox createInsightCard(String title, String value, String color, String icon) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setPrefWidth(180);
        card.setMinWidth(150);
        
        HBox iconRow = new HBox(10);
        iconRow.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24px;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d; -fx-font-weight: 500;");
        titleLabel.setWrapText(true);
        
        card.getChildren().addAll(iconLabel, valueLabel, titleLabel);
        card.setAlignment(Pos.CENTER_LEFT);
        
        return card;
    }

    /**
     * Create Today's Shifts section.
     */
    private VBox createTodayShiftsSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(15));
        section.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        section.setMinHeight(200);
        
        Label sectionTitle = new Label("📋 Today's Shifts");
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        ListView<String> shiftsList = new ListView<>();
        shiftsList.setPrefHeight(150);
        shiftsList.setStyle("-fx-background-color: #fafafa; -fx-border-color: transparent;");
        
        // Get today's shifts
        List<Shift> todayShifts = shiftController.getShiftList().stream()
                .filter(s -> s.getDate() != null && s.getDate().equals(LocalDate.now()))
                .sorted((s1, s2) -> {
                    if (s1.getStartTime() == null || s2.getStartTime() == null) return 0;
                    return s1.getStartTime().compareTo(s2.getStartTime());
                })
                .collect(Collectors.toList());
        
        if (todayShifts.isEmpty()) {
            shiftsList.getItems().add("No shifts scheduled for today");
        } else {
            for (Shift shift : todayShifts) {
                String timeRange = shift.getStartTime() != null && shift.getEndTime() != null ?
                    String.format("%02d:%02d - %02d:%02d", 
                        shift.getStartTime().getHour(), shift.getStartTime().getMinute(),
                        shift.getEndTime().getHour(), shift.getEndTime().getMinute()) : "TBD";
                String shiftInfo = String.format("%s | %s | %s", 
                    shift.getEmployeeName(), timeRange, shift.getShiftType() != null ? shift.getShiftType() : "");
                shiftsList.getItems().add(shiftInfo);
            }
        }
        
        section.getChildren().addAll(sectionTitle, shiftsList);
        VBox.setVgrow(shiftsList, Priority.ALWAYS);
        
        return section;
    }

    /**
     * Create Today's Bookings section.
     */
    private VBox createTodayBookingsSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(15));
        section.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        section.setMinHeight(200);
        
        Label sectionTitle = new Label("📞 Today's Bookings");
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        ListView<String> bookingsList = new ListView<>();
        bookingsList.setPrefHeight(150);
        bookingsList.setStyle("-fx-background-color: #fafafa; -fx-border-color: transparent;");
        
        // Get today's bookings
        List<Booking> todayBookings = bookingController.getBookingList().stream()
                .filter(b -> b.getDate() != null && b.getDate().equals(LocalDate.now()))
                .sorted((b1, b2) -> {
                    if (b1.getStartTime() == null || b2.getStartTime() == null) return 0;
                    return b1.getStartTime().compareTo(b2.getStartTime());
                })
                .collect(Collectors.toList());
        
        if (todayBookings.isEmpty()) {
            bookingsList.getItems().add("No bookings for today");
        } else {
            for (Booking booking : todayBookings) {
                String time = booking.getStartTime() != null ?
                    String.format("%02d:%02d", booking.getStartTime().getHour(), booking.getStartTime().getMinute()) : "TBD";
                String statusColor = booking.getStatus().equals("CONFIRMED") ? "🟢" : 
                                   booking.getStatus().equals("SEATED") ? "🔵" : "🔴";
                String bookingInfo = String.format("%s %s | %s guests | Table %s | %s", 
                    statusColor, time, booking.getNumberOfGuests(), 
                    booking.getTableId(), booking.getCustomerName());
                bookingsList.getItems().add(bookingInfo);
            }
        }
        
        section.getChildren().addAll(sectionTitle, bookingsList);
        VBox.setVgrow(bookingsList, Priority.ALWAYS);
        
        return section;
    }

    /**
     * Create Alerts section.
     */
    private VBox createAlertsSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(15));
        section.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        
        Label sectionTitle = new Label("⚠️ Alerts & Notifications");
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        VBox alertsContainer = new VBox(8);
        alertsContainer.setPadding(new Insets(5, 0, 0, 0));
        
        // Low stock items
        List<InventoryItem> lowStockItems = inventoryController.getItemList().stream()
                .filter(InventoryItem::isLowStock)
                .limit(5)
                .collect(Collectors.toList());
        
        if (lowStockItems.isEmpty()) {
            Label noAlerts = new Label("✓ No alerts at this time");
            noAlerts.setStyle("-fx-font-size: 13px; -fx-text-fill: #27ae60; -fx-padding: 5;");
            alertsContainer.getChildren().add(noAlerts);
        } else {
            Label alertHeader = new Label("Low Stock Items:");
            alertHeader.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
            alertsContainer.getChildren().add(alertHeader);
            
            for (InventoryItem item : lowStockItems) {
                Label alertItem = new Label(String.format("  • %s: %.1f %s (min: %.1f)", 
                    item.getName(), item.getQuantity(), item.getUnit(), item.getMinimumThreshold()));
                alertItem.setStyle("-fx-font-size: 12px; -fx-text-fill: #c0392b; -fx-padding: 2 0 2 10;");
                alertsContainer.getChildren().add(alertItem);
            }
        }
        
        section.getChildren().addAll(sectionTitle, alertsContainer);
        
        return section;
    }

    /**
     * Create Recent Activity section.
     */
    private VBox createRecentActivitySection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(15));
        section.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        section.setMinHeight(200);
        
        Label sectionTitle = new Label("📊 Recent Inventory Activity");
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        ListView<String> activityList = new ListView<>();
        activityList.setPrefHeight(150);
        activityList.setStyle("-fx-background-color: #fafafa; -fx-border-color: transparent;");
        
        // Get recent transactions (last 10)
        List<InventoryTransaction> recentTransactions = inventoryController.getTransactionList().stream()
                .sorted((t1, t2) -> {
                    if (t1.getTimestamp() == null || t2.getTimestamp() == null) return 0;
                    return t2.getTimestamp().compareTo(t1.getTimestamp()); // Most recent first
                })
                .limit(10)
                .collect(Collectors.toList());
        
        if (recentTransactions.isEmpty()) {
            activityList.getItems().add("No recent activity");
        } else {
            for (InventoryTransaction trans : recentTransactions) {
                String typeIcon = "IN".equals(trans.getType()) ? "⬇️" : "⬆️";
                String timeStr = trans.getTimestamp() != null ?
                    trans.getTimestamp().format(DateTimeFormatter.ofPattern("MM/dd HH:mm")) : "";
                String activity = String.format("%s %s %s | %.1f %s | %s", 
                    typeIcon, trans.getType(), trans.getItemName(), 
                    trans.getQuantity(), trans.getReason(), timeStr);
                activityList.getItems().add(activity);
            }
        }
        
        section.getChildren().addAll(sectionTitle, activityList);
        VBox.setVgrow(activityList, Priority.ALWAYS);
        
        return section;
    }

    /**
     * Create Employee Management tab.
     */
    private Tab createEmployeeTab() {
        Tab tab = new Tab("Employees");
        tab.setClosable(false);
        
        HBox employeeContent = new HBox(15);
        employeeContent.setPadding(new Insets(15));
        employeeContent.setStyle("-fx-background-color: #f5f5f5;");

        // Left side - Employee Table
        VBox tableSection = createTableSection();
        tableSection.setPrefWidth(700);

        // Right side - Form
        VBox formSection = createFormSection();
        formSection.setPrefWidth(400);

        employeeContent.getChildren().addAll(tableSection, formSection);
        
        tab.setContent(employeeContent);
        return tab;
    }

    private VBox calendarViewContainer;

    /**
     * Create Shift Assignment tab with Google Calendar-style view.
     */
    private Tab createShiftTab() {
        Tab tab = new Tab("Shifts");
        tab.setClosable(false);
        
        HBox shiftContent = new HBox(15);
        shiftContent.setPadding(new Insets(15));
        shiftContent.setStyle("-fx-background-color: #f5f5f5;");

        // Left side - Calendar view (takes most of the space)
        VBox calendarSection = createCalendarView();
        calendarViewContainer = calendarSection;
        HBox.setHgrow(calendarSection, Priority.ALWAYS);

        // Right side - Form panel
        VBox formSection = createShiftFormSection();
        formSection.setPrefWidth(400);
        formSection.setMinWidth(350);
        formSection.setMaxWidth(450);

        shiftContent.getChildren().addAll(calendarSection, formSection);
        
        tab.setContent(shiftContent);
        return tab;
    }

    // Inventory UI Components
    private TextField inventoryIdField, inventoryNameField, inventoryQuantityField;
    private TextField inventoryThresholdField, inventorySupplierField;
    private ComboBox<String> inventoryCategoryComboBox, inventoryUnitComboBox;
    private ComboBox<String> inventoryStorageComboBox;
    private ComboBox<String> stockItemComboBox, stockReasonComboBox, stockTypeComboBox;
    private TextField stockQuantityField;
    private TextField inventorySearchField;
    private ComboBox<String> inventoryCategoryFilterComboBox;
    private CheckBox lowStockCheckBox;

    /**
     * Create Inventory tab with full management capabilities.
     */
    private Tab createInventoryTab() {
        Tab tab = new Tab("Inventory");
        tab.setClosable(false);
        
        VBox inventoryContent = new VBox(10);
        inventoryContent.setPadding(new Insets(15));
        inventoryContent.setStyle("-fx-background-color: #f5f5f5;");

        // Top section - Search and Filter
        VBox searchSection = createInventorySearchSection();

        // Main content - Split into left (table) and right (forms)
        HBox mainContent = new HBox(15);
        
        // Left side - Item table
        VBox tableSection = createInventoryTableSection();
        HBox.setHgrow(tableSection, Priority.ALWAYS);

        // Right side - Forms (Item form and Stock form) - wrapped in ScrollPane
        ScrollPane formScrollPane = createInventoryFormSection();
        formScrollPane.setPrefWidth(450);
        formScrollPane.setMinWidth(400);

        mainContent.getChildren().addAll(tableSection, formScrollPane);

        inventoryContent.getChildren().addAll(searchSection, mainContent);
        VBox.setVgrow(mainContent, Priority.ALWAYS);
        
        tab.setContent(inventoryContent);
        return tab;
    }

    // Booking UI Components
    private TextField bookingIdField, bookingCustomerNameField, bookingPhoneField;
    private TextField bookingGuestsField, bookingTableIdField;
    private DatePicker bookingDatePicker;
    private ComboBox<Integer> bookingHourComboBox, bookingMinuteComboBox;
    private ComboBox<String> bookingStatusComboBox;

    /**
     * Create Booking Management tab.
     */
    private Tab createBookingTab() {
        Tab tab = new Tab("Booking");
        tab.setClosable(false);
        
        HBox bookingContent = new HBox(15);
        bookingContent.setPadding(new Insets(15));
        bookingContent.setStyle("-fx-background-color: #f5f5f5;");

        // Left side - Booking Table
        VBox tableSection = createBookingTableSection();
        tableSection.setPrefWidth(800);
        HBox.setHgrow(tableSection, Priority.ALWAYS);

        // Right side - Form (wrapped in ScrollPane)
        ScrollPane formScrollPane = createBookingFormSection();
        formScrollPane.setPrefWidth(400);
        formScrollPane.setMinWidth(350);

        bookingContent.getChildren().addAll(tableSection, formScrollPane);
        
        tab.setContent(bookingContent);
        return tab;
    }

    /**
     * Create booking table section.
     */
    private VBox createBookingTableSection() {
        VBox tableBox = new VBox(10);
        tableBox.setPadding(new Insets(10));
        tableBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");

        Label tableLabel = new Label("All Bookings");
        tableLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        bookingTable = new TableView<>();
        bookingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        bookingTable.setPrefHeight(700);

        // Table columns
        TableColumn<Booking, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(80);

        TableColumn<Booking, String> customerColumn = new TableColumn<>("Customer");
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerColumn.setPrefWidth(150);

        TableColumn<Booking, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        phoneColumn.setPrefWidth(120);

        TableColumn<Booking, Integer> guestsColumn = new TableColumn<>("Guests");
        guestsColumn.setCellValueFactory(new PropertyValueFactory<>("numberOfGuests"));
        guestsColumn.setPrefWidth(70);

        TableColumn<Booking, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDate();
            return new javafx.beans.property.SimpleStringProperty(
                date != null ? date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : ""
            );
        });
        dateColumn.setPrefWidth(120);

        TableColumn<Booking, String> timeColumn = new TableColumn<>("Time");
        timeColumn.setCellValueFactory(cellData -> {
            LocalTime time = cellData.getValue().getStartTime();
            return new javafx.beans.property.SimpleStringProperty(
                time != null ? String.format("%02d:%02d", time.getHour(), time.getMinute()) : ""
            );
        });
        timeColumn.setPrefWidth(80);

        TableColumn<Booking, String> tableColumn = new TableColumn<>("Table");
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("tableId"));
        tableColumn.setPrefWidth(80);

        TableColumn<Booking, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setPrefWidth(120);

        bookingTable.getColumns().addAll(idColumn, customerColumn, phoneColumn, guestsColumn,
                                         dateColumn, timeColumn, tableColumn, statusColumn);

        // Bind table to controller
        bookingController.setTableView(bookingTable);
        bookingTable.setItems(bookingController.getBookingList());

        // Handle row selection
        bookingTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        bookingController.handleTableSelection();
                    }
                }
        );

        tableBox.getChildren().addAll(tableLabel, bookingTable);
        return tableBox;
    }

    /**
     * Create booking form section wrapped in ScrollPane.
     */
    private ScrollPane createBookingFormSection() {
        VBox formBox = new VBox(15);
        formBox.setPadding(new Insets(10));
        formBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");

        Label formLabel = new Label("Booking Form");
        formLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Form fields
        bookingIdField = new TextField();
        bookingIdField.setPromptText("ID (auto-generated)");
        bookingIdField.setEditable(false);
        bookingIdField.setStyle("-fx-background-color: #f0f0f0;");

        bookingCustomerNameField = new TextField();
        bookingCustomerNameField.setPromptText("Customer Name *");

        bookingPhoneField = new TextField();
        bookingPhoneField.setPromptText("Phone Number *");

        bookingGuestsField = new TextField();
        bookingGuestsField.setPromptText("Number of Guests *");

        bookingDatePicker = new DatePicker();
        bookingDatePicker.setPromptText("Date *");
        bookingDatePicker.setValue(LocalDate.now());

        // Time - Hour and Minute
        HBox timeBox = new HBox(5);
        timeBox.setAlignment(Pos.CENTER_LEFT);
        Label timeLabel = new Label("Time:");
        bookingHourComboBox = new ComboBox<>();
        bookingHourComboBox.setPrefWidth(80);
        Label colon = new Label(":");
        bookingMinuteComboBox = new ComboBox<>();
        bookingMinuteComboBox.setPrefWidth(80);
        timeBox.getChildren().addAll(timeLabel, bookingHourComboBox, colon, bookingMinuteComboBox);

        bookingTableIdField = new TextField();
        bookingTableIdField.setPromptText("Table ID *");

        bookingStatusComboBox = new ComboBox<>();
        bookingStatusComboBox.setPromptText("Status *");
        bookingStatusComboBox.setPrefWidth(Double.MAX_VALUE);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button addButton = new Button("Add");
        addButton.setPrefWidth(70);
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addButton.setOnAction(e -> bookingController.handleAdd());

        Button updateButton = new Button("Update");
        updateButton.setPrefWidth(70);
        updateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        updateButton.setOnAction(e -> bookingController.handleUpdate());

        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(70);
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        cancelButton.setOnAction(e -> bookingController.handleCancel());

        Button seatButton = new Button("Seat");
        seatButton.setPrefWidth(70);
        seatButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        seatButton.setOnAction(e -> bookingController.handleSeatCustomer());

        Button clearButton = new Button("Clear");
        clearButton.setPrefWidth(70);
        clearButton.setOnAction(e -> {
            bookingIdField.clear();
            bookingCustomerNameField.clear();
            bookingPhoneField.clear();
            bookingGuestsField.clear();
            bookingDatePicker.setValue(LocalDate.now());
            bookingHourComboBox.setValue(18);
            bookingMinuteComboBox.setValue(0);
            bookingTableIdField.clear();
            bookingStatusComboBox.setValue("CONFIRMED");
            bookingTable.getSelectionModel().clearSelection();
        });

        buttonBox.getChildren().addAll(addButton, updateButton, cancelButton, seatButton, clearButton);

        // Set form fields in controller
        bookingController.setFormFields(bookingIdField, bookingCustomerNameField, bookingPhoneField,
                                       bookingGuestsField, bookingDatePicker,
                                       bookingHourComboBox, bookingMinuteComboBox,
                                       bookingTableIdField, bookingStatusComboBox);

        formBox.getChildren().addAll(
                formLabel,
                new Label("ID:"),
                bookingIdField,
                new Label("Customer Name:"),
                bookingCustomerNameField,
                new Label("Phone:"),
                bookingPhoneField,
                new Label("Number of Guests:"),
                bookingGuestsField,
                new Label("Date:"),
                bookingDatePicker,
                timeBox,
                new Label("Table ID:"),
                bookingTableIdField,
                new Label("Status:"),
                bookingStatusComboBox,
                buttonBox
        );

        // Wrap in ScrollPane to allow scrolling when content exceeds available space
        ScrollPane scrollPane = new ScrollPane(formBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: white;");
        
        return scrollPane;
    }

    /**
     * Create the employee table section.
     */
    private VBox createTableSection() {
        VBox tableBox = new VBox(10);
        tableBox.setPadding(new Insets(10));
        tableBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");

        Label tableLabel = new Label("Employee List");
        tableLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        employeeTable = new TableView<>();
        employeeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        employeeTable.setPrefHeight(600);

        // Table columns
        TableColumn<Employee, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(80);

        TableColumn<Employee, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(150);

        TableColumn<Employee, String> positionColumn = new TableColumn<>("Position");
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        positionColumn.setPrefWidth(120);

        TableColumn<Employee, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        phoneColumn.setPrefWidth(120);

        TableColumn<Employee, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailColumn.setPrefWidth(200);

        employeeTable.getColumns().addAll(idColumn, nameColumn, positionColumn, phoneColumn, emailColumn);

        // Bind table to controller
        employeeController.setTableView(employeeTable);
        employeeTable.setItems(employeeController.getEmployeeList());

        // Handle row selection
        employeeTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        employeeController.handleTableSelection();
                    }
                }
        );

        tableBox.getChildren().addAll(tableLabel, employeeTable);
        return tableBox;
    }

    /**
     * Create the form section.
     */
    private VBox createFormSection() {
        VBox formBox = new VBox(15);
        formBox.setPadding(new Insets(10));
        formBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");

        Label formLabel = new Label("Employee Form");
        formLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Form fields
        idField = new TextField();
        idField.setPromptText("ID (auto-generated)");
        idField.setEditable(false);
        idField.setStyle("-fx-background-color: #f0f0f0;");

        nameField = new TextField();
        nameField.setPromptText("Name *");

        positionField = new TextField();
        positionField.setPromptText("Position *");

        phoneField = new TextField();
        phoneField.setPromptText("Phone Number");

        emailField = new TextField();
        emailField.setPromptText("Email");

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button addButton = new Button("Add");
        addButton.setPrefWidth(80);
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addButton.setOnAction(e -> {
            employeeController.handleAdd();
            shiftController.refreshEmployeeComboBox();
        });

        Button updateButton = new Button("Update");
        updateButton.setPrefWidth(80);
        updateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        updateButton.setOnAction(e -> {
            employeeController.handleUpdate();
            shiftController.refreshEmployeeComboBox();
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setPrefWidth(80);
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> {
            employeeController.handleDelete();
            shiftController.refreshEmployeeComboBox();
        });

        Button clearButton = new Button("Clear");
        clearButton.setPrefWidth(80);
        clearButton.setOnAction(e -> {
            idField.clear();
            nameField.clear();
            positionField.clear();
            phoneField.clear();
            emailField.clear();
            employeeTable.getSelectionModel().clearSelection();
        });

        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);

        // Set form fields in controller
        employeeController.setFormFields(idField, nameField, positionField, phoneField, emailField);

        formBox.getChildren().addAll(
                formLabel,
                new Label("ID:"),
                idField,
                new Label("Name:"),
                nameField,
                new Label("Position:"),
                positionField,
                new Label("Phone:"),
                phoneField,
                new Label("Email:"),
                emailField,
                buttonBox
        );

        return formBox;
    }

    /**
     * Create the shift table section.
     */
    private VBox createShiftTableSection() {
        VBox tableBox = new VBox(10);
        tableBox.setPadding(new Insets(10));
        tableBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");

        Label tableLabel = new Label("Shift List");
        tableLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        shiftTable = new TableView<>();
        shiftTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        shiftTable.setPrefHeight(600);

        // Table columns
        TableColumn<Shift, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(80);

        TableColumn<Shift, String> employeeColumn = new TableColumn<>("Employee");
        employeeColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        employeeColumn.setPrefWidth(150);

        TableColumn<Shift, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDate();
            return new javafx.beans.property.SimpleStringProperty(
                date != null ? date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : ""
            );
        });
        dateColumn.setPrefWidth(120);

        TableColumn<Shift, String> startTimeColumn = new TableColumn<>("Start");
        startTimeColumn.setCellValueFactory(cellData -> {
            var time = cellData.getValue().getStartTime();
            return new javafx.beans.property.SimpleStringProperty(
                time != null ? String.format("%02d:%02d", time.getHour(), time.getMinute()) : ""
            );
        });
        startTimeColumn.setPrefWidth(80);

        TableColumn<Shift, String> endTimeColumn = new TableColumn<>("End");
        endTimeColumn.setCellValueFactory(cellData -> {
            var time = cellData.getValue().getEndTime();
            return new javafx.beans.property.SimpleStringProperty(
                time != null ? String.format("%02d:%02d", time.getHour(), time.getMinute()) : ""
            );
        });
        endTimeColumn.setPrefWidth(80);

        TableColumn<Shift, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("shiftType"));
        typeColumn.setPrefWidth(100);

        shiftTable.getColumns().addAll(idColumn, employeeColumn, dateColumn, startTimeColumn, endTimeColumn, typeColumn);

        // Bind table to controller
        shiftController.setTableView(shiftTable);
        shiftTable.setItems(shiftController.getShiftList());

        // Handle row selection
        shiftTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        shiftController.handleTableSelection();
                    }
                }
        );

        tableBox.getChildren().addAll(tableLabel, shiftTable);
        return tableBox;
    }

    /**
     * Create Google Calendar-style weekly view.
     */
    private VBox createCalendarView() {
        VBox calendarBox = new VBox(10);
        calendarBox.setPadding(new Insets(10));
        calendarBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");
        calendarBox.setMinWidth(800);

        // Header with navigation
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(10));

        Button prevWeekButton = new Button("◀ Previous");
        prevWeekButton.setOnAction(e -> {
            currentWeekStart = currentWeekStart.minusWeeks(1);
            refreshCalendarView(calendarBox);
        });

        Button nextWeekButton = new Button("Next ▶");
        nextWeekButton.setOnAction(e -> {
            currentWeekStart = currentWeekStart.plusWeeks(1);
            refreshCalendarView(calendarBox);
        });

        Button todayButton = new Button("Today");
        todayButton.setOnAction(e -> {
            currentWeekStart = LocalDate.now().with(DayOfWeek.MONDAY);
            refreshCalendarView(calendarBox);
        });

        Label weekLabel = new Label();
        weekLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        updateWeekLabel(weekLabel);

        headerBox.getChildren().addAll(prevWeekButton, todayButton, weekLabel, nextWeekButton);
        HBox.setHgrow(weekLabel, Priority.ALWAYS);
        weekLabel.setAlignment(Pos.CENTER);

        // Calendar grid
        GridPane calendarGrid = new GridPane();
        calendarGrid.setHgap(1);
        calendarGrid.setVgap(1);
        calendarGrid.setStyle("-fx-background-color: #e0e0e0;");
        
        // Initialize row constraints for all time slots (6 AM to 11 PM = 18 rows)
        for (int i = 0; i < 19; i++) {
            RowConstraints rowConstraint = new RowConstraints();
            rowConstraint.setMinHeight(50);
            rowConstraint.setPrefHeight(50);
            rowConstraint.setMaxHeight(50);
            calendarGrid.getRowConstraints().add(rowConstraint);
        }

        // Day headers - ensure equal width for all 7 days
        String[] dayNames = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (int i = 0; i < 7; i++) {
            VBox dayHeader = new VBox(5);
            dayHeader.setAlignment(Pos.CENTER);
            dayHeader.setPadding(new Insets(10));
            dayHeader.setStyle("-fx-background-color: #4285f4;");
            dayHeader.setMaxWidth(Double.MAX_VALUE);
            
            Label dayNameLabel = new Label(dayNames[i]);
            dayNameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 12px;");
            
            LocalDate dayDate = currentWeekStart.plusDays(i);
            Label dateLabel = new Label(dayDate.format(DateTimeFormatter.ofPattern("MMM d")));
            dateLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            
            dayHeader.getChildren().addAll(dayNameLabel, dateLabel);
            GridPane.setHgrow(dayHeader, Priority.ALWAYS);
            calendarGrid.add(dayHeader, i + 1, 0);
        }

        // Pre-calculate which hours are covered by spanning shifts for each day
        Map<LocalDate, Set<Integer>> coveredHours = new HashMap<>();
        for (int day = 0; day < 7; day++) {
            LocalDate dayDate = currentWeekStart.plusDays(day);
            List<Shift> allShiftsForDate = shiftController.getShiftsByDate(dayDate);
            Set<Integer> covered = new HashSet<>();
            
            for (Shift shift : allShiftsForDate) {
                if (shift.getStartTime() != null && shift.getEndTime() != null) {
                    int startHour = shift.getStartTime().getHour();
                    int startMinute = shift.getStartTime().getMinute();
                    int endHour = shift.getEndTime().getHour();
                    int endMinute = shift.getEndTime().getMinute();
                    
                    int startTotalMinutes = startHour * 60 + startMinute;
                    int endTotalMinutes = endHour * 60 + endMinute;
                    int durationMinutes = endTotalMinutes - startTotalMinutes;
                    int rowSpan = Math.max(1, (int) Math.ceil(durationMinutes / 60.0));
                    
                    // Mark all hours covered by this shift (except the starting hour)
                    for (int h = 1; h < rowSpan; h++) {
                        int coveredHour = startHour + h;
                        if (coveredHour < 24) {
                            covered.add(coveredHour);
                        }
                    }
                }
            }
            coveredHours.put(dayDate, covered);
        }

        // Time slots (6 AM to 11 PM) - ensure full week is visible
        for (int hour = 6; hour < 24; hour++) {
            // Time label column
            Label timeLabel = new Label(String.format("%02d:00", hour));
            timeLabel.setStyle("-fx-font-size: 11px; -fx-padding: 5px;");
            timeLabel.setPrefWidth(60);
            timeLabel.setMinWidth(60);
            timeLabel.setAlignment(Pos.CENTER_RIGHT);
            calendarGrid.add(timeLabel, 0, hour - 5);

            // Day columns - ensure equal width
            for (int day = 0; day < 7; day++) {
                final LocalDate slotDate = currentWeekStart.plusDays(day);
                final int hourSlot = hour;
                
                // Check if this hour is already covered by a shift from a previous hour
                Set<Integer> covered = coveredHours.getOrDefault(slotDate, Collections.emptySet());
                boolean isCovered = covered.contains(hourSlot);
                
                // Get all shifts for this date
                List<Shift> allShiftsForDate = shiftController.getShiftsByDate(slotDate);
                
                // Find shifts that START in this hour slot (always show shifts in their starting hour)
                List<Shift> shiftsStartingInSlot = allShiftsForDate.stream()
                        .filter(s -> s.getStartTime() != null && s.getEndTime() != null &&
                                s.getStartTime().getHour() == hourSlot)
                        .collect(Collectors.toList());
                
                // Create time slot container
                // Use StackPane to allow overlapping shifts if needed
                StackPane timeSlot = new StackPane();
                
                // If this hour is covered by a spanning shift, make it transparent
                if (isCovered) {
                    timeSlot.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
                } else {
                    timeSlot.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0;");
                }
                timeSlot.setPadding(new Insets(2));
                // Default height for single hour
                timeSlot.setPrefHeight(50);
                timeSlot.setMinHeight(50);

                // Only add shift content if shifts start here AND this hour is not covered
                if (!shiftsStartingInSlot.isEmpty() && !isCovered) {
                    int maxRowSpan = 1;
                    
                    // Calculate max row span first
                    for (Shift shift : shiftsStartingInSlot) {
                        int startHour = shift.getStartTime().getHour();
                        int startMinute = shift.getStartTime().getMinute();
                        int endHour = shift.getEndTime().getHour();
                        int endMinute = shift.getEndTime().getMinute();
                        
                        int startTotalMinutes = startHour * 60 + startMinute;
                        int endTotalMinutes = endHour * 60 + endMinute;
                        int durationMinutes = endTotalMinutes - startTotalMinutes;
                        
                        // Calculate how many hour slots to span (rounded up)
                        int rowSpan = Math.max(1, (int) Math.ceil(durationMinutes / 60.0));
                        maxRowSpan = Math.max(maxRowSpan, rowSpan);
                    }
                    
                    // Set container height BEFORE adding children
                    timeSlot.setPrefHeight(50 * maxRowSpan);
                    timeSlot.setMinHeight(50 * maxRowSpan);
                    timeSlot.setMaxHeight(50 * maxRowSpan);
                    
                    // Use VBox inside StackPane for vertical stacking
                    VBox shiftsContainer = new VBox(2);
                    shiftsContainer.setMaxWidth(Double.MAX_VALUE);
                    shiftsContainer.setMaxHeight(Double.MAX_VALUE);
                    
                    for (Shift shift : shiftsStartingInSlot) {
                        // Calculate duration in hours (rounded up)
                        int startHour = shift.getStartTime().getHour();
                        int startMinute = shift.getStartTime().getMinute();
                        int endHour = shift.getEndTime().getHour();
                        int endMinute = shift.getEndTime().getMinute();
                        
                        // Calculate total minutes
                        int startTotalMinutes = startHour * 60 + startMinute;
                        int endTotalMinutes = endHour * 60 + endMinute;
                        int durationMinutes = endTotalMinutes - startTotalMinutes;
                        
                        // Calculate how many hour slots to span (minimum 1, rounded up)
                        int rowSpan = Math.max(1, (int) Math.ceil(durationMinutes / 60.0));
                        
                        // Create shift label
                        Label shiftLabel = new Label(shift.getEmployeeName() + "\n" + 
                            String.format("%02d:%02d-%02d:%02d", 
                                shift.getStartTime().getHour(), shift.getStartTime().getMinute(),
                                shift.getEndTime().getHour(), shift.getEndTime().getMinute()));
                        shiftLabel.setStyle("-fx-font-size: 9px; -fx-padding: 2px; -fx-background-color: #4285f4; -fx-text-fill: white; -fx-background-radius: 3px;");
                        shiftLabel.setMaxWidth(Double.MAX_VALUE);
                        shiftLabel.setWrapText(true);
                        shiftLabel.setAlignment(Pos.TOP_LEFT);
                        
                        // Set height based on duration (each hour slot is 50px)
                        // If multiple shifts start at same time, stack them vertically
                        double heightPerShift = (50 * rowSpan - 4) / shiftsStartingInSlot.size();
                        shiftLabel.setPrefHeight(heightPerShift);
                        shiftLabel.setMinHeight(heightPerShift);
                        shiftLabel.setMaxHeight(heightPerShift);
                        
                        shiftLabel.setOnMouseClicked(e -> {
                            datePicker.setValue(slotDate);
                            // Find and select the shift in the table
                            if (shiftTable != null) {
                                shiftTable.getSelectionModel().select(shift);
                                shiftController.handleTableSelection();
                            }
                        });
                        
                        // Add to shifts container
                        shiftsContainer.getChildren().add(shiftLabel);
                    }
                    
                    // Add shifts container to time slot
                    timeSlot.getChildren().add(shiftsContainer);
                    StackPane.setAlignment(shiftsContainer, Pos.TOP_LEFT);
                    
                    // Set row span for the time slot container to span multiple rows
                    // This must be set BEFORE adding to grid
                    if (maxRowSpan > 1) {
                        GridPane.setRowSpan(timeSlot, maxRowSpan);
                    }
                }
                // If no shifts start here, timeSlot remains empty (default state)

                GridPane.setHgrow(timeSlot, Priority.ALWAYS);
                
                // Add to grid
                int gridRow = hour - 5;
                calendarGrid.add(timeSlot, day + 1, gridRow);
            }
        }

        ScrollPane scrollPane = new ScrollPane(calendarGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        calendarBox.getChildren().addAll(headerBox, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return calendarBox;
    }

    /**
     * Refresh the calendar view.
     */
    private void refreshCalendarView(VBox calendarBox) {
        if (calendarBox != null) {
            calendarBox.getChildren().clear();
            VBox newCalendar = createCalendarView();
            calendarBox.getChildren().addAll(newCalendar.getChildren());
        }
    }

    /**
     * Update week label.
     */
    private void updateWeekLabel(Label weekLabel) {
        LocalDate weekEnd = currentWeekStart.plusDays(6);
        String weekText = currentWeekStart.format(DateTimeFormatter.ofPattern("MMM d")) + " - " +
                          weekEnd.format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
        weekLabel.setText(weekText);
    }

    /**
     * Create the shift form section with hour/minute combo boxes.
     */
    private VBox createShiftFormSection() {
        VBox formBox = new VBox(15);
        formBox.setPadding(new Insets(10));
        formBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");

        Label formLabel = new Label("Shift Assignment Form");
        formLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Form fields
        shiftIdField = new TextField();
        shiftIdField.setPromptText("ID (auto-generated)");
        shiftIdField.setEditable(false);
        shiftIdField.setStyle("-fx-background-color: #f0f0f0;");

        employeeComboBox = new ComboBox<>();
        employeeComboBox.setPromptText("Select Employee *");
        employeeComboBox.setPrefWidth(Double.MAX_VALUE);

        datePicker = new DatePicker();
        datePicker.setPromptText("Select Date *");
        datePicker.setValue(LocalDate.now());

        // Start time - Hour and Minute
        HBox startTimeBox = new HBox(5);
        startTimeBox.setAlignment(Pos.CENTER_LEFT);
        Label startTimeLabel = new Label("Start Time:");
        startHourComboBox = new ComboBox<>();
        startHourComboBox.setPrefWidth(80);
        Label colon1 = new Label(":");
        startMinuteComboBox = new ComboBox<>();
        startMinuteComboBox.setPrefWidth(80);
        startTimeBox.getChildren().addAll(startTimeLabel, startHourComboBox, colon1, startMinuteComboBox);

        // End time - Hour and Minute
        HBox endTimeBox = new HBox(5);
        endTimeBox.setAlignment(Pos.CENTER_LEFT);
        Label endTimeLabel = new Label("End Time:");
        endHourComboBox = new ComboBox<>();
        endHourComboBox.setPrefWidth(80);
        Label colon2 = new Label(":");
        endMinuteComboBox = new ComboBox<>();
        endMinuteComboBox.setPrefWidth(80);
        endTimeBox.getChildren().addAll(endTimeLabel, endHourComboBox, colon2, endMinuteComboBox);

        shiftTypeComboBox = new ComboBox<>();
        shiftTypeComboBox.setPromptText("Select Shift Type *");
        shiftTypeComboBox.setPrefWidth(Double.MAX_VALUE);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button addButton = new Button("Add");
        addButton.setPrefWidth(80);
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addButton.setOnAction(e -> {
            shiftController.handleAdd();
            if (calendarViewContainer != null) {
                refreshCalendarView(calendarViewContainer);
            }
        });

        Button updateButton = new Button("Update");
        updateButton.setPrefWidth(80);
        updateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        updateButton.setOnAction(e -> {
            shiftController.handleUpdate();
            if (calendarViewContainer != null) {
                refreshCalendarView(calendarViewContainer);
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setPrefWidth(80);
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> {
            shiftController.handleDelete();
            if (calendarViewContainer != null) {
                refreshCalendarView(calendarViewContainer);
            }
        });

        Button clearButton = new Button("Clear");
        clearButton.setPrefWidth(80);
        clearButton.setOnAction(e -> {
            shiftIdField.clear();
            employeeComboBox.setValue(null);
            datePicker.setValue(LocalDate.now());
            startHourComboBox.setValue(9);
            startMinuteComboBox.setValue(0);
            endHourComboBox.setValue(17);
            endMinuteComboBox.setValue(0);
            shiftTypeComboBox.setValue(null);
            if (shiftTable != null) {
                shiftTable.getSelectionModel().clearSelection();
            }
        });

        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);

        // Set form fields in controller
        shiftController.setFormFields(shiftIdField, employeeComboBox, datePicker, 
                                     startHourComboBox, startMinuteComboBox, 
                                     endHourComboBox, endMinuteComboBox, shiftTypeComboBox);

        formBox.getChildren().addAll(
                formLabel,
                new Label("ID:"),
                shiftIdField,
                new Label("Employee:"),
                employeeComboBox,
                new Label("Date:"),
                datePicker,
                startTimeBox,
                endTimeBox,
                new Label("Shift Type:"),
                shiftTypeComboBox,
                buttonBox
        );

        return formBox;
    }


    /**
     * Create inventory search and filter section.
     */
    private VBox createInventorySearchSection() {
        VBox searchBox = new VBox(10);
        searchBox.setPadding(new Insets(10));
        searchBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");

        Label searchLabel = new Label("Search & Filter");
        searchLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox searchRow = new HBox(10);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label("Search:");
        inventorySearchField = new TextField();
        inventorySearchField.setPromptText("Search by name or ID...");
        inventorySearchField.setPrefWidth(200);

        Label categoryLabel = new Label("Category:");
        inventoryCategoryFilterComboBox = new ComboBox<>();
        inventoryCategoryFilterComboBox.getItems().addAll("All", "Food", "Beverage", "Ingredient", "Cleaning", "Others");
        inventoryCategoryFilterComboBox.setValue("All");
        inventoryCategoryFilterComboBox.setPrefWidth(120);

        lowStockCheckBox = new CheckBox("Low Stock Only");
        
        Button searchButton = new Button("Apply Filter");
        searchButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        searchButton.setOnAction(e -> applyInventoryFilter());

        searchRow.getChildren().addAll(nameLabel, inventorySearchField, categoryLabel, 
                                      inventoryCategoryFilterComboBox, lowStockCheckBox, searchButton);

        searchBox.getChildren().addAll(searchLabel, searchRow);
        return searchBox;
    }

    /**
     * Apply inventory filter.
     */
    private void applyInventoryFilter() {
        String searchText = inventorySearchField.getText();
        String categoryFilter = inventoryCategoryFilterComboBox.getValue();
        if ("All".equals(categoryFilter)) {
            categoryFilter = null;
        }
        boolean lowStockOnly = lowStockCheckBox.isSelected();

        javafx.collections.transformation.FilteredList<InventoryItem> filtered = 
            inventoryController.filterItems(searchText, categoryFilter, lowStockOnly);
        inventoryTable.setItems(filtered);
    }

    /**
     * Create inventory table section.
     */
    private VBox createInventoryTableSection() {
        VBox tableBox = new VBox(10);
        tableBox.setPadding(new Insets(10));
        tableBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");

        Label tableLabel = new Label("Inventory Items");
        tableLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        inventoryTable = new TableView<>();
        inventoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        inventoryTable.setPrefHeight(600);

        // Table columns
        TableColumn<InventoryItem, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(80);

        TableColumn<InventoryItem, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(150);

        TableColumn<InventoryItem, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryColumn.setPrefWidth(100);

        TableColumn<InventoryItem, Double> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityColumn.setPrefWidth(80);

        TableColumn<InventoryItem, String> unitColumn = new TableColumn<>("Unit");
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        unitColumn.setPrefWidth(60);

        TableColumn<InventoryItem, Double> thresholdColumn = new TableColumn<>("Min Threshold");
        thresholdColumn.setCellValueFactory(new PropertyValueFactory<>("minimumThreshold"));
        thresholdColumn.setPrefWidth(100);

        TableColumn<InventoryItem, String> supplierColumn = new TableColumn<>("Supplier");
        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        supplierColumn.setPrefWidth(120);

        TableColumn<InventoryItem, String> locationColumn = new TableColumn<>("Location");
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("storageLocation"));
        locationColumn.setPrefWidth(100);

        // Add low stock indicator column
        TableColumn<InventoryItem, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> {
            InventoryItem item = cellData.getValue();
            String status = item.isLowStock() ? "⚠ Low Stock" : "✓ OK";
            return new javafx.beans.property.SimpleStringProperty(status);
        });
        statusColumn.setPrefWidth(100);

        inventoryTable.getColumns().addAll(idColumn, nameColumn, categoryColumn, quantityColumn,
                                           unitColumn, thresholdColumn, supplierColumn, locationColumn, statusColumn);

        // Bind table to controller
        inventoryController.setItemTableView(inventoryTable);
        inventoryTable.setItems(inventoryController.getItemList());

        // Handle row selection
        inventoryTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        inventoryController.handleTableSelection();
                    }
                }
        );

        tableBox.getChildren().addAll(tableLabel, inventoryTable);
        return tableBox;
    }

    /**
     * Create inventory form section (Item form + Stock form) wrapped in ScrollPane.
     */
    private ScrollPane createInventoryFormSection() {
        VBox formSection = new VBox(15);
        formSection.setPadding(new Insets(10));

        // Item Management Form
        VBox itemFormBox = createInventoryItemForm();
        
        // Stock In/Out Form
        VBox stockFormBox = createStockInOutForm();

        // Transaction History
        VBox transactionBox = createTransactionHistory();

        formSection.getChildren().addAll(itemFormBox, stockFormBox, transactionBox);
        
        // Wrap in ScrollPane to allow scrolling when content exceeds available space
        ScrollPane scrollPane = new ScrollPane(formSection);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: white;");
        
        return scrollPane;
    }

    /**
     * Create inventory item form.
     */
    private VBox createInventoryItemForm() {
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(10));
        formBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");

        Label formLabel = new Label("Item Management");
        formLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Form fields
        inventoryIdField = new TextField();
        inventoryIdField.setPromptText("ID (auto-generated)");
        inventoryIdField.setEditable(false);
        inventoryIdField.setStyle("-fx-background-color: #f0f0f0;");

        inventoryNameField = new TextField();
        inventoryNameField.setPromptText("Item Name *");

        inventoryCategoryComboBox = new ComboBox<>();
        inventoryCategoryComboBox.setPromptText("Category *");

        inventoryUnitComboBox = new ComboBox<>();
        inventoryUnitComboBox.setPromptText("Unit *");

        inventoryQuantityField = new TextField();
        inventoryQuantityField.setPromptText("Quantity *");

        inventoryThresholdField = new TextField();
        inventoryThresholdField.setPromptText("Minimum Threshold *");

        inventorySupplierField = new TextField();
        inventorySupplierField.setPromptText("Supplier Name");

        inventoryStorageComboBox = new ComboBox<>();
        inventoryStorageComboBox.setPromptText("Storage Location *");

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button addButton = new Button("Add");
        addButton.setPrefWidth(70);
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addButton.setOnAction(e -> inventoryController.handleAdd());

        Button updateButton = new Button("Update");
        updateButton.setPrefWidth(70);
        updateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        updateButton.setOnAction(e -> inventoryController.handleUpdate());

        Button deleteButton = new Button("Delete");
        deleteButton.setPrefWidth(70);
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> inventoryController.handleDelete());

        Button clearButton = new Button("Clear");
        clearButton.setPrefWidth(70);
        clearButton.setOnAction(e -> {
            inventoryIdField.clear();
            inventoryNameField.clear();
            inventoryCategoryComboBox.setValue(null);
            inventoryUnitComboBox.setValue(null);
            inventoryQuantityField.clear();
            inventoryThresholdField.clear();
            inventorySupplierField.clear();
            inventoryStorageComboBox.setValue(null);
            inventoryTable.getSelectionModel().clearSelection();
        });

        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);

        // Set form fields in controller
        inventoryController.setItemFormFields(inventoryIdField, inventoryNameField, inventoryCategoryComboBox,
                                             inventoryUnitComboBox, inventoryQuantityField,
                                             inventoryThresholdField, inventorySupplierField,
                                             inventoryStorageComboBox);

        formBox.getChildren().addAll(
                formLabel,
                new Label("ID:"),
                inventoryIdField,
                new Label("Name:"),
                inventoryNameField,
                new Label("Category:"),
                inventoryCategoryComboBox,
                new Label("Unit:"),
                inventoryUnitComboBox,
                new Label("Quantity:"),
                inventoryQuantityField,
                new Label("Min Threshold:"),
                inventoryThresholdField,
                new Label("Supplier:"),
                inventorySupplierField,
                new Label("Storage Location:"),
                inventoryStorageComboBox,
                buttonBox
        );

        return formBox;
    }

    /**
     * Create stock in/out form.
     */
    private VBox createStockInOutForm() {
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(10));
        formBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");

        Label formLabel = new Label("Stock In/Out");
        formLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        stockItemComboBox = new ComboBox<>();
        stockItemComboBox.setPromptText("Select Item *");
        stockItemComboBox.setPrefWidth(Double.MAX_VALUE);

        stockTypeComboBox = new ComboBox<>();
        stockTypeComboBox.setPromptText("Type *");
        stockTypeComboBox.setPrefWidth(Double.MAX_VALUE);

        stockQuantityField = new TextField();
        stockQuantityField.setPromptText("Quantity *");

        stockReasonComboBox = new ComboBox<>();
        stockReasonComboBox.setPromptText("Reason *");
        stockReasonComboBox.setPrefWidth(Double.MAX_VALUE);

        Button stockButton = new Button("Process Stock");
        stockButton.setPrefWidth(Double.MAX_VALUE);
        stockButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        stockButton.setOnAction(e -> {
            // Use first employee as default staff (in real app, get from session)
            String staffId = "SYS";
            String staffName = "System";
            inventoryController.handleStockOperation(staffId, staffName);
        });

        // Set form fields in controller
        inventoryController.setStockFormFields(stockItemComboBox, stockQuantityField,
                                              stockReasonComboBox, stockTypeComboBox);

        formBox.getChildren().addAll(
                formLabel,
                new Label("Item:"),
                stockItemComboBox,
                new Label("Type:"),
                stockTypeComboBox,
                new Label("Quantity:"),
                stockQuantityField,
                new Label("Reason:"),
                stockReasonComboBox,
                stockButton
        );

        return formBox;
    }

    /**
     * Create transaction history section.
     */
    private VBox createTransactionHistory() {
        VBox transactionBox = new VBox(10);
        transactionBox.setPadding(new Insets(10));
        transactionBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");
        transactionBox.setPrefHeight(250);

        Label transactionLabel = new Label("Recent Transactions");
        transactionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        transactionTable = new TableView<>();
        transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        transactionTable.setPrefHeight(200);

        // Table columns
        TableColumn<InventoryTransaction, String> itemColumn = new TableColumn<>("Item");
        itemColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        itemColumn.setPrefWidth(120);

        TableColumn<InventoryTransaction, Double> qtyColumn = new TableColumn<>("Qty");
        qtyColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        qtyColumn.setPrefWidth(60);

        TableColumn<InventoryTransaction, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeColumn.setPrefWidth(50);

        TableColumn<InventoryTransaction, String> reasonColumn = new TableColumn<>("Reason");
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        reasonColumn.setPrefWidth(80);

        TableColumn<InventoryTransaction, String> staffColumn = new TableColumn<>("Staff");
        staffColumn.setCellValueFactory(new PropertyValueFactory<>("staffName"));
        staffColumn.setPrefWidth(80);

        TableColumn<InventoryTransaction, String> timeColumn = new TableColumn<>("Time");
        timeColumn.setCellValueFactory(cellData -> {
            var time = cellData.getValue().getTimestamp();
            return new javafx.beans.property.SimpleStringProperty(
                time != null ? time.format(DateTimeFormatter.ofPattern("MM/dd HH:mm")) : ""
            );
        });
        timeColumn.setPrefWidth(100);

        transactionTable.getColumns().addAll(itemColumn, qtyColumn, typeColumn, reasonColumn, staffColumn, timeColumn);

        // Bind table to controller
        inventoryController.setTransactionTableView(transactionTable);
        transactionTable.setItems(inventoryController.getTransactionList());

        transactionBox.getChildren().addAll(transactionLabel, transactionTable);
        return transactionBox;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

