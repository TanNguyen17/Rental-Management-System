package com.yourcompany.rentalmanagement.view;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.yourcompany.rentalmanagement.controller.RentalAgreementController;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import com.yourcompany.rentalmanagement.model.UserRole;
import com.yourcompany.rentalmanagement.util.UserSession;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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
    TableColumn<RentalAgreement, LocalDate> contractedDate = new TableColumn<>();

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

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        rentalAgreements = FXCollections.observableArrayList();

        initializeColumn();
        initializeViewMoreColumn();

        if (userSession.getCurrentUser().getRole().equals(UserRole.MANAGER) || userSession.getCurrentUser().getRole().equals(UserRole.HOST)) {
            delete.setVisible(false);
            initializeDeleteColumn();
        }

        loadingData();

        rentalAgreementTableView.setItems(rentalAgreements);
    }

    private void loadingData() {
        System.out.println("loading data");
        rentalAgreements.setAll(rentalAgreementController.getAllRentalAgreements(
                userSession.getCurrentUser().getRole(),
                userSession.getCurrentUser().getId()
        ));
    }

    private void initializeColumn() {
        agreementId.setCellValueFactory(new PropertyValueFactory<>("id"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        contractedDate.setCellValueFactory(new PropertyValueFactory<>("contractDate"));
        owner.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        host.setCellValueFactory(new PropertyValueFactory<>("hostName"));
        rentingFee.setCellValueFactory(new PropertyValueFactory<>("rentingFee"));
//        tenants.setCellValueFactory(new PropertyValueFactory<>("tenantsName"));

        status.setCellFactory(column -> {
            return new TableCell<RentalAgreement, RentalAgreement.rentalAgreementStatus>() {
                @Override
                protected void updateItem(RentalAgreement.rentalAgreementStatus item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                        getStyleClass().removeAll("status-new", "status-active", "status-completed");
                    } else {
                        setText(item.toString());
                        switch (item) {
                            case NEW:
                                getStyleClass().add("status-new");
                                break;
                            case ACTIVE:
                                getStyleClass().add("status-active");
                                break;
                            case COMPLETED:
                                getStyleClass().add("status-completed");
                                break;
                        }
                    }
                }
            };
        });
    }

    private void initializeViewMoreColumn() {
        view.setCellFactory(col -> new TableCell<>() {
            @Override
            public void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                setGraphic(null);
                if (!empty) {
                    Button button = new Button("View More");
                    button.getStyleClass().addAll("button", "view-button");
                    button.setOnAction(e -> {
                        RentalAgreement agreement = getTableView().getItems().get(getIndex());
                        System.out.println("Viewing agreement: " + agreement.getId());
                    });
                    setGraphic(button);
                }
            }
        });
    }

    private void initializeDeleteColumn() {
        delete.setCellFactory(col -> new TableCell<>() {
            @Override
            public void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                setGraphic(null);
                if (!empty) {
                    Button button = new Button("Delete");
                    button.getStyleClass().addAll("button", "delete-button");
                    button.setOnAction(e -> {
                        RentalAgreement agreement = getTableView().getItems().get(getIndex());
                        System.out.println("Deleting agreement: " + agreement.getId());
                    });
                    setGraphic(button);
                }
            }
        });
    }

}
