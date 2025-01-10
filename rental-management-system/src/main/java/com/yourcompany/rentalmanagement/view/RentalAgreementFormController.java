package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.dao.impl.HostDaoImp;
import com.yourcompany.rentalmanagement.dao.impl.OwnerDaoImpl;
import com.yourcompany.rentalmanagement.dao.impl.RentalAgreementDaoImpl;
import com.yourcompany.rentalmanagement.dao.impl.TenantDaoImp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

public class RentalAgreementFormController {
    private RentalAgreementDaoImpl rentalAgreementDaoImpl = new RentalAgreementDaoImpl();
    private TenantDaoImp tenantDaoImp = new TenantDaoImp();
    private HostDaoImp hostDaoImp = new HostDaoImp();
    private OwnerDaoImpl ownerDaoImp = new OwnerDaoImpl();

    @FXML
    private ComboBox<String> tenantField;

    @FXML
    private ComboBox<String> ownerField;

    @FXML
    private ComboBox<String> hostField;

    @FXML
    private ComboBox<String> propertyTypeField;

    @FXML
    private ComboBox<String> propertyField;

    @FXML
    private TextField rentingFeeField;

    @FXML
    private DatePicker startDateField;

    @FXML
    private DatePicker endDateField;

    @FXML
    private ComboBox<String> statusField;

    @FXML
    private Button uploadButton;

    @FXML
    private ImageView imageView;

    @FXML
    private Button submitButton;

    @FXML
    public void initialize() {
        ObservableList<String> tenants = FXCollections.observableArrayList(tenantDaoImp.getAllUserName());
        tenantField.setItems(tenants);

        ObservableList<String> owners = FXCollections.observableArrayList(ownerDaoImp.getAllUserName());
        ownerField.setItems(owners);

        ObservableList<String> hosts = FXCollections.observableArrayList(hostDaoImp.getAllUserName());
        hostField.setItems(hosts);
        // Populate the status ComboBox

        statusField.getItems().addAll("NEW", "ACTIVE", "COMPLETED");
    }

    @FXML
    private void handleSubmitButtonAction() {
        try {
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