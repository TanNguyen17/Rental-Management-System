package com.yourcompany.rentalmanagement.controller;

import com.yourcompany.rentalmanagement.dao.PaymentDaoImp;
import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.model.Tenant;

import java.util.List;
import java.util.Map;

public class PaymentController {
    PaymentDaoImp paymentDao = new PaymentDaoImp();
    List<Payment> payments;
    private static final int PAGE_SIZE = 10;

    public PaymentController() {

    }

    public List<Payment> getAllPayment() {
        return paymentDao.loadData();
    }

    public List<Payment> getPaymentsPag(int currentPage) {
        return paymentDao.loadDataPag(currentPage);
    }

    public Tenant getTenant(long paymentId) {
        return paymentDao.getTenant(paymentId);
    }

    public List<Payment> getFilterPayment(Map<String, String> filter) {
        return paymentDao.filterData(filter);
    }

    public long getPaymentCount() {
        return paymentDao.getTotalPaymentCount();
    }
}
