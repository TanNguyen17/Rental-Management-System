package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.controller.PaymentController;
import com.yourcompany.rentalmanagement.dao.impl.PaymentDaoImpl;
import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.model.Tenant;
import com.yourcompany.rentalmanagement.util.EmailUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
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
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PaymentsView implements Initializable {

    private PaymentController paymentController = new PaymentController();
    private ObservableList<Payment> payments = FXCollections.observableArrayList();
    private Map<Integer, List<Payment>> pageCache = new HashMap<>();
    private Map<String, String> filter = new HashMap<>();

    private int currentPageIndex = 1;
    private boolean isLoading = false;
    private boolean allDataLoaded = false;

    private Payment payment;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeCombobox();
        initializeColumn();
        initializeActionColumn();

        paymentTable.setItems(payments);
        paymentTable.setMaxHeight(300);
        loadPayments(currentPageIndex);

        paymentTable.addEventFilter(ScrollEvent.ANY, event ->{
            if (!isLoading && isAtBottom() && !allDataLoaded) {
                loadMore();
            }
        });
    }

    private void initializeCombobox() {
        methodOption.setItems(FXCollections.observableArrayList("Debit Card", "Credit Card", "Bank Transfer"));
        statusOption.setItems(FXCollections.observableArrayList("Failed", "Completed", "Pending"));
    }

    private void initializeColumn() {
        receipt.setCellValueFactory(new PropertyValueFactory<Payment, String>("receipt"));
        method.setCellValueFactory(new PropertyValueFactory<Payment, String>("method"));
        amount.setCellValueFactory(new PropertyValueFactory<Payment, Double>("amount"));
        status.setCellValueFactory(new PropertyValueFactory<Payment, String>("status"));
    }

    private void initializeActionColumn() {
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
                            handleViewPayment();
                        });

                        sendEmail.setOnMouseClicked(event -> {
                            handleSendEmail();
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

    private void handleViewPayment() {
        payment = paymentTable.getSelectionModel().getSelectedItem();
        if (payment == null) return;

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
    }

    private void handleSendEmail() {
        payment = paymentTable.getSelectionModel().getSelectedItem();
        if (payment == null) return;

        Tenant tenant = paymentController.getTenant(payment.getId());
        if (tenant == null || tenant.getEmail() == null || tenant.getEmail().isEmpty()) {
            // Handle case where tenant or email is not found
            System.out.println("Tenant or email not found for payment: " + payment.getId());
            return;
        }
        System.out.println(tenant.getEmail());

        try {
            EmailUtil.sendEmail(tenant.getEmail(), "gagag", "gagaga");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void filterPayment(ActionEvent event) {
        filter.clear();
        String method = methodOption.getValue();
        String status = statusOption.getValue();

        filter.put("method", method);
        filter.put("status", status);

        pageCache.clear();
        currentPageIndex = 1;
        payments.clear();
        loadPayments(currentPageIndex);
    }

    private boolean isAtBottom() {
        ScrollBar verticalScrollBar = (ScrollBar) paymentTable.lookup(".scroll-bar:vertical");
        return verticalScrollBar != null && verticalScrollBar.getValue() == verticalScrollBar.getMax();
    }

    private void loadMore() {
        currentPageIndex += 1;
        loadPayments(currentPageIndex);
    }


    private void loadPayments(int page) {
        if (isLoading) {
            return;
        }
        isLoading = true;
        new Thread(() -> {
            List<Payment> paymentList = paymentController.getPayments(page, filter);

            Platform.runLater(() -> {
                if (!paymentList.isEmpty()) {
                    payments.addAll(paymentList);
                    paymentTable.setItems(payments);
                } else {
                    allDataLoaded = true;
                    if (page > 1) {
                        currentPageIndex -= 1;
                    }
                }
                isLoading = false;
            });
        }).start();
    }
}
