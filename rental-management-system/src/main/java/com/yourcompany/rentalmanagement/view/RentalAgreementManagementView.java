package com.yourcompany.rentalmanagement.view;
/**
 * @author FTech
 */
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.yourcompany.rentalmanagement.controller.RentalAgreementController;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import com.yourcompany.rentalmanagement.model.UserRole;
import com.yourcompany.rentalmanagement.util.UserSession;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RentalAgreementManagementView implements Initializable {

    private RentalAgreementController rentalAgreementController = new RentalAgreementController();
    private UserSession userSession = UserSession.getInstance();
    private List<RentalAgreement> rentalAgreements = new ArrayList<>();

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
    private TableColumn<RentalAgreement, String> view = new TableColumn<>();

    @FXML
    private TableColumn<RentalAgreement, String> delete = new TableColumn<>();

    @FXML
    TableColumn<RentalAgreement, Double> rentingFee = new TableColumn<>();

    @FXML
    Button addNewBtn = new Button();

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        initializeColumn();
        if (userSession.getCurrentUser().getRole() == UserRole.MANAGER || userSession.getCurrentUser().getRole() == UserRole.HOST) {
            initializeViewMoreColumn();
            initializeDeleteColumn();
        }


        // cai nay de check user role for add new button visibility
        UserRole currentRole = userSession.getCurrentUser().getRole();
        addNewBtn.setVisible(currentRole == UserRole.MANAGER || currentRole == UserRole.TENANT);
        addNewBtn.setManaged(currentRole == UserRole.MANAGER || currentRole == UserRole.TENANT);

        new Thread(() -> {
            List<RentalAgreement> rentalAgreementList;
            if (currentRole.equals(UserRole.MANAGER)) {
                rentalAgreementList = rentalAgreementController.getAllRentalAgreements(currentRole, 1);
            } else {
                rentalAgreementList = rentalAgreementController.getAllRentalAgreements(currentRole, userSession.getCurrentUser().getId());
            }

            Platform.runLater(() -> {
                if (!rentalAgreementList.isEmpty()) {
                    rentalAgreements.addAll(rentalAgreementList);
                    rentalAgreementTableView.setItems(FXCollections.observableArrayList(rentalAgreements));
                }
            });
        }).start();

        addNewBtn.setOnMouseClicked(e -> {
            openAddNewDataForm();
        });

        // Setup the view column
        if (userSession.getCurrentUser().getRole() == UserRole.MANAGER || userSession.getCurrentUser().getRole() == UserRole.HOST) {
            view.setCellFactory(column -> new TableCell<RentalAgreement, String>() {
                private final Button viewButton = new Button("View More");

                {
                    viewButton.getStyleClass().add("view-more-button");
                    viewButton.setOnAction(event -> {
                        RentalAgreement agreement = getTableView().getItems().get(getIndex());
                        openUpdateForm(agreement.getId());
                    });
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(viewButton);
                    }
                }
            });

            // Setup the delete column
            delete.setCellFactory(column -> new TableCell<RentalAgreement, String>() {
                private final Button deleteButton = new Button("Delete");

                {
                    deleteButton.getStyleClass().add("delete-button");
                    deleteButton.setOnAction(event -> {
                        RentalAgreement agreement = getTableView().getItems().get(getIndex());
                        handleDelete(agreement);
                    });
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(deleteButton);
                    }
                }
            });
        }
    }

//    private void loadingData() {
//        System.out.println("loading data");
//        rentalAgreements.setAll(rentalAgreementController.getAllRentalAgreements(
//                userSession.getCurrentUser().getRole(),
//                userSession.getCurrentUser().getId()
//        ));
//        rentalAgreements = FXCollections.observableArrayList(rentalAgreementController.getAllRentalAgreements(userSession.getCurrentUser().getRole(), userSession.getCurrentUser().getId()));
//    }
    private void initializeColumn() {
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
        view.setCellFactory(col -> new TableCell<RentalAgreement, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Button button = new Button("View More");
                    button.getStyleClass().add("view-more-button");
                    button.setOnAction(e -> {
                        RentalAgreement agreement = getTableView().getItems().get(getIndex());
                        openUpdateForm(agreement.getId());
                    });
                    setGraphic(button);
                }
            }
        });
    }

    private void initializeDeleteColumn() {
        delete.setCellFactory(col -> new TableCell<RentalAgreement, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Button button = new Button("Delete");
                    button.getStyleClass().add("delete-button");
                    button.setOnAction(e -> {
                        RentalAgreement agreement = getTableView().getItems().get(getIndex());
                        handleDelete(agreement);
                    });
                    setGraphic(button);
                }
            }
        });
    }

    private void openAddNewDataForm() {
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
            // Remove the fixed size and let it use the preferred size from FXML
            Scene scene = new Scene(newRoot);
            newStage.setScene(scene);
            // Set minimum size to prevent window from being too small
            newStage.setMinWidth(1000);
            newStage.setMinHeight(700);
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDelete(RentalAgreement agreement) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText("Delete Rental Agreement");
        confirmation.setContentText("Are you sure you want to delete this rental agreement?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                rentalAgreementController.deleteRentalAgreementById(agreement.getId());

                // Remove from local list and refresh table
                rentalAgreements.remove(agreement);
                rentalAgreementTableView.setItems(FXCollections.observableArrayList(rentalAgreements));

                // Show success message
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText(null);
                success.setContentText("Rental Agreement deleted successfully");
                success.show();

            } catch (Exception e) {
                // Show error message if deletion fails
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText("Delete Failed");
                error.setContentText("Could not delete the rental agreement: " + e.getMessage());
                error.show();
            }
        }
    }
}
