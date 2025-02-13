package com.yourcompany.rentalmanagement.view;
/**
 * @author FTech
 */
import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.util.TimeFormat;
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
    private Text paymentMethod;

    @FXML
    private Text paymentStatus;

    @FXML
    void setData(Payment payment) {
        this.paymentTitle.setText(payment.getReceipt());
        this.paymentAmount.setText(String.valueOf(payment.getAmount()));
        this.paymentMethod.setText(payment.getMethod().toString());
        this.paymentDue.setText(payment.getDueDate() != null ? TimeFormat.dateToString(payment.getDueDate()) : null);
        this.paymentStatus.setText(payment.getStatus().toString());
        this.tenantName.setText(payment.getTenant().getUsername());
    }
}
