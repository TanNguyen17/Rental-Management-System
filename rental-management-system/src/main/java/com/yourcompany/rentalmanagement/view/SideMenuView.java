package com.yourcompany.rentalmanagement.view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.yourcompany.rentalmanagement.model.UserRole;
import com.yourcompany.rentalmanagement.util.UserSession;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

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
    private ComboBox<String> roleComboBox = new ComboBox<>();

    private Map<UserRole, Map<String, String>> menuConfig = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureMenu();
        populateMenu();

        // Show initial view based on user role (No need)
//        Platform.runLater(() -> {
//            UserRole role = userSession.getCurrentUser().getRole();
//            String initialView = switch (role) {
//                case OWNER, TENANT ->
//                    "/fxml/ViewRentalProperties.fxml";
//                case HOST ->
//                    "/fxml/RentalAgreementListView.fxml";
//                case MANAGER ->
//                    "/fxml/ManagerDashBoard.fxml";
//                default ->
//                    throw new IllegalStateException("Unexpected role: " + role);
//            };
//            loadView(initialView);
//        });
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
                "Profile", "/fxml/ProfileView.fxml",
                "Rental Agreements", "/fxml/RentalAgreementListView.fxml",
                "Payments", "/fxml/PaymentsView.fxml"
        )));
        menuConfig.put(UserRole.MANAGER, new LinkedHashMap<>(Map.of(
                "Profile", "/fxml/ProfileView.fxml",
                "Dashboard", "/fxml/manager/ManagerDashBoard.fxml",
                "Statistical Report", "/fxml/manager/ManagerDashBoard.fxml"
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
            buttonOrder.addAll(Arrays.asList("Rental Agreements", "Payments", "Profile"));
        } else if (role.equals(UserRole.MANAGER)) {
            buttonOrder.addAll(Arrays.asList("Dashboard", "Profile"));
        }

        for (String buttonName : buttonOrder) {
            if (roleMenu.containsKey(buttonName)) {
                String fxmlPath = roleMenu.get(buttonName);
                Button button = new Button(buttonName);
                button.getStyleClass().add("menu-button");
                button.setPrefWidth(180);
                button.setPrefHeight(45);
                button.setOnAction(event -> loadView(fxmlPath));
                navBar.getChildren().add(button);
            }
        }
        navBar.getChildren().add(logOut);
    }

//    private void setButtionActions() {
//        if (userSession.getCurrentUser().getRole().equals(UserRole.OWNER)) {
//            roleComboBox.setValue("OWNER");
//            report.setVisible(false);
//            payment.setVisible(false);
//            home.setOnMouseClicked(event -> loadView("/fxml/ViewRentalProperties.fxml"));
//            rentalAgreement.setOnMouseClicked(event -> loadView("/fxml/RentalAgreementListView.fxml"));
//            profile.setOnMouseClicked(event -> loadView("/fxml/ProfileView.fxml"));
//        }
//        if (userSession.getCurrentUser().getRole().equals(UserRole.TENANT)) {
//            roleComboBox.setValue("TENANT");
//            home.setOnMouseClicked(event -> loadView("/fxml/PropertiesView.fxml"));
//            rentalAgreement.setOnMouseClicked(event -> loadView("/fxml/RentalAgreementListView.fxml"));
//            payment.setOnMouseClicked(event -> loadView("/fxml/PaymentView.fxml"));
//            profile.setOnMouseClicked(event -> loadView("/fxml/ProfileView.fxml"));
//        }
//        if (userSession.getCurrentUser().getRole().equals(UserRole.HOST)) {
//            rentalAgreement.setOnMouseClicked(event -> loadView("/fxml/RentalAgreementListView.fxml"));
//            payment.setOnMouseClicked(event -> loadView("/fxml/PaymentView.fxml"));
//            profile.setOnMouseClicked(event -> loadView("/fxml/ProfileView.fxml"));
//        }
//        if (userSession.getCurrentUser().getRole().equals(UserRole.MANAGER)) {
//            home.setOnMouseClicked(event -> loadView("/fxml/ManagerDashBoard.fxml"));
//            profile.setOnMouseClicked(event -> loadView("/fxml/ProfileView.fxml"));
//        }
//    }
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
    void logout(ActionEvent event) throws IOException {
        userSession.clearSession();
        Platform.exit();
    }
}