package com.yourcompany.rentalmanagement.controller;

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
        data = paymentDao.createPayment(payment, rentalAgreementId);
        if (data.get("status") == "failed") {
            return false;
        }
        return true;
    }

    public List<Payment> getPayments(int pageNumber, Map<String, String> filterValue) {
        return paymentDao.loadDataByRole(pageNumber, filterValue, null, 1);
    }

    public List<Payment> getPaymentsByRole(int pageNumber, Map<String, String> filterValue, UserRole userRole, long tenantId) {
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

    public long getPaymentCount(Map<String, String> filterValue) {
        return paymentDao.getPaymentCount(filterValue);
    }

    public boolean changePaymentStatus(long paymentId) {
        data = paymentDao.updatePaymentStatus(paymentId);
        if (data.get("status") == "failed") {
            return false;
        }
        return true;
    }
}
