package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import model.User;
import service.FileStorageService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Button exitButton;
    @FXML private Label messageLabel;

    public static User loggedInUser;

    private List<User> users = new ArrayList<>();

    private final Predicate<String> notBlank =
            value -> value != null && !value.trim().isEmpty();

    @FXML
    private void initialize() {
        loadUsers();

        if (users.isEmpty()) {
            users.add(new User("admin", "admin123"));
            saveUsers();
        }

        usernameField.textProperty().addListener(
                (obs, oldValue, newValue) -> updateLoginButton()
        );

        passwordField.textProperty().addListener(
                (obs, oldValue, newValue) -> updateLoginButton()
        );

        loginButton.setOnAction(e -> handleLogin(e));
        registerButton.setOnAction(e -> handleRegister());
        exitButton.setOnAction(e -> handleExit(e));

passwordField.setOnKeyPressed(e -> {
    if (e.getCode() == KeyCode.ENTER) {
        handleLogin(new ActionEvent(passwordField, passwordField));
    }
});

        updateLoginButton();
    }

    private void updateLoginButton() {
        boolean ready = notBlank.test(usernameField.getText())
                && notBlank.test(passwordField.getText());

        loginButton.setDisable(!ready);
    }

    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (!notBlank.test(username) || !notBlank.test(password)) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Enter both username and password."
            );
            return;
        }

        Optional<User> matchedUser = users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username))
                .filter(user -> user.getPassword().equals(password))
                .findFirst();

        if (!matchedUser.isPresent()) {
            messageLabel.setText("Invalid username or password.");
            showAlert(
                    Alert.AlertType.ERROR,
                    "Invalid username or password."
            );
            return;
        }

        loggedInUser = matchedUser.get();
        messageLabel.setText("Welcome, " + username + "!");

        navigateTo(
                event,
                "/view/Dashboard.fxml",
                "Dashboard"
        );
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (!notBlank.test(username) || !notBlank.test(password)) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Enter both username and password."
            );
            return;
        }

        if (password.length() < 4) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Password must be at least 4 characters."
            );
            return;
        }

        boolean duplicate = users.stream()
                .anyMatch(user ->
                        user.getUsername().equalsIgnoreCase(username));

        if (duplicate) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Username already exists."
            );
            return;
        }

        users.add(new User(username, password));
        saveUsers();

        messageLabel.setText("Account created. You can login now.");
        showAlert(
                Alert.AlertType.INFORMATION,
                "Account created successfully."
        );

        usernameField.clear();
        passwordField.clear();
        updateLoginButton();
    }

    private void handleExit(ActionEvent event) {
        Platform.exit();
    }

    public void handleLogout(ActionEvent event) {
        loggedInUser = null;

        navigateTo(
                event,
                "/view/Login.fxml",
                "Login"
        );
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static boolean isLoggedIn() {
        return loggedInUser != null;
    }

    private void loadUsers() {
        try {
            users = FileStorageService.loadList("users.dat", User.class);
        } catch (IOException | ClassNotFoundException e) {
            users = new ArrayList<>();
            showAlert(Alert.AlertType.ERROR,
                    "Unable to load users.");
        }
    }

    private void saveUsers() {
        try {
            FileStorageService.saveList("users.dat", users);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR,
                    "Unable to save users.");
        }
    }

    private void navigateTo(
            ActionEvent event,
            String fxmlPath,
            String title
    ) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlPath)
            );

            Parent root = loader.load();

            Stage stage = (Stage)
                    ((Node) event.getSource())
                            .getScene()
                            .getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();

        } catch (Exception e) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Cannot open page: " + fxmlPath
            );
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}