package com.yourcompany.rentalmanagement.controller;

import com.yourcompany.rentalmanagement.dao.PaymentDaoImp;
import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.model.Tenant;

import java.util.List;
import java.util.Map;

public class PaymentController {
    PaymentDaoImp paymentDao = new PaymentDaoImp();
    List<Payment> payments;

    public PaymentController() {

    }

    public List<Payment> getAllPayment() {
        return paymentDao.loadData();
    }

    public Tenant getTenant(long paymentId) {
        return paymentDao.getTenant(paymentId);
    }

    public List<Payment> getFilterPayment(Map<String, String> filter) {
        return paymentDao.filterData(filter);
    }
}
