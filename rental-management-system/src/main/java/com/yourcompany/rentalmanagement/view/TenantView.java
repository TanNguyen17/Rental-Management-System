package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.controller.PaymentController;
import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.model.Tenant;
import com.yourcompany.rentalmanagement.util.EmailUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TenantView implements Initializable {

    PaymentController paymentController = new PaymentController();
    List<Payment> paymentList = new ArrayList<>();
    Payment payment;
    private int currentPageIndex = 1;

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
    private MFXButton findButton;

    @FXML
    private Pagination pagination;

    @FXML
    public void initialData() {
        paymentList = paymentController.getPaymentsPag(currentPageIndex);
        paymentTable.setItems(FXCollections.observableList(paymentList));
    }

    @FXML
    private void btnFind(ActionEvent event) {
        methodOption.getItems().clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        methodOption.setItems(FXCollections.observableArrayList("Debit Card", "Credit Card", "Bank Transfer"));
        statusOption.setItems(FXCollections.observableArrayList("Failed", "Completed", "Pending"));

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
                        FontAwesomeIconView sendEmail = new FontAwesomeIconView(FontAwesomeIcon.MAIL_FORWARD);

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

                        sendEmail.setOnMouseClicked(event -> {
                            payment = paymentTable.getSelectionModel().getSelectedItem();
                            Tenant tenant = paymentController.getTenant(payment.getId());
                            System.out.println(tenant.getEmail());

                            try {
                                EmailUtil.sendEmail(tenant.getEmail(), "gagag", "gagaga");
                            } catch (MessagingException e) {
                                throw new RuntimeException(e);
                            }
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

        long paymentCount = paymentController.getPaymentCount();
        int pageCount = (int) Math.ceil((double) paymentCount / 10);

        pagination.setPageCount(pageCount);
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(this::createPage);

        this.actions.setCellFactory(cellFactory);
        initialData();
    }

    @FXML
    public void findPayment(ActionEvent event) {
        String method = methodOption.getValue();
        String status = statusOption.getValue();
        System.out.println(method);
        System.out.println(status);
        Map<String, String> filter = new HashMap<>();
        filter.put("method", method);
        filter.put("status", status);
        paymentList = paymentController.getFilterPayment(filter);
        paymentTable.setItems(FXCollections.observableArrayList(paymentList));
    }

    private Node createPage(int pageIndex) {
        currentPageIndex = pageIndex + 1;
        initialData();
        return paymentTable;
    }
}
