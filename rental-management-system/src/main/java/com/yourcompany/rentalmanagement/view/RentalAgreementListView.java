package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.controller.RentalAgreementController;
import com.yourcompany.rentalmanagement.dao.impl.RentalAgreementDaoImpl;
import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class RentalAgreementListView implements Initializable {
    RentalAgreementController rentalAgreementController = new RentalAgreementController();
    ObservableList<RentalAgreement> rentalAgreements = FXCollections.observableArrayList();

    @FXML
    TableView<RentalAgreement> rentalAgreementTableView = new TableView<>();

    @FXML
    TableColumn<RentalAgreement, String> agreementId;

    @FXML
    TableColumn<RentalAgreement, RentalAgreement.rentalAgreementStatus> status;

    @FXML
    TableColumn<RentalAgreement, LocalDate> contractedDate;

    @FXML
    TableColumn<RentalAgreement, Double> rentingFee;

    @Override
    public void initialize(URL url, ResourceBundle bundle){
//        loadRentalAgreement();
//        initializeColumn();
//        rentalAgreementTableView.setItems(rentalAgreements);
    }

    public void initializeColumn(){
        agreementId.setCellValueFactory(new PropertyValueFactory<>("id"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        contractedDate.setCellValueFactory(new PropertyValueFactory<>("contractDate"));
        rentingFee.setCellValueFactory(new PropertyValueFactory<>("rentingFee"));
    }

    private void loadRentalAgreement() {
        new Thread(() -> {
            List<RentalAgreement> rentalAgreementsList = rentalAgreementController.getAllRentalAgreements();
            Platform.runLater(() -> {
                rentalAgreements.addAll(rentalAgreementsList);
                rentalAgreementTableView.setItems(rentalAgreements);
            });
        }).start();
    }

}