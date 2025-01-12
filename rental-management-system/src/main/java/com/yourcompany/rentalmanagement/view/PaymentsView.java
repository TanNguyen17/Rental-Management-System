package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.controller.PaymentController;
import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.model.Tenant;
import com.yourcompany.rentalmanagement.model.User;
import com.yourcompany.rentalmanagement.model.UserRole;
import com.yourcompany.rentalmanagement.util.AlertUtils;
import com.yourcompany.rentalmanagement.util.EmailUtil;
import com.yourcompany.rentalmanagement.util.UserSession;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PaymentsView implements Initializable {

    private PaymentController paymentController = new PaymentController();
    private ObservableList<Payment> payments = FXCollections.observableArrayList();
    private Map<Integer, List<Payment>> pageCache = new HashMap<>();
    private Map<String, String> filter = new HashMap<>();
    private UserSession userSession = UserSession.getInstance();
    private User currentUser = userSession.getCurrentUser();
//    private User currentUser = UserSession.getInstance().getCurrentUser();

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

        paymentTable.addEventFilter(ScrollEvent.ANY, event -> {
            if (!isLoading && isAtBottom() && !allDataLoaded) {
                loadMore();
            }
        });
    }

    public void refreshData() {
        pageCache.clear();
        currentPageIndex = 1;
        payments.clear();
        loadPayments(currentPageIndex);
        System.out.println("tan");
    }

    private void initializeCombobox() {
        ObservableList<String> methodOptions = FXCollections.observableArrayList("All");
        for (Payment.paymentMethod m : Payment.paymentMethod.values()) {
            methodOptions.add(m.toString());
        }
        methodOption.setItems(methodOptions);
        methodOption.setValue("All");

        ObservableList<String> statusOptions = FXCollections.observableArrayList("All");
        for (Payment.paymentStatus status : Payment.paymentStatus.values()) {
            statusOptions.add(status.toString());
        }
        statusOption.setItems(statusOptions);
        statusOption.setValue("All");
    }

    private void initializeColumn() {
        receipt.setCellValueFactory(new PropertyValueFactory<Payment, String>("receipt"));
        method.setCellValueFactory(new PropertyValueFactory<Payment, String>("method"));
        amount.setCellValueFactory(new PropertyValueFactory<Payment, Double>("amount"));
        status.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getStatus().toString()));
        status.setCellFactory(column -> {
            return new TableCell<Payment, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                        getStyleClass().removeAll("status-paid", "status-unpaid");
                    } else {
                        setText(item);
                        if (item.equals("PAID")) {
                            getStyleClass().add("status-paid");
                            getStyleClass().remove("status-unpaid");
                        } else if (item.equals("UNPAID")) {
                            getStyleClass().add("status-unpaid");
                            getStyleClass().remove("status-paid");
                        }
                    }
                }
            };
        });
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

                        FontAwesomeIconView viewIcon = new FontAwesomeIconView(FontAwesomeIcon.EYE);
                        viewIcon.getStyleClass().add("action-icon");

                        FontAwesomeIconView payIcon = null;
                        FontAwesomeIconView sendEmailIcon = null;

                        if (currentUser.getRole().equals(UserRole.TENANT)) {
                            payIcon = new FontAwesomeIconView(FontAwesomeIcon.MONEY);
                            payIcon.getStyleClass().add("action-icon");
                        } else {
                            sendEmailIcon = new FontAwesomeIconView(FontAwesomeIcon.MAIL_REPLY);
                        }


                        viewIcon.setStyle(
                                "-fx-cursor: hand ;"
                                        + "-glyph-size:20px;"
                                + "-fx-color: #fff"
                        );
                        payIcon.setStyle(
                                "-fx-cursor: hand ;"
                                        + "-glyph-size:20px;"
                                        + "-fx-color: #fff"
                        );

                        HBox action = null;

                        viewIcon.setOnMouseClicked(event -> {
                            handleViewPayment();
                        });

                        if (payIcon != null) {
                            payIcon.setOnMouseClicked(event -> {
                                handlePayment();
                            });
                            action = new HBox(viewIcon, payIcon);
                        }
                        if (sendEmailIcon != null) {
                            sendEmailIcon.setOnMouseClicked(event -> {
                                handleSendEmail();
                            });
                            action = new HBox(viewIcon, sendEmailIcon);
                        }

                        action.setStyle("-fx-alignment:center");
                        action.setSpacing(5);
                        setGraphic(action);
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
        if (payment == null) {
            return;
        }

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/PaymentView.fxml"));

        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(TenantView.class.getName()).log(Level.SEVERE, null, ex);
        }

        paymentView = loader.getController();
        paymentView.setData(payment);
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
            EmailUtil.sendEmail(tenant.getEmail(), "Payment Reminder from Rental Management System", "Hi this is" +
                    "a reminder for your rental payment payment info " + payment.getReceipt() + "with fee of: " + payment.getAmount()
                    + "due date " + payment.getDueDate());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    private void handlePayment() {
        payment = paymentTable.getSelectionModel().getSelectedItem();
        if (payment == null) {
            return;
        }

        if (payment.getStatus().toString().toLowerCase().equals("paid")) {
            AlertUtils.showSuccessAlert("Payment is paid", "Your transaction has been paid");
        } else {
            if (paymentController.changePaymentStatus(payment.getId())) {
                AlertUtils.showSuccessAlert("Payment is completed", "Your transaction has been completed successfully");
            } else {
                AlertUtils.showErrorAlert("Payment is not completed", "Your transaction is not completed due to some error");
            }
        }
    }

    @FXML
    public void filterPayment(ActionEvent event) {
        filter.clear();
        String method = methodOption.getValue();
        String status = statusOption.getValue();

        if (method != null && !method.equals("All")) {
            filter.put("method", method);
        }
        if (status != null && !status.equals("All")) {
            filter.put("status", status);
        }

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
            List<Payment> paymentList = paymentController.getPaymentsByRole(page, filter, currentUser.getRole(), currentUser.getId());

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
