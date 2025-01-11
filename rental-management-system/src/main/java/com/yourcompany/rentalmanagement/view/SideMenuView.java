package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.model.UserRole;
import com.yourcompany.rentalmanagement.util.UserSession;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

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
    public void initialize(URL url, ResourceBundle resourceBundle) {;
        configureMenu();
        populateMenu();
    }

    private void configureMenu() {
        menuConfig.put(UserRole.OWNER, Map.of(
                "Home", "/fxml/ViewRentalProperties.fxml",
                "Rental Agreements", "/fxml/RentalAgreementListView.fxml",
                "Profile", "/fxml/ProfileView.fxml"
        ));
        menuConfig.put(UserRole.TENANT, Map.of(
                "Home", "/fxml/PropertiesView.fxml",
                "Rental Agreements", "/fxml/RentalAgreementListView.fxml",
                "Payments", "/fxml/PaymentView.fxml",
                "Profile", "/fxml/ProfileView.fxml"
        ));
        menuConfig.put(UserRole.HOST, Map.of(
                "Rental Agreements", "/fxml/RentalAgreementListView.fxml",
                "Payments", "/fxml/PaymentView.fxml",
                "Profile", "/fxml/ProfileView.fxml"
        ));
        menuConfig.put(UserRole.MANAGER, Map.of(
                "Dashboard", "/fxml/ManagerDashBoard.fxml",
                "Profile", "/fxml/ProfileView.fxml"
        ));
    }

    private void populateMenu() {
        if (userSession.getCurrentUser() == null) return;

        UserRole role = userSession.getCurrentUser().getRole();
        Map<String, String> roleMenu = menuConfig.getOrDefault(role, new HashMap<>());

        navBar.getChildren().clear(); // Clear existing buttons
        for (Map.Entry<String, String> entry : roleMenu.entrySet()) {
            Button button = new Button(entry.getKey());
            button.setPrefWidth(180);
            button.setPrefHeight(45);
            button.setOnAction(event -> loadView(entry.getValue()));
            navBar.getChildren().add(button);
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
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(fxmlPath));
            this.borderPane.setCenter(anchorPane);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void logout(ActionEvent event) throws IOException {
        userSession.clearSession();
        Platform.exit();
    }
}