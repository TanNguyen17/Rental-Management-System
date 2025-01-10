package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.controller.PropertyController;
import com.yourcompany.rentalmanagement.controller.RentalAgreementController;
import com.yourcompany.rentalmanagement.model.Host;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import com.yourcompany.rentalmanagement.model.Tenant;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class RentalAgreementFormView implements Initializable {
    RentalAgreement rentalAgreement;
    RentalAgreementController rentalAgreementController = new RentalAgreementController();
    PropertyController propertyController = new PropertyController();
    Map<String, Object> renderedData = new HashMap<>();

    @FXML
    ComboBox<Property> propertyInput = new ComboBox<>();

    @FXML
    ComboBox<Host> hostInput = new ComboBox<>();

    @FXML
    ComboBox<Tenant> subTenantInput = new ComboBox<>();

    @FXML
    ComboBox<String> contractedTimeInput = new ComboBox<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        showRentalAgreementByIdForUpdate(1);
    }

    public void showRentalAgreementByIdForUpdate(long id){
        rentalAgreement = rentalAgreementController.getRentalAgreementById(id);

        // For Property Update
        Property property = new Property();
        if (rentalAgreement.getCommercialProperty() != null) {
             property = rentalAgreement.getCommercialProperty();
        } else {
            property = rentalAgreement.getResidentialProperty();
        }
        List<Property> otherProperties = propertyController.getPropertyList(); // Need to change to getAvailable
        // Property
        propertyInput.setItems(FXCollections.observableArrayList(otherProperties));
        propertyInput.setValue(property);

        // For Host Update
        Host host = rentalAgreement.getHost();
    }
}