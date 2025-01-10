package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.controller.PropertyController;
import com.yourcompany.rentalmanagement.controller.RentalAgreementController;
import com.yourcompany.rentalmanagement.controller.UserController;
import com.yourcompany.rentalmanagement.model.Host;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import com.yourcompany.rentalmanagement.model.Tenant;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.controlsfx.control.CheckComboBox;
import org.hibernate.annotations.Check;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class RentalAgreementFormView {
    RentalAgreement rentalAgreement;
    RentalAgreementController rentalAgreementController = new RentalAgreementController();
    PropertyController propertyController = new PropertyController();
    UserController userController = new UserController();
    Map<String, Object> renderedData = new HashMap<>();
    private long rentalAgreementID;

    @FXML
    Label ownerShow = new Label();

    @FXML
    ComboBox<Property> propertyInput = new ComboBox<>();

    @FXML
    ComboBox<Host> hostInput = new ComboBox<>();

    @FXML
    CheckComboBox<Tenant> subTenantInput = new CheckComboBox<>();

    @FXML
    ComboBox<String> contractedTimeInput = new ComboBox<>();

    @FXML
    ComboBox<RentalAgreement.rentalAgreementStatus> statusInput = new ComboBox<>();

//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle){
//        showRentalAgreementByIdForUpdate(1);
//    }

    public RentalAgreementFormView(){}

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
        propertyInput.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Update the label text when the ComboBox value changes
            if (newValue != null) {
                ownerShow.setText("Owner: " + newValue.getOwner().toString());
                hostInput.setValue(newValue.getRentalAgreement().getHost());
            }
        });

        // For Owner
        ownerShow.setText("Owner: " + property.getOwner().toString());

        // For Host Update
        Host host = rentalAgreement.getHost();
        List<Host> otherHosts = userController.getAllHosts();
        hostInput.setValue(host);
        hostInput.setItems(FXCollections.observableArrayList(otherHosts));

        //For sub tenant update
//        List<Tenant> tenants = rentalAgreement.getTenants();
//        System.out.println(tenants.toString());
        List<Tenant> otherTenants = userController.getTenants();
        subTenantInput.getItems().addAll(FXCollections.observableArrayList(otherTenants));
//        subTenantInput.getCheckModel().getCheckedItems().addAll();

        // For status update
        RentalAgreement.rentalAgreementStatus status = rentalAgreement.getStatus();
        statusInput.setValue(status);
        statusInput.getItems().addAll(RentalAgreement.rentalAgreementStatus.NEW,
                RentalAgreement.rentalAgreementStatus.ACTIVE, RentalAgreement.rentalAgreementStatus.COMPLETED);

        // For contract periods update => store renting fee
//        List<String> contractPeriod

    }
}