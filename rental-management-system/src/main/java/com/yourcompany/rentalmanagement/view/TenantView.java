package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.controller.PaymentController;
import com.yourcompany.rentalmanagement.model.Payment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TenantView implements Initializable {

    PaymentController paymentController = new PaymentController();
    List<Payment> paymentList = new ArrayList<>();
    Payment payment;

    @FXML
    private PaymentView paymentView;

    @FXML
    private TableColumn<Payment, String> receipt;

    @FXML
    private TableColumn<Payment, String> method;

    @FXML
    private TableColumn<Payment, Double> amount;

    @FXML
    private TableColumn<Payment, String> status;

    @FXML
    private TableColumn<Payment, String> actions;

    @FXML
    private ComboBox<String> methodOption;

    @FXML
    private ComboBox<String> statusOption;

    @FXML
    private TableView<Payment> paymentTable;

    @FXML
    ObservableList<Payment> initialData() {
        paymentList = paymentController.getAllPayment();

        methodOption.setItems(FXCollections.observableArrayList("Cash", "Card"));
        statusOption.setItems(FXCollections.observableArrayList("Done", "Not Done"));

        return FXCollections.observableArrayList(paymentList);
    }

    @FXML
    private void btnFind(ActionEvent event) {
        methodOption.getItems().clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        receipt.setCellValueFactory(new PropertyValueFactory<Payment, String>("receipt"));
        method.setCellValueFactory(new PropertyValueFactory<Payment, String>("method"));
        amount.setCellValueFactory(new PropertyValueFactory<Payment, Double>("amount"));
        status.setCellValueFactory(new PropertyValueFactory<Payment, String>("status"));

        Callback<TableColumn<Payment, String>, TableCell<Payment, String>> cellFactory = (TableColumn<Payment, String> param) -> {
            final TableCell<Payment, String> cell = new TableCell<Payment, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        FontAwesomeIconView viewIcon = new FontAwesomeIconView(FontAwesomeIcon.DOT_CIRCLE_ALT);
                        viewIcon.setStyle(
                                "-fx-cursor: hand ;"
                                + "-glyph-size:28px;"
                        );

                        viewIcon.setOnMouseClicked(event -> {
                            payment = paymentTable.getSelectionModel().getSelectedItem();
                            FXMLLoader loader = new FXMLLoader();
                            loader.setLocation(getClass().getResource("/fxml/PaymentView.fxml"));
                            System.out.println(loader);

                            try {
                                loader.load();
                            } catch (IOException ex) {
                                Logger.getLogger(TenantView.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            paymentView = loader.getController();
                            paymentView.setText("tan", payment.getReceipt(),
                                    payment.getAmount(), "17-04-2005", payment.getStatus());
                            Parent parent = loader.getRoot();
                            Stage stage = new Stage();
                            stage.setScene(new Scene(parent));
                            stage.show();
                        });
                        HBox viewButton = new HBox(viewIcon);
                        viewButton.setStyle("-fx-alignment:center");
                        setGraphic(viewButton);
                        setText(null);
                    }
                }
            };
            return cell;
        };
        this.actions.setCellFactory(cellFactory);
        paymentTable.setItems(initialData());
    }
}
