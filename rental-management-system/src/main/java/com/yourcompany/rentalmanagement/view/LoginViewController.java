package com.yourcompany.rentalmanagement.view;

import java.io.IOException;

import com.yourcompany.rentalmanagement.controller.LoginController;
import com.yourcompany.rentalmanagement.model.Host;
import com.yourcompany.rentalmanagement.model.Manager;
import com.yourcompany.rentalmanagement.model.Owner;
import com.yourcompany.rentalmanagement.model.Tenant;
import com.yourcompany.rentalmanagement.model.User;
import com.yourcompany.rentalmanagement.model.UserRole;
import com.yourcompany.rentalmanagement.util.UserSession;
import com.yourcompany.rentalmanagement.view.components.Toast;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

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
    @FXML
    private TabPane tabPane;

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
            Stage stage = (Stage) messageLabel.getScene().getWindow();
            Toast.showError(stage, "Please fill in all fields");
            return;
        }

        String username = loginUsername.getText();
        String password = loginPassword.getText();
        controller.handleLogin(username, password);
    }

    @FXML
    private void handleSignup() {
        messageLabel.setText("");

        if (signupUsername.getText().length() < 3) {
            showErrorMessage("Username must be at least 3 characters");
            return;
        }

        if (signupPassword.getText().length() < 6) {
            showErrorMessage("Password must be at least 6 characters");
            return;
        }

        if (!signupEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showErrorMessage("Please enter a valid email address");
            return;
        }

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

    @FXML
    private void handleForgotPassword() {
        Stage stage = (Stage) messageLabel.getScene().getWindow();
        Toast.showError(stage, "Sorry, you're on your own with this one! 😅");
    }

    @FXML
    private void handleSignupClick() {
        tabPane.getSelectionModel().select(1); // Switch to signup tab (index 1)
    }

    public void navigateToMainView() {
        try {
            User currentUser = UserSession.getInstance().getCurrentUser();
            FXMLLoader loader = null;

            if (currentUser instanceof Tenant) {
                loader = new FXMLLoader(getClass().getResource("/fxml/TenantView.fxml"));
            } else if (currentUser instanceof Owner) {
                loader = new FXMLLoader(getClass().getResource("/fxml/OwnerView.fxml"));
            } else if (currentUser instanceof Manager) {
                loader = new FXMLLoader(getClass().getResource("/fxml/ManagerView.fxml"));
            } else if (currentUser instanceof Host) {
                loader = new FXMLLoader(getClass().getResource("/fxml/HostView.fxml"));
            }

            if (loader != null) {
                Stage stage = (Stage) messageLabel.getScene().getWindow();
                Scene scene = new Scene(loader.load());

                // Add stylesheets
                scene.getStylesheets().addAll(
                        getClass().getResource("/css/property-list.css").toExternalForm(),
                        getClass().getResource("/css/side-menu.css").toExternalForm(),
                        getClass().getResource("/css/property-form.css").toExternalForm(),
                        getClass().getResource("/css/components/loading-spinner.css").toExternalForm(),
                        getClass().getResource("/css/components/toast.css").toExternalForm()
                );

                stage.setMaximized(false);
                stage.setWidth(1280);
                stage.setHeight(720);

                stage.setMinWidth(1024);
                stage.setMinHeight(768);

                stage.setScene(scene);
                stage.setTitle("Rental Management System - " + currentUser.getRole());

                stage.centerOnScreen();

                stage.show();

                Platform.runLater(() -> {
                    stage.setMaximized(true);
                });
            }
        } catch (IOException e) {
            showErrorMessage("Error loading view: " + e.getMessage());
        }
    }

    public void showSuccessMessage(String message) {
        Stage stage = (Stage) messageLabel.getScene().getWindow();
        Toast.showSuccess(stage, message);
    }

    public void showErrorMessage(String message) {
        Stage stage = (Stage) messageLabel.getScene().getWindow();
        Toast.showError(stage, message);
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
