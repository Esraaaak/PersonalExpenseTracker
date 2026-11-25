package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import model.Category;
import service.ExpenseStorageService;
import service.FileStorageService;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class CategoryController {

    @FXML private TextField nameField;
    @FXML private TextField descriptionField;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private ListView<Category> categoryListView;
    @FXML private Label statusLabel;

    private final ObservableList<Category> categories =
            FXCollections.observableArrayList();

    private final Predicate<String> notBlank =
            value -> value != null && !value.trim().isEmpty();

    private final Predicate<String> duplicateName = name ->
            categories.stream()
                    .anyMatch(category ->
                            category.getName().equalsIgnoreCase(name));

    @FXML
    private void initialize() {
        loadCategories();

        categoryListView.setItems(categories);

        categoryListView.setCellFactory(list -> new ListCell<Category>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);

                if (empty || category == null) {
                    setText(null);
                } else {
                    String description = category.getDescription();

                    if (description == null || description.trim().isEmpty()) {
                        setText(category.getName());
                    } else {
                        setText(category.getName() + " - " + description);
                    }
                }
            }
        });

        addButton.setOnAction(e -> handleAddCategory());
        updateButton.setOnAction(e -> handleUpdateCategory());
        deleteButton.setOnAction(e -> handleDeleteCategory());

        nameField.textProperty().addListener(
                (obs, oldValue, newValue) -> updateButtons()
        );

        categoryListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldValue, selected) -> {
                    if (selected != null) {
                        nameField.setText(selected.getName());
                        descriptionField.setText(selected.getDescription());
                    }
                    updateButtons();
                });

        categoryListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2
                    && categoryListView.getSelectionModel()
                    .getSelectedItem() != null) {

                nameField.requestFocus();
                nameField.selectAll();
            }
        });

        nameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleAddCategory();
            }
        });

        updateButtons();
    }

    private void handleAddCategory() {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();

        if (!notBlank.test(name)) {
            showAlert(Alert.AlertType.WARNING,
                    "Category name is required.");
            return;
        }

        if (duplicateName.test(name)) {
            showAlert(Alert.AlertType.ERROR,
                    "Category already exists.");
            return;
        }

        categories.add(new Category(name, description));
        sortCategories();

        saveCategories();
        clearFields();

        statusLabel.setText("Category added: " + name);
    }

    private void handleUpdateCategory() {
        Category selected =
                categoryListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING,
                    "Select a category to update.");
            return;
        }

        String newName = nameField.getText().trim();
        String newDescription = descriptionField.getText().trim();

        if (!notBlank.test(newName)) {
            showAlert(Alert.AlertType.WARNING,
                    "Category name is required.");
            return;
        }

        boolean renaming =
                !selected.getName().equalsIgnoreCase(newName);

        if (renaming && duplicateName.test(newName)) {
            showAlert(Alert.AlertType.ERROR,
                    "Category already exists.");
            return;
        }

        if (renaming && usedByExpenses(selected.getName())) {
            showAlert(Alert.AlertType.ERROR,
                    "Cannot rename this category because expenses use it.");
            return;
        }

        selected.setName(newName);
        selected.setDescription(newDescription);

        sortCategories();
        saveCategories();

        statusLabel.setText("Category updated.");
    }

    private void handleDeleteCategory() {
        Category selected =
                categoryListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING,
                    "Select a category to delete.");
            return;
        }

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Delete category '" + selected.getName() + "'?",
                ButtonType.YES,
                ButtonType.NO
        );
        confirmation.setHeaderText(null);

        if (confirmation.showAndWait().orElse(ButtonType.NO)
                != ButtonType.YES) {
            return;
        }

        if (usedByExpenses(selected.getName())) {
            showAlert(Alert.AlertType.ERROR,
                    "Cannot delete this category because expenses use it.");
            return;
        }

        categories.remove(selected);
        saveCategories();
        clearFields();

        statusLabel.setText("Category deleted.");
    }
    private boolean usedByExpenses(String categoryName) {
        try {
            return ExpenseStorageService.loadExpenses().stream()
                    .anyMatch(expense -> categoryName.equalsIgnoreCase(
                            expense.getCategory()));
        } catch (IllegalStateException e) {
            showAlert(Alert.AlertType.ERROR,
                    "Unable to check expenses.");
            return true;
        }
    }

    private void updateButtons() {
        boolean hasSelection =
                categoryListView.getSelectionModel().getSelectedItem() != null;

        boolean hasName = notBlank.test(nameField.getText());

        updateButton.setDisable(!hasSelection || !hasName);
        deleteButton.setDisable(!hasSelection);
    }

    private void clearFields() {
        nameField.clear();
        descriptionField.clear();
        categoryListView.getSelectionModel().clearSelection();
        updateButtons();
    }

    private void sortCategories() {
        categories.sort(Comparator.comparing(
                Category::getName,
                String.CASE_INSENSITIVE_ORDER
        ));
    }

    private void loadCategories() {
        try {
            List<Category> loaded =
                    FileStorageService.loadList(
                            "categories.dat", Category.class);
            categories.setAll(loaded);
            statusLabel.setText("");
        } catch (IOException | ClassNotFoundException e) {
            statusLabel.setText("Unable to load categories.");
        }
    }

    private void saveCategories() {
        try {
            FileStorageService.saveList("categories.dat", categories);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR,
                    "Unable to save categories.");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}