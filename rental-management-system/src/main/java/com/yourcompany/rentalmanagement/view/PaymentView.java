package com.yourcompany.rentalmanagement.view;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class PaymentView {
    @FXML
    private Text paymentTitle;

    @FXML
    private Text tenantName;

    @FXML
    private Text paymentAmount;

    @FXML
    private Text paymentDue;

    @FXML
    private Text paymentStatus;

    @FXML
    void setText(String tenantName, String paymentTitle, double paymentAmount, String paymentDue, String paymentStatus) {
        this.paymentTitle.setText(paymentTitle);
        this.paymentAmount.setText(String.valueOf(paymentAmount));
        this.paymentDue.setText(paymentDue);
        this.paymentStatus.setText(paymentStatus);
        this.tenantName.setText(tenantName);
    }
}
