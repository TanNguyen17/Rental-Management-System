package com.yourcompany.rentalmanagement.controller;

import com.yourcompany.rentalmanagement.dao.RentalAgreementDaoImp;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import java.time.LocalDate;

public class RentalAgreementFormController {
    private RentalAgreementDaoImp rentalAgreementDaoImp = new RentalAgreementDaoImp();

    @FXML
    private MFXTextField tenantNameField;

    @FXML
    private MFXTextField ownerNameField;

    @FXML
    private MFXTextField propertyNameField;

    @FXML
    private MFXTextField locationNameField;

    @FXML
    private MFXTextField hostNameField;

    @FXML
    private MFXTextField rentingFeeField;

    @FXML
    private MFXTextField dateField;

    @FXML
    private MFXComboBox<String> statusField;

    @FXML
    private MFXTextField imageField;

    @FXML
    private MFXButton submitButton;

    @FXML
    public void initialize() {
        // Populate the status ComboBox
        statusField.getItems().addAll("NEW", "ACTIVE", "COMPLETED");
    }

    @FXML
    private void handleSubmitButtonAction() {
        try {
            // Retrieve data from the form
            String tenantName = tenantNameField.getText();
            String ownerName = ownerNameField.getText();
            String propertyName = propertyNameField.getText();
            String locationName = locationNameField.getText();
            String hostName = hostNameField.getText();
            double rentingFee = Double.parseDouble(rentingFeeField.getText());
            LocalDate contractDate = LocalDate.parse(dateField.getText());
            String status = statusField.getValue();

            RentalAgreement rentalAgreement = new RentalAgreement();
            rentalAgreement.setRentingFee(rentingFee);
            rentalAgreement.setContractDate(contractDate);
            rentalAgreement.setStatus(RentalAgreement.rentalAgreementStatus.valueOf(status));

            // Set relationships (you may need to fetch these from the database in real scenarios)
            rentalAgreement.setOwner(null);
            rentalAgreement.setHost(null);
            rentalAgreement.setTenants(null);

            System.out.println(rentalAgreement);
            // Save to the database
            //rentalAgreementDaoImp.save(rentalAgreement);

            // Show success alert
            showAlert("Success", "Rental Agreement saved successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save the Rental Agreement. Check your input.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
