package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.controller.LoginController;
import com.yourcompany.rentalmanagement.model.UserRole;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class LoginViewController {

    @FXML
    private TextField loginUsername;
    @FXML
    private PasswordField loginPassword;
    @FXML
    private TextField signupUsername;
    @FXML
    private PasswordField signupPassword;
    @FXML
    private TextField signupEmail;
    @FXML
    private ComboBox<UserRole> roleComboBox;
    @FXML
    private Label messageLabel;

    private LoginController controller;

    @FXML
    public void initialize() {
        controller = new LoginController(this);
        roleComboBox.getItems().addAll(UserRole.TENANT, UserRole.HOST, UserRole.OWNER, UserRole.MANAGER);

        // "Enter" --> Login
        loginUsername.setOnKeyPressed(e -> handleLoginKeyPress(e));
        loginPassword.setOnKeyPressed(e -> handleLoginKeyPress(e));

        // "Enter" --> Signup
        signupUsername.setOnKeyPressed(e -> handleSignupKeyPress(e));
        signupPassword.setOnKeyPressed(e -> handleSignupKeyPress(e));
        signupEmail.setOnKeyPressed(e -> handleSignupKeyPress(e));
        roleComboBox.setOnKeyPressed(e -> handleSignupKeyPress(e));
    }

    @FXML
    public void handleLoginKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (event.getSource() == loginUsername) {
                loginPassword.requestFocus();
            } else if (event.getSource() == loginPassword) {
                handleLogin();
            }
        }
    }

    @FXML
    public void handleSignupKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (event.getSource() == signupUsername) {
                signupPassword.requestFocus();
            } else if (event.getSource() == signupPassword) {
                signupEmail.requestFocus();
            } else if (event.getSource() == signupEmail) {
                roleComboBox.requestFocus();
            } else if (event.getSource() == roleComboBox && roleComboBox.getValue() != null) {
                handleSignup();
            }
        }
    }

    @FXML
    private void handleLogin() {
        if (loginUsername.getText().isEmpty() || loginPassword.getText().isEmpty()) {
            showErrorMessage("Please fill in all fields");
            return;
        }

        String username = loginUsername.getText();
        String password = loginPassword.getText();
        controller.handleLogin(username, password);
    }

    @FXML
    private void handleSignup() {
        // Clear previous error messages
        messageLabel.setText("");

        // Validate username
        if (signupUsername.getText().length() < 3) {
            showErrorMessage("Username must be at least 3 characters");
            return;
        }

        // Validate password
        if (signupPassword.getText().length() < 6) {
            showErrorMessage("Password must be at least 6 characters");
            return;
        }

        // Validate email
        if (!signupEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showErrorMessage("Please enter a valid email address");
            return;
        }

        // Validate role selection
        if (roleComboBox.getValue() == null) {
            showErrorMessage("Please select a role");
            return;
        }

        try {
            String username = signupUsername.getText();
            String password = signupPassword.getText();
            String email = signupEmail.getText();
            UserRole role = roleComboBox.getValue();

            System.out.println("Attempting signup with username: " + username + ", role: " + role);
            controller.handleSignup(username, password, email, role);
        } catch (Exception e) {
            showErrorMessage("Registration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVisitor() {
        controller.handleVisitor();
    }

    public void navigateToMainView() {
        // Will implement navigation later
        showSuccessMessage("Login successful! Navigation will be implemented later...");
    }

    public void showSuccessMessage(String message) {
        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText(message);
    }

    public void showErrorMessage(String message) {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText(message);
    }

    public void clearFields() {
        loginUsername.clear();
        loginPassword.clear();
        signupUsername.clear();
        signupPassword.clear();
        signupEmail.clear();
        roleComboBox.setValue(null);
        messageLabel.setText("");
    }
}
