package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.controller.LoginController;
import com.yourcompany.rentalmanagement.model.UserRole;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
        if (signupUsername.getText().isEmpty()
                || signupPassword.getText().isEmpty()
                || signupEmail.getText().isEmpty()) {
            showErrorMessage("Please fill in all fields");
            return;
        }

        if (roleComboBox.getValue() == null) {
            showErrorMessage("Please select a role");
            return;
        }

        String username = signupUsername.getText();
        String password = signupPassword.getText();
        String email = signupEmail.getText();
        UserRole role = roleComboBox.getValue();

        controller.handleSignup(username, password, email, role);
    }

    @FXML
    private void handleVisitor() {
        controller.handleVisitor();
    }

    public void navigateToMainView() {
        // Will implement navigation later
        System.out.println("Navigation will be implemented later...");
    }

    public void showErrorMessage(String message) {
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
