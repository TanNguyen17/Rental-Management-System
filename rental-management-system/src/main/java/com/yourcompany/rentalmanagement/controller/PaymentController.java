package com.yourcompany.rentalmanagement.controller;

import com.yourcompany.rentalmanagement.dao.PaymentDaoImp;
import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.model.Tenant;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PaymentController {
    PaymentDaoImp paymentDao = new PaymentDaoImp();
    List<Payment> payments;
    private static final int PAGE_SIZE = 10;

    public PaymentController() {

    }

    public List<Payment> getPayments(int pageNumber, Map<String, String> filterValue) {
        return paymentDao.loadData(pageNumber, filterValue);
    }

    public Tenant getTenant(long paymentId) {
        return paymentDao.getTenant(paymentId);
    }

    public long getPaymentCount(Map<String, String> filterValue) {
        return paymentDao.getPaymentCount(filterValue);
    }
}
