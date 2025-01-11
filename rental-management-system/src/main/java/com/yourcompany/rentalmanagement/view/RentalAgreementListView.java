package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.controller.RentalAgreementController;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import com.yourcompany.rentalmanagement.model.UserRole;
import com.yourcompany.rentalmanagement.util.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class RentalAgreementListView implements Initializable {
    private RentalAgreementController rentalAgreementController = new RentalAgreementController();
    private UserSession userSession = UserSession.getInstance();
    private ObservableList<RentalAgreement> rentalAgreements;

    @FXML
    TableView<RentalAgreement> rentalAgreementTableView = new TableView<>();

    @FXML
    TableColumn<RentalAgreement, String> agreementId = new TableColumn<>();

    @FXML
    TableColumn<RentalAgreement, RentalAgreement.rentalAgreementStatus> status = new TableColumn<>();

    @FXML
    TableColumn<RentalAgreement, LocalDate> startDate = new TableColumn<>();

    @FXML
    TableColumn<RentalAgreement, LocalDate> endDate = new TableColumn<>();

    @FXML
    TableColumn<RentalAgreement, Long> owner = new TableColumn<>();

    @FXML
    TableColumn<RentalAgreement, String> host = new TableColumn<>();

    @FXML
    private TableColumn<RentalAgreement, Button> view = new TableColumn<>();

    @FXML
    private TableColumn<RentalAgreement, Button> delete = new TableColumn<>();

    @FXML
    TableColumn<RentalAgreement, Double> rentingFee = new TableColumn<>();

    @FXML
    Button addNewBtn = new Button();

    @Override
    public void initialize(URL url, ResourceBundle bundle){
        initializeColumn();
        initializeViewMoreColumn();
        initializeDeleteColumn();
        rentalAgreements = FXCollections.observableArrayList(rentalAgreementController.getAllRentalAgreements(UserRole.MANAGER, 1));
        rentalAgreementTableView.setItems(rentalAgreements);
        addNewBtn.setOnMouseClicked(e -> {
            openAddNewDataForm();
        });
    }

    private void loadingData() {
        rentalAgreements = FXCollections.observableArrayList(rentalAgreementController.getAllRentalAgreements(userSession.getCurrentUser().getRole(), userSession.getCurrentUser().getId()));
    }

    private void initializeColumn(){
        agreementId.setCellValueFactory(new PropertyValueFactory<>("id"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        startDate.setCellValueFactory(new PropertyValueFactory<>("startContractDate"));
        endDate.setCellValueFactory(new PropertyValueFactory<>("endContractDate"));
        owner.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        host.setCellValueFactory(new PropertyValueFactory<>("hostName"));
        rentingFee.setCellValueFactory(new PropertyValueFactory<>("rentingFee"));
//        tenants.setCellValueFactory(new PropertyValueFactory<>("tenantsName"));
    }

    private void initializeViewMoreColumn() {
        view.setCellFactory(col -> new TableCell<>(){
            @Override
            public void updateItem(Button item, boolean empty){
                super.updateItem(item, empty);
                setText(null);
                setGraphic(null);
                if (!empty){
                    Button button = new Button("View More");
                    button.setOnAction(e -> {
                        long raId = this.getTableView().getItems().get(getIndex()).getId();
                        openUpdateForm(raId);
                    });
                    setText(null);
                    setGraphic(button);
                }
            }
        });
    }

    private void initializeDeleteColumn() {
        delete.setCellFactory(col -> new TableCell<>(){
            @Override
            public void updateItem(Button item, boolean empty){
                super.updateItem(item, empty);
                setText(null);
                setGraphic(null);
                if (!empty){
                    Button button = new Button("Delete");
                    button.setOnAction(e -> {
                        long raId = this.getTableView().getItems().get(getIndex()).getId();
                        deleteRow(raId);
                    });
                    setText(null);
                    setGraphic(button);
                }
            }
        });
    }

    private void openAddNewDataForm(){
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/manager/rental-agreement-management/RentalAgreementForm.fxml"));
            VBox newRoot = loader.load();

            RentalAgreementFormView controller = loader.getController();
            controller.showAddNewDataForm();

            // Create a new Stage (or replace the existing one)
            Stage newStage = new Stage();
            newStage.setTitle("Add Rental Agreement");
            newStage.setScene(new Scene(newRoot, 400, 360));
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openUpdateForm(long id) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/manager/rental-agreement-management/RentalAgreementForm.fxml"));
            VBox newRoot = loader.load();

            // Pass id to form
            RentalAgreementFormView controller = loader.getController();
            controller.showRentalAgreementByIdForUpdate(id);

            // Create a new Stage (or replace the existing one)
            Stage newStage = new Stage();
            newStage.setTitle("Update Rental Agreement");
            newStage.setScene(new Scene(newRoot, 400, 360));
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteRow(long id){
        rentalAgreementController.deleteRentalAgreementById(id);
    }



}