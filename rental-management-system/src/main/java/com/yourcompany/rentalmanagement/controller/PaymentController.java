package com.yourcompany.rentalmanagement.controller;

/**
 * @author FTech
 */

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yourcompany.rentalmanagement.dao.impl.PaymentDaoImpl;
import com.yourcompany.rentalmanagement.model.*;

public class PaymentController {

    PaymentDaoImpl paymentDao = new PaymentDaoImpl();
    Map<String, String> data = new HashMap<>();
    List<Payment> payments;
    private static final int PAGE_SIZE = 10;

    public PaymentController() {

    }

    public boolean createPayment(Payment payment, long rentalAgreementId) {
        return paymentDao.createPayment(payment, rentalAgreementId);
    }

    public List<Payment> getPayments(int pageNumber, Map<String, String> filterValue) {
        return paymentDao.loadDataByRole(pageNumber, filterValue, null, 1);
    }

    public List<Payment> getAllPayments(){
        return paymentDao.getAllPayments();
    }

    public List<Payment> getPaymentsByRole(int pageNumber, Map<String, String> filterValue, User.UserRole userRole, long tenantId) {
        return paymentDao.loadDataByRole(pageNumber, filterValue, userRole, tenantId);
    }

    public boolean shouldGeneratePayment(RentalAgreement rentalAgreement, LocalDate today) {
        Payment latestPayment = paymentDao.getLatestPayment(rentalAgreement, today);
        if (latestPayment == null) return true;

        LocalDate nextDueDate = latestPayment.getDueDate().plusMonths(1);
        return today.isEqual(nextDueDate) || today.isAfter(nextDueDate);
    }

    public Tenant getTenant(long paymentId) {
        return paymentDao.getTenant(paymentId);
    }

    public long getPaymentCount(Map<String, String> filterValue, User.UserRole userRole, long userId) {
        Long count = paymentDao.getPaymentCountByRole(filterValue, userRole, userId);
        if (count == null) return 0L;
        return count;
    }

    public boolean changePaymentStatus(long paymentId) {
        return paymentDao.updatePaymentStatus(paymentId);
    }
    public String getTotalIncome() {
        List<Payment> payments = paymentDao.getAllPaidPayments(Payment.paymentStatus.PAID);
        double totalIncome = 0;
        for (Payment payment : payments) {
            totalIncome += payment.getAmount();
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        return decimalFormat.format(totalIncome);
    }
}