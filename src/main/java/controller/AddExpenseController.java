
package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import model.Expense;
import model.Category;

import service.ExpenseStorageService;
import service.FileStorageService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AddExpenseController {

    // FXML controls
    @FXML private TextField nameField;
    @FXML private TextField amountField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextArea notesArea;
    @FXML private CheckBox recurringCheckBox;
    @FXML private Label statusLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    // Initialize the screen when FXML is loaded.
    @FXML
    private void initialize() {

        datePicker.setValue(LocalDate.now());

        loadCategories();

        // Register button events using lambda expressions.
        saveButton.setOnAction(e -> handleSaveExpense());
        cancelButton.setOnAction(e -> handleCancel());

        // Clear the status message when input changes.
        nameField.textProperty().addListener(
                (obs, oldValue, newValue) ->
                        statusLabel.setText("")
        );

        amountField.textProperty().addListener(
                (obs, oldValue, newValue) ->
                        statusLabel.setText("")
        );
    }

    // Load categories from shared storage.
    private void loadCategories() {

        try {
            List<Category> categories =
                    FileStorageService.loadList(
                            "categories.dat", Category.class);

            // Extract and sort category names using Stream API.
            List<String> categoryNames = categories.stream()
                    .map(Category::getName)
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .collect(Collectors.toList());

            categoryComboBox.getItems().setAll(categoryNames);

            if (categoryNames.isEmpty()) {
                statusLabel.setText(
                        "Please add a category first.");
            }

        } catch (IOException | ClassNotFoundException e) {

            showAlert(Alert.AlertType.ERROR,
                    "Unable to load categories.");
        }
    }

    // Validate and save a new expense.
    private void handleSaveExpense() {

        String name = nameField.getText().trim();
        String amountText = amountField.getText().trim();
        String category = categoryComboBox.getValue();
        LocalDate date = datePicker.getValue();
        String notes = notesArea.getText().trim();
        boolean recurring = recurringCheckBox.isSelected();

        // Validate required fields.
        if (name.isEmpty() || amountText.isEmpty()
                || category == null || date == null) {

            showAlert(Alert.AlertType.WARNING,
                    "Please fill in all required fields.");
            return;
        }

        double amount;

        // Validate the expense amount.
        try {
            amount = Double.parseDouble(amountText);

            if (!Double.isFinite(amount) || amount <= 0) {
                showAlert(Alert.AlertType.WARNING,
                        "Amount must be a positive number.");
                return;
            }

        } catch (NumberFormatException e) {

            showAlert(Alert.AlertType.ERROR,
                    "Please enter a valid numeric amount.");
            return;
        }

        // Create a new expense object.
        Expense expense = new Expense(
                name, amount, category,
                date, notes, recurring
        );

        try {
            // Load existing expenses before adding a new one.
            List<Expense> expenses =
                    ExpenseStorageService.loadExpenses();

            expenses.add(expense);

            // Save the updated expense list.
            ExpenseStorageService.saveExpenses(expenses);

            clearFields();

            statusLabel.setText(
                    "Expense saved successfully.");

            showAlert(Alert.AlertType.INFORMATION,
                    "Expense added successfully!");

        } catch (IOException | IllegalStateException e) {

            showAlert(Alert.AlertType.ERROR,
                    "Unable to save expense.");
        }
    }

    // Cancel the current input.
    private void handleCancel() {

        clearFields();
        statusLabel.setText("Entry cancelled.");
    }

    // Reset all form fields.
    private void clearFields() {

        nameField.clear();
        amountField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
        datePicker.setValue(LocalDate.now());
        notesArea.clear();
        recurringCheckBox.setSelected(false);
    }

    // Display feedback to the user.
    private void showAlert(Alert.AlertType type,
                           String message) {

        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
