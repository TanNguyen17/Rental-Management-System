package com.yourcompany.rentalmanagement.view;
/**
 * @author FTech
 */
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.yourcompany.rentalmanagement.model.User;
import com.yourcompany.rentalmanagement.model.UserRole;
import com.yourcompany.rentalmanagement.util.UserSession;
import com.yourcompany.rentalmanagement.view.components.Toast;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SideMenuView implements Initializable {

    private UserSession userSession = UserSession.getInstance();
    private OwnerView ownerView = new OwnerView();
    private BorderPane borderPane;

    public void setBorderPane(BorderPane borderPane) {
        this.borderPane = borderPane;
        System.out.println("Success");
    }

    @FXML
    private VBox navBar;

    @FXML
    private Button logOut;

    @FXML
    private Text roleText;

    @FXML
    private Text usernameText;

    @FXML
    private Button exit;

    @FXML
    private HBox userInfoBox;

    @FXML
    private Text userInfoText;

    private boolean showingUsername = false;
    private User currentUser;

    private Map<UserRole, Map<String, String>> menuConfig = new HashMap<>();

    private Button activeButton = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureMenu();
        populateMenu();
        setupUserInfo();
    }

    private void configureMenu() {
        menuConfig.put(UserRole.OWNER, new LinkedHashMap<>(Map.of(
                "Profile", "/fxml/ProfileView.fxml",
                "Home", "/fxml/ViewRentalProperties.fxml",
                "Rental Agreements", "/fxml/RentalAgreementListView.fxml"
        )));
        menuConfig.put(UserRole.TENANT, new LinkedHashMap<>(Map.of(
                "Profile", "/fxml/ProfileView.fxml",
                "Home", "/fxml/PropertiesView.fxml",
                "Rental Agreements", "/fxml/RentalAgreementListView.fxml",
                "Payments", "/fxml/PaymentsView.fxml"
        )));
        menuConfig.put(UserRole.HOST, new LinkedHashMap<>(Map.of(
                "Dashboard", "/fxml/HostDashboardView.fxml",
                "Property", "/fxml/ViewRentalProperties.fxml",
                "Rental Agreements", "/fxml/RentalAgreementListView.fxml",
                "Payments", "/fxml/PaymentsView.fxml",
                "Profile", "/fxml/ProfileView.fxml"
        )));
        menuConfig.put(UserRole.MANAGER, new LinkedHashMap<>(Map.of(
                "Profile", "/fxml/ProfileView.fxml",
                "Dashboard", "/fxml/manager/ManagerDashBoard.fxml",
                "Statistical Report", "/fxml/StatisticalReport.fxml",
                "Property", "/fxml/ViewRentalProperties.fxml"
        )));
    }

    private void populateMenu() {
        if (userSession.getCurrentUser() == null) {
            return;
        }

        UserRole role = userSession.getCurrentUser().getRole();
        Map<String, String> roleMenu = menuConfig.getOrDefault(role, new LinkedHashMap<>());

        navBar.getChildren().clear();

        List<String> buttonOrder = new ArrayList<>();

        if (role.equals(UserRole.OWNER)) {
            buttonOrder.addAll(Arrays.asList("Home", "Rental Agreements", "Profile"));
        } else if (role.equals(UserRole.TENANT)) {
            buttonOrder.addAll(Arrays.asList("Home", "Rental Agreements", "Payments", "Profile"));
        } else if (role.equals(UserRole.HOST)) {
            buttonOrder.addAll(Arrays.asList("Dashboard", "Property", "Rental Agreements", "Payments", "Profile"));
        } else if (role.equals(UserRole.MANAGER)) {
            buttonOrder.addAll(Arrays.asList("Dashboard", "Statistical Report", "Property", "Profile"));
        }

        for (String buttonName : buttonOrder) {
            if (roleMenu.containsKey(buttonName)) {
                String fxmlPath = roleMenu.get(buttonName);
                Button button = new Button(buttonName);
                button.getStyleClass().add("menu-button");
                button.setPrefWidth(180);
                button.setPrefHeight(45);

                button.setOnAction(event -> {
                    loadView(fxmlPath);
                    setActiveButton(button);
                });

                navBar.getChildren().add(button);

                if (buttonName.equals("Home") || buttonName.equals("Dashboard")) {
                    setActiveButton(button);
                }
            }
        }
        navBar.getChildren().add(logOut);
    }

    private void setupUserInfo() {
        currentUser = userSession.getCurrentUser();
        if (currentUser != null) {
            userInfoText.setText(currentUser.getRole().toString());

            // Add hover effect
            userInfoBox.setOnMouseEntered(e -> userInfoBox.setStyle("-fx-cursor: hand;"));
            userInfoBox.setOnMouseExited(e -> userInfoBox.setStyle("-fx-cursor: default;"));
        }
    }

    @FXML
    private void handleUserInfoClick() {
        if (currentUser != null) {
            if (!showingUsername) {
                userInfoText.setText(currentUser.getUsername());
                showingUsername = true;
            } else {
                userInfoText.setText(currentUser.getRole().toString());
                showingUsername = false;
            }
        }
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            javafx.scene.Node view = loader.load();
            if (this.borderPane != null) {
                this.borderPane.setCenter(view);
            } else {
                System.err.println("BorderPane is null. Make sure setBorderPane is called.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading view: " + fxmlPath);
        }
    }

    @FXML
    private void handleLogout() throws IOException {
        userSession.clearSession();

        Stage stage = (Stage) logOut.getScene().getWindow();
        Toast.showSuccess(stage, "Successfully logged out");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
        Scene scene = new Scene(loader.load());

        scene.getStylesheets().addAll(
                getClass().getResource("/css/login.css").toExternalForm(),
                getClass().getResource("/css/components/toast.css").toExternalForm()
        );

        stage.setMaximized(false);
        stage.setWidth(1280);
        stage.setHeight(720);
        stage.setMinWidth(1024);
        stage.setMinHeight(768);

        stage.setScene(scene);
        stage.setTitle("Rental Management System - Login");
        stage.centerOnScreen();
        stage.show();

        Platform.runLater(() -> {
            stage.setMaximized(true);
        });
    }

    @FXML
    private void handleExit() {
        Stage stage = (Stage) exit.getScene().getWindow();
        Toast.showSuccess(stage, "Goodbye!");

        // small delay before exit to show the toast
        PauseTransition delay = new PauseTransition(Duration.seconds(1));
        delay.setOnFinished(e -> Platform.exit());
        delay.play();
    }

    private void setActiveButton(Button button) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("active");
        }
        activeButton = button;
        activeButton.getStyleClass().add("active");
    }
}
