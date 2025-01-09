package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.controller.RentalAgreementController;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    RentalAgreementController rentalAgreementController = new RentalAgreementController();
    ObservableList<RentalAgreement> rentalAgreements = FXCollections.observableArrayList(rentalAgreementController.getAllRentalAgreements());

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
    private TableColumn<RentalAgreement, String> actions;

    @FXML
    TableColumn<RentalAgreement, Double> rentingFee = new TableColumn<>();

    @Override
    public void initialize(URL url, ResourceBundle bundle){
        initializeColumn();
        rentalAgreementTableView.setItems(rentalAgreements);
    }

    public void initializeColumn(){
        agreementId.setCellValueFactory(new PropertyValueFactory<>("id"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        contractedDate.setCellValueFactory(new PropertyValueFactory<>("contractDate"));
        owner.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        host.setCellValueFactory(new PropertyValueFactory<>("hostName"));
        rentingFee.setCellValueFactory(new PropertyValueFactory<>("rentingFee"));
//        tenants.setCellValueFactory(new PropertyValueFactory<>("tenantsName"));
    }

    private void initializeActionColumn() {
        Callback<TableColumn<RentalAgreement, String>, TableCell<RentalAgreement, String>> cellFactory =
                (TableColumn<RentalAgreement, String> param) -> {
            final TableCell<RentalAgreement, String> cell = new TableCell<RentalAgreement, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        FontAwesomeIconView viewIcon = new FontAwesomeIconView(FontAwesomeIcon.DOT_CIRCLE_ALT);
                        FontAwesomeIconView sendEmail = new FontAwesomeIconView(FontAwesomeIcon.MAIL_FORWARD);

                        viewIcon.setStyle(
                                "-fx-cursor: hand ;"
                                        + "-glyph-size:28px;"
                        );

                        viewIcon.setOnMouseClicked(event -> {
                            System.out.println("You want to view!");
                        });

                        sendEmail.setOnMouseClicked(event -> {
                            System.out.println("You want to send email!");
                        });

                        HBox viewButton = new HBox(viewIcon, sendEmail);
                        viewButton.setStyle("-fx-alignment:center");
                        setGraphic(viewButton);
                        setText(null);
                    }
                }
            };
            return cell;
        };
        actions.setCellFactory(cellFactory);
    }

}