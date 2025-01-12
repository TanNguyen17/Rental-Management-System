package com.yourcompany.rentalmanagement.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.yourcompany.rentalmanagement.util.UserSession;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class ManagerView implements Initializable {

    private UserSession userSession = UserSession.getInstance();

    @FXML
    private BorderPane managerBorderPane;

    @FXML
    private BorderPane borderPane;

    @FXML
    private Button homeTab;

    @FXML
    private Button paymentTab;

    @FXML
    private Button profileTab;

    @FXML
    private Button rentalAgreementTab;

    @FXML
    private Label user;

    @FXML
    void btnHome(ActionEvent event) throws IOException {
        AnchorPane homeView = FXMLLoader.load(getClass().getResource("/fxml/PropertiesView.fxml"));
        borderPane.setCenter(homeView);
    }

    @FXML
    void btnPayment(ActionEvent event) throws IOException {
        AnchorPane paymentsView = FXMLLoader.load(getClass().getResource("/fxml/PaymentsView.fxml"));
        borderPane.setCenter(paymentsView);
    }

    @FXML
    void btnProfile(ActionEvent event) throws IOException {
        AnchorPane profileView = FXMLLoader.load(getClass().getResource("/fxml/ProfileView.fxml"));
        borderPane.setCenter(profileView);
    }

    @FXML
    void btnRentalAgreement(ActionEvent event) throws IOException {
        TableView rentalAgreementView = FXMLLoader.load(getClass().getResource("/fxml/manager/rental-agreement-management/RentalAgreementView.fxml"));
        borderPane.setCenter(rentalAgreementView);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeSideMenu();
    }

    private void initializeSideMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SideMenu.fxml"));
            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/fxml/manager/ManagerDashBoard.fxml"));

            VBox sideMenu = loader.load();
            Node main = mainLoader.load();

            SideMenuView sideMenuView = loader.getController();
            managerBorderPane.setLeft(sideMenu);
            managerBorderPane.setCenter(main);
            sideMenuView.setBorderPane(managerBorderPane);
            System.out.println("BorderPane set in SideMenuView.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
