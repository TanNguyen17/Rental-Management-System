package com.yourcompany.rentalmanagement.controller;

import java.util.List;
import java.util.Map;

import com.yourcompany.rentalmanagement.dao.impl.PaymentDaoImpl;
import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.model.Tenant;

public class PaymentController {

    PaymentDaoImpl paymentDao = new PaymentDaoImpl();
    List<Payment> payments;
    private static final int PAGE_SIZE = 10;

    public PaymentController() {

    }

    public List<Payment> getPayments(int pageNumber, Map<String, String> filterValue) {
        return paymentDao.loadDataByRole(pageNumber, filterValue, null, 1);
    }

    public Tenant getTenant(long paymentId) {
        return paymentDao.getTenant(paymentId);
    }

    public long getPaymentCount(Map<String, String> filterValue) {
        return paymentDao.getPaymentCount(filterValue);
    }
}
