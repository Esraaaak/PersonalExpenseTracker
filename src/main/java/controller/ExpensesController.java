/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author duhanasir
 */



import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;

import model.Expense;
import service.ExpenseFilterService;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;


public class ExpensesController {
    
        @FXML private TextField searchField;

    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> sortComboBox;

    @FXML private TableView<Expense> expensesTable;

    @FXML private TableColumn<Expense, String> nameColumn;
    @FXML private TableColumn<Expense, String> categoryColumn;
    @FXML private TableColumn<Expense, Double> amountColumn;
    @FXML private TableColumn<Expense, LocalDate> dateColumn;

    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    @FXML private Label statusLabel;

    private final ObservableList<Expense> allExpenses =
            FXCollections.observableArrayList();

    // Temporary categories for testing
    private final ObservableList<String> categories =
            FXCollections.observableArrayList(
                    "Food", "Travel", "Education", "Shopping"
            );

    @FXML
    private void initialize() {

        setupTable();
        setupFilters();
        setupEvents();
        loadMockExpenses();
        refreshTable();
    }

    // Configure table columns
    private void setupTable() {

        nameColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(
                        data.getValue().getName()));

        categoryColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(
                        data.getValue().getCategory()));

        amountColumn.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(
                        data.getValue().getAmount()));

        dateColumn.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(
                        data.getValue().getDate()));

        expensesTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);

        nameColumn.setSortable(false);
        categoryColumn.setSortable(false);
        amountColumn.setSortable(false);
        dateColumn.setSortable(false);
    }

    // Configure category and sorting options
    private void setupFilters() {

        categoryComboBox.getItems().add("All");
        categoryComboBox.getItems().addAll(categories);
        categoryComboBox.setValue("All");

        sortComboBox.getItems().addAll(
                "Default",
                "Amount: Low to High",
                "Amount: High to Low",
                "Date: Newest First",
                "Date: Oldest First"
        );

        sortComboBox.setValue("Default");
    }

    // Configure events using lambdas
    private void setupEvents() {

        ChangeListener<String> searchListener =
                (obs, oldValue, newValue) -> refreshTable();

        searchField.textProperty()
                .addListener(searchListener);

        categoryComboBox.setOnAction(
                e -> refreshTable());

        sortComboBox.setOnAction(
                e -> refreshTable());

        editButton.setOnAction(
                e -> handleEditExpense());

        deleteButton.setOnAction(
                e -> handleDeleteExpense());

        addButton.setOnAction(
                e -> handleAddExpense(e));

        expensesTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldValue, selected) -> {

                    editButton.setDisable(selected == null);
                    deleteButton.setDisable(selected == null);
                });

        // Double-click mouse event
        expensesTable.setRowFactory(table -> {

            TableRow<Expense> row = new TableRow<>();

            row.setOnMouseClicked(event -> {

                if (event.getButton() == MouseButton.PRIMARY
                        && event.getClickCount() == 2
                        && !row.isEmpty()) {

                    expensesTable.getSelectionModel()
                            .select(row.getIndex());

                    handleEditExpense();
                }
            });

            return row;
        });
    }

    // Temporary expense data for testing
    private void loadMockExpenses() {

        allExpenses.setAll(

                new Expense("Coffee", 15.0, "Food",
                        LocalDate.of(2026, 11, 20),
                        "Morning coffee", false),

                new Expense("Uber", 35.0, "Travel",
                        LocalDate.of(2026, 11, 21),
                        "", false),

                new Expense("Lunch", 45.0, "Food",
                        LocalDate.of(2026, 11, 22),
                        "", false),

                new Expense("Books", 120.0, "Education",
                        LocalDate.of(2026, 11, 23),
                        "", false),

                new Expense("Shoes", 200.0, "Shopping",
                        LocalDate.of(2026, 11, 24),
                        "", false)
        );
    }

    // Search, filter and sort expenses
    private void refreshTable() {

        List<Expense> results =
                new ArrayList<>(allExpenses);

        results = ExpenseFilterService.searchExpenses(
                results, searchField.getText());

        results = ExpenseFilterService.filterByCategory(
                results, categoryComboBox.getValue());

        String sort = sortComboBox.getValue();

        if (sort != null) {

            switch (sort) {

                case "Amount: Low to High":
                    results = ExpenseFilterService
                            .sortByAmount(results, true);
                    break;

                case "Amount: High to Low":
                    results = ExpenseFilterService
                            .sortByAmount(results, false);
                    break;

                case "Date: Newest First":
                    results = ExpenseFilterService
                            .sortByDate(results, false);
                    break;

                case "Date: Oldest First":
                    results = ExpenseFilterService
                            .sortByDate(results, true);
                    break;

                default:
                    break;
            }
        }

        expensesTable.setItems(
                FXCollections.observableArrayList(results));

        expensesTable.getSelectionModel().clearSelection();

        editButton.setDisable(true);
        deleteButton.setDisable(true);

        statusLabel.setText(
                results.size() + " expenses found");
    }

    // Edit selected expense
    private void handleEditExpense() {

        Expense selected = expensesTable
                .getSelectionModel().getSelectedItem();

        if (selected == null) {

            showAlert(Alert.AlertType.WARNING,
                    "Select an expense to edit.");
            return;
        }

        Dialog<Expense> dialog = new Dialog<>();

        dialog.setTitle("Edit Expense");
        dialog.setHeaderText("Update expense information");

        ButtonType saveButtonType = new ButtonType(
                "Save", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(
                saveButtonType, ButtonType.CANCEL);

        TextField nameField =
                new TextField(selected.getName());

        TextField amountField =
                new TextField(
                        String.valueOf(selected.getAmount()));

        ComboBox<String> categoryField =
                new ComboBox<>(categories);

        categoryField.setValue(selected.getCategory());

        DatePicker dateField =
                new DatePicker(selected.getDate());

        dateField.setEditable(false);

        TextArea notesField =
                new TextArea(selected.getNotes());

        notesField.setPrefRowCount(3);

        CheckBox recurringField =
                new CheckBox("Recurring Expense");

        recurringField.setSelected(selected.isRecurring());

        GridPane grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        grid.addRow(0, new Label("Name:"), nameField);
        grid.addRow(1, new Label("Amount:"), amountField);
        grid.addRow(2, new Label("Category:"), categoryField);
        grid.addRow(3, new Label("Date:"), dateField);
        grid.addRow(4, new Label("Notes:"), notesField);
        grid.add(recurringField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        Button saveButton = (Button) dialog
                .getDialogPane().lookupButton(saveButtonType);

        // Validate input before saving
        saveButton.addEventFilter(ActionEvent.ACTION, event -> {

            if (nameField.getText().trim().isEmpty()
                    || categoryField.getValue() == null
                    || dateField.getValue() == null) {

                showAlert(Alert.AlertType.WARNING,
                        "Please fill all required fields.");

                event.consume();
                return;
            }

            try {

                double amount = Double.parseDouble(
                        amountField.getText().trim());

                if (!Double.isFinite(amount) || amount <= 0) {

                    showAlert(Alert.AlertType.ERROR,
                            "Amount must be a positive number.");

                    event.consume();
                }

            } catch (NumberFormatException ex) {

                showAlert(Alert.AlertType.ERROR,
                        "Enter a valid numeric amount.");

                event.consume();
            }
        });

        dialog.setResultConverter(button -> {

            if (button == saveButtonType) {

                return new Expense(
                        nameField.getText().trim(),
                        Double.parseDouble(
                                amountField.getText().trim()),
                        categoryField.getValue(),
                        dateField.getValue(),
                        notesField.getText(),
                        recurringField.isSelected()
                );
            }

            return null;
        });

        dialog.showAndWait().ifPresent(updated -> {

            int index = allExpenses.indexOf(selected);

            if (index >= 0) {

                allExpenses.set(index, updated);

                refreshTable();

                showAlert(Alert.AlertType.INFORMATION,
                        "Expense updated successfully.");
            }
        });
    }

    // Delete selected expense
    private void handleDeleteExpense() {

        Expense selected = expensesTable
                .getSelectionModel().getSelectedItem();

        if (selected == null) {

            showAlert(Alert.AlertType.WARNING,
                    "Select an expense to delete.");
            return;
        }

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Delete expense '" + selected.getName() + "'?",
                ButtonType.YES,
                ButtonType.NO
        );

        confirmation.setHeaderText(null);

        if (confirmation.showAndWait()
                .orElse(ButtonType.NO) == ButtonType.YES) {

            allExpenses.remove(selected);

            refreshTable();

            showAlert(Alert.AlertType.INFORMATION,
                    "Expense deleted successfully.");
        }
    }

    // Temporary navigation until Member 5 is ready
    private void handleAddExpense(ActionEvent event) {

        showAlert(Alert.AlertType.INFORMATION,
                "Add Expense page is not connected yet.");
    }

    // Reusable alert method
    private void showAlert(Alert.AlertType type,
                           String message) {

        Alert alert = new Alert(type, message);

        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
    
    

