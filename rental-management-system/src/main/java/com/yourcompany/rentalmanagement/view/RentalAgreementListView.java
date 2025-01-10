package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.controller.RentalAgreementController;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import com.yourcompany.rentalmanagement.model.UserRole;
import com.yourcompany.rentalmanagement.util.UserSession;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

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
    public void initialize(URL url, ResourceBundle bundle){
        initializeColumn();
        initializeViewMoreColumn();
        initializeDeleteColumn();
        rentalAgreementTableView.setItems(rentalAgreements);
    }

    private void loadingData() {
        FXCollections.observableArrayList(rentalAgreementController.getAllRentalAgreements(userSession.getCurrentUser().getRole(), userSession.getCurrentUser().getId()));
    }

    private void initializeColumn(){
        agreementId.setCellValueFactory(new PropertyValueFactory<>("id"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        contractedDate.setCellValueFactory(new PropertyValueFactory<>("contractDate"));
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
                        System.out.println(this.getTableRow().getIndex());
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
                        System.out.println(this.getTableRow().getIndex());
                    });
                    setText(null);
                    setGraphic(button);
                }
            }
        });
    }

}