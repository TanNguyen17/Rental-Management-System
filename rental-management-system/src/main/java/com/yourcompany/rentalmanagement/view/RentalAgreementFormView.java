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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class RentalAgreementFormView implements Initializable{
    RentalAgreement rentalAgreement;
    RentalAgreementController rentalAgreementController = new RentalAgreementController();
    PropertyController propertyController = new PropertyController();
    UserController userController = new UserController();
    Map<String, Object> renderedData = new HashMap<>();
    private long rentalAgreementID;
    boolean isAddingNewData;

    @FXML
    Label ownerShow = new Label();

    @FXML
    ComboBox<Property> propertyInput = new ComboBox<>();

    @FXML
    ComboBox<Host> hostInput = new ComboBox<>();

    @FXML
    CheckComboBox<Tenant> subTenantInput = new CheckComboBox<>();

    @FXML
    ComboBox<Integer> contractedTimeInput = new ComboBox<>();

    @FXML
    ComboBox<RentalAgreement.rentalAgreementStatus> statusInput = new ComboBox<>();

    @FXML
    Button cancelButton = new Button();

    @FXML
    Button updateButton = new Button();


    @FXML
    public void closeWindow(Button closeButton) {
        // Get the current stage and close it
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        cancelButton.setOnMouseClicked(e -> {
            closeWindow(cancelButton);
        });

        updateButton.setOnMouseClicked(e -> {
            if (isAddingNewData){
                addInformation();
            } else {
                updateInformation(1); // How to pass id
            }
        });

    }

    public void showRentalAgreementByIdForUpdate(long id){
        rentalAgreement = rentalAgreementController.getRentalAgreementById(id);

        // For Property Update
        Property property;
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
        List<Tenant> otherTenants = userController.getTenants();
        subTenantInput.getItems().addAll(FXCollections.observableArrayList(otherTenants));
//        subTenantInput.getCheckModel().getCheckedItems().addAll();

        // For status update
        RentalAgreement.rentalAgreementStatus status = rentalAgreement.getStatus();
        statusInput.setValue(status);
        statusInput.getItems().addAll(RentalAgreement.rentalAgreementStatus.NEW,
                RentalAgreement.rentalAgreementStatus.ACTIVE, RentalAgreement.rentalAgreementStatus.COMPLETED);

        // For contract periods update => store renting fee
        contractedTimeInput.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
        LocalDate currentStartDate = rentalAgreement.getStartContractDate();
        LocalDate currentEndDate = rentalAgreement.getEndContractDate();
        if ((currentStartDate == null) ||  (currentEndDate == null)) {
            contractedTimeInput.setValue(0);
        } else {
            contractedTimeInput.setValue((int) ChronoUnit.MONTHS.between(currentStartDate, currentEndDate));
        }

        updateButton.setOnMouseClicked(e -> {
            System.out.println("You are updating new rental agreement!");
            updateInformation(1); // How to pass id
        });
    }


    public void updateInformation(long id){
        checkAddingNewData(false);
        int monthsToAdd = contractedTimeInput.getValue();
        rentalAgreementID = id;
        Property newProperty = propertyInput.getValue();
        List<Tenant> newSubTenants = subTenantInput.getCheckModel().getCheckedItems();
        Host newHost = hostInput.getValue();
        LocalDate newStartDate = LocalDate.now();
        LocalDate newEndDate = ChronoUnit.MONTHS.addTo(newStartDate, monthsToAdd);
        // LocalDate newEndDate = LocalDate.now();
        double newRentingFee = monthsToAdd * propertyInput.getValue().getPrice() * 1.1;
        RentalAgreement.rentalAgreementStatus newStatus = statusInput.getValue();

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("property", newProperty);
        updatedData.put("host", newHost);
        updatedData.put("owner", newProperty.getOwner());
        updatedData.put("subTenants", newSubTenants);
        updatedData.put("startDate", newStartDate);
        updatedData.put("endDate", newEndDate);
        updatedData.put("rentingFee", newRentingFee);
        updatedData.put("status", newStatus);

        rentalAgreementController.updateRentalAgreementById(id, updatedData);

    }

    public void showAddNewDataForm() {
        checkAddingNewData(true);
        List<Property> otherProperties = propertyController.getPropertyList();
        propertyInput.setItems(FXCollections.observableArrayList(otherProperties));
        propertyInput.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Update the label text when the ComboBox value changes
            if (newValue != null) {
                ownerShow.setText("Owner: " + newValue.getOwner().toString());
            }

            List<Host> otherHosts = userController.getAllHosts();
            hostInput.setItems(FXCollections.observableArrayList(otherHosts));

            List<Tenant> otherTenants = userController.getTenants();
            subTenantInput.getItems().addAll(FXCollections.observableArrayList(otherTenants));

            statusInput.getItems().addAll(RentalAgreement.rentalAgreementStatus.NEW,
                    RentalAgreement.rentalAgreementStatus.ACTIVE, RentalAgreement.rentalAgreementStatus.COMPLETED);

            contractedTimeInput.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
        });

        updateButton.setOnMouseClicked(e -> {
            addInformation();
            System.out.println("You are adding new rental agreement!");
        });
    }

    public void addInformation(){
        int monthsToAdd = contractedTimeInput.getValue();
        Property newProperty = propertyInput.getValue();
        List<Tenant> newSubTenants = subTenantInput.getCheckModel().getCheckedItems();
        Host newHost = hostInput.getValue();
        LocalDate newStartDate = LocalDate.now();
        LocalDate newEndDate = ChronoUnit.MONTHS.addTo(newStartDate, monthsToAdd);
        // LocalDate newEndDate = LocalDate.now();
        double newRentingFee = monthsToAdd * propertyInput.getValue().getPrice() * 1.1;
        RentalAgreement.rentalAgreementStatus newStatus = statusInput.getValue();

        Map<String, Object> newData = new HashMap<>();
        newData.put("property", newProperty);
        newData.put("host", newHost);
        newData.put("owner", newProperty.getOwner());
        newData.put("subTenants", newSubTenants);
        newData.put("startDate", newStartDate);
        newData.put("endDate", newEndDate);
        newData.put("rentingFee", newRentingFee);
        newData.put("status", newStatus);

        rentalAgreementController.createRentalAgreement(newData);
    }

    public void checkAddingNewData(boolean isAddNewData){
        this.isAddingNewData = isAddNewData;
    }
}