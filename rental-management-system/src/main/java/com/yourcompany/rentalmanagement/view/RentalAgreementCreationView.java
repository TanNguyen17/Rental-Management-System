package com.yourcompany.rentalmanagement.view;
/**
 * @author FTech
 */
import com.yourcompany.rentalmanagement.controller.RentalAgreementController;
import com.yourcompany.rentalmanagement.controller.UserController;
import com.yourcompany.rentalmanagement.model.*;
import com.yourcompany.rentalmanagement.util.AlertUtils;
import com.yourcompany.rentalmanagement.util.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.controlsfx.control.CheckComboBox;
import javafx.scene.text.Text;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class RentalAgreementCreationView implements Initializable {
    private RentalAgreementController rentalAgreementController = new RentalAgreementController();
    private UserController userController = new UserController();

    private List<Integer> contractTime = new ArrayList<>();
    private List<Host> hostInfo;
    private List<String> hostNames = new ArrayList<>();
    private List<Tenant> tenants = new ArrayList<>();
    private Map<String, Long> tenantNames = new HashMap<>();
    private List<Long> selectedTenants = new ArrayList<>();
    private Owner ownerInfo;
    private Property propertyInfo;
    private UserSession userSession = UserSession.getInstance();

    @FXML
    private ComboBox<Integer> contractPeriod;

    @FXML
    private Button createBtn;

    @FXML
    private Text chooseHostError;

    @FXML
    private Text contractPeriodError;

    @FXML
    private ComboBox<String> hostSelection;

    @FXML
    private CheckComboBox<String> tenantSelection;

    @FXML
    private Label ownerDetail;

    @FXML
    private Label propertyDetail;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        contractPeriodError.setVisible(false);
        chooseHostError.setVisible(false);

        initiateTenantData();
        initiateContractPeriod();
    }

    public void setInformation(Map<String, Object> data) {
        hostInfo = ((List<Host>) data.get("hosts"));
        ownerInfo = ((Owner) data.get("owner"));
        propertyInfo = ((Property) data.get("property"));

        for (Host host : hostInfo) {
            hostNames.add(host.getUsername());
        }
        this.hostSelection.getItems().addAll(hostNames);

        ownerDetail.setText(ownerInfo.getUsername());
        propertyDetail.setText(propertyInfo.getTitle());
    }

    private void initiateTenantData() {
        tenants = userController.getTenants();
        for (Tenant tenant : tenants) {
            if (tenant.getId() != userSession.getCurrentUser().getId()) {
                tenantNames.put(tenant.getUsername(), tenant.getId());
            }
        }
        this.tenantSelection.getItems().addAll(tenantNames.keySet());
    }

    private void initiateContractPeriod() {
        ObservableList<Integer> contractTime = FXCollections.observableArrayList(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
        this.contractPeriod.setItems(contractTime);
    }

    @FXML
    void createRentalAgreement(ActionEvent event) {
        System.out.println("Creating Rental Agreement ...");
        System.out.println(contractPeriod.getValue());
        RentalAgreement newRentalAgreement = new RentalAgreement();
        Host selectHost = null;

        if (hostSelection.getValue() != null) {
            for (Host host : hostInfo) {
                if (host.getUsername().equals(hostSelection.getValue())) {
                    selectHost = host;
                }
            }
        } else {
            chooseHostError.setVisible(true);
            chooseHostError.setText("Please select a host");
            return;
        }

        // Get contract selected
        if (contractPeriod.getValue() != null) {
            LocalDate startContractDate = LocalDate.now();
            int periodInMonths = contractPeriod.getValue();

            LocalDate endContractDate = startContractDate.plusMonths(periodInMonths);
            newRentalAgreement.setStartContractDate(startContractDate);
            newRentalAgreement.setEndContractDate(endContractDate);
        } else {
            contractPeriodError.setVisible(true);
            contractPeriodError.setText("Please select a contract period");
            return;
        }

        // Get tenant selected
//        if (tenantSelection.getItems() != null) {
//            for (String tenantName : tenantSelection.getItems().stream().toList()) {
//                selectedTenants.add(tenantNames.get(tenantName));
//            }
//        }

        selectedTenants = tenantSelection.getCheckModel().getCheckedItems().stream()
                .map(tenantNames::get)
                .collect(Collectors.toList());

        // Create new rental agreement
        newRentalAgreement.setStatus(RentalAgreement.rentalAgreementStatus.NEW);
        newRentalAgreement.setRentingFee(propertyInfo.getPrice());
        rentalAgreementController.createRentalAgreement(newRentalAgreement, userSession.getCurrentUser().getId(), propertyInfo, ownerInfo.getId(), selectHost.getId(), selectedTenants);
    }

    public void showSuccessAlert(String title, String content) {
        AlertUtils.showSuccessAlert(title, content);
    }

    public void showErrorAlert(String title, String content) {
        AlertUtils.showErrorAlert(title, content);
    }
}
