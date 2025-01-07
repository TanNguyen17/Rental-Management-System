package com.yourcompany.rentalmanagement.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TenantView implements Initializable {
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
    void btnHome(ActionEvent event) {

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
    void btnRentalAgreement(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
