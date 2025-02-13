package com.yourcompany.rentalmanagement.view;
/**
 * @author FTech
 */
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.controlsfx.control.CheckComboBox;

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

public class RentalAgreementFormView implements Initializable {

    private RentalAgreement rentalAgreement;
    private RentalAgreementController rentalAgreementController = new RentalAgreementController();
    private PropertyController propertyController = new PropertyController();
    private UserController userController = new UserController();
    private Map<String, Object> renderedData = new HashMap<>();
    private Map<String, Object> result = new HashMap<>();

    private Long rentalAgreementID = null;
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
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleSubmit() {
        if (isAddingNewData) {
            addInformation();
        } else {
            if (rentalAgreementID != null) {
                updateInformation(rentalAgreementID);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cancelButton.setOnMouseClicked(e -> {
            handleCancel();
        });

        updateButton.setOnMouseClicked(e -> {
            handleSubmit();
        });

    }

    public void showRentalAgreementByIdForUpdate(long id) {
        // set chosen rental agreement id
        this.rentalAgreementID = id;

        // get rental agreement data along with property
        result = rentalAgreementController.getFullRentalAgreementById(id);
        rentalAgreement = (RentalAgreement) result.get("rentalAgreement");
        Property property = (Property) result.get("property");
        if (rentalAgreement != null) {
            if (property != null) {
                List<Property> otherProperties = propertyController.getPropertyList();
                propertyInput.setItems(FXCollections.observableArrayList(otherProperties));
                propertyInput.setValue(property);
                propertyInput.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null && newValue.getOwner() != null) {
                        ownerShow.setText("Owner: " + newValue.getOwner().toString());
                        if (newValue.getRentalAgreement() != null
                                && newValue.getRentalAgreement().getHost() != null) {
                            hostInput.setValue(newValue.getRentalAgreement().getHost());
                        }
                    }
                });

                if (property.getOwner() != null) {
                    ownerShow.setText("Owner: " + property.getOwner().toString());
                }

                Host host = rentalAgreement.getHost();
                if (host != null) {
                    List<Host> otherHosts = userController.getAllHosts();
                    hostInput.setValue(host);
                    hostInput.setItems(FXCollections.observableArrayList(otherHosts));
                }

                List<Tenant> otherTenants = userController.getTenants();
                if (otherTenants != null && !otherTenants.isEmpty()) {
                    subTenantInput.getItems().addAll(FXCollections.observableArrayList(otherTenants));
                }

                RentalAgreement.rentalAgreementStatus status = rentalAgreement.getStatus();
                if (status != null) {
                    statusInput.setValue(status);
                    statusInput.getItems().addAll(RentalAgreement.rentalAgreementStatus.values());
                }

                contractedTimeInput.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
                LocalDate currentStartDate = rentalAgreement.getStartContractDate();
                LocalDate currentEndDate = rentalAgreement.getEndContractDate();
                if (currentStartDate != null && currentEndDate != null) {
                    contractedTimeInput.setValue((int) ChronoUnit.MONTHS.between(currentStartDate, currentEndDate));
                }
            }
        }
    }

    public void updateInformation(long id) {
        checkAddingNewData(false);
        int monthsToAdd = contractedTimeInput.getValue();
        rentalAgreementID = id;
        Long newPropertyId = propertyInput.getValue().getId();
        Long newOwnerId = propertyInput.getValue().getOwner().getId();
        List<Long> newSubTenantIds = subTenantInput
                .getCheckModel()
                .getCheckedItems()
                .stream()
                .map(Tenant::getId)
                .collect(Collectors.toList());
        Long newHostId = hostInput.getValue().getId();
        LocalDate newStartDate = LocalDate.now();
        LocalDate newEndDate = ChronoUnit.MONTHS.addTo(newStartDate, monthsToAdd);
        // LocalDate newEndDate = LocalDate.now();
        double newRentingFee = monthsToAdd * propertyInput.getValue().getPrice() * 1.1;
        RentalAgreement.rentalAgreementStatus newStatus = statusInput.getValue();

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("property", newPropertyId);
        updatedData.put("host", newHostId);
        updatedData.put("owner", newOwnerId);
        updatedData.put("subTenants", newSubTenantIds);
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

    public void addInformation() {
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

    public void checkAddingNewData(boolean isAddNewData) {
        this.isAddingNewData = isAddNewData;
    }
}