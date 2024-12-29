package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.model.Payment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class PaymentViewController implements Initializable {
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
        Payment p1 = new Payment();
        p1.setAmount(5000.0);
        p1.setMethod("online");
        p1.setReceipt("tanne");
        p1.setStatus("done");

        Payment p2 = new Payment();
        p2.setAmount(5000.0);
        p2.setMethod("online");
        p2.setReceipt("tanne");
        p2.setStatus("done");

        methodOption.setItems(FXCollections.observableArrayList("Cash", "Card"));
        statusOption.setItems(FXCollections.observableArrayList("Done", "Not Done"));

        return FXCollections.observableArrayList(p1, p2);
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

        paymentTable.setItems(initialData());
    }
}
