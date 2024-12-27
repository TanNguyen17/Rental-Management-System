package com.yourcompany.rentalmanagement.controller;

import com.yourcompany.rentalmanagement.dao.PaymentDaoImp;
import com.yourcompany.rentalmanagement.model.Payment;

import java.util.List;

public class PaymentController {
    PaymentDaoImp paymentDao = new PaymentDaoImp();
    List<Payment> payments;

    public PaymentController() {

    }

    public List<Payment> getAllPayment() {
        payments = paymentDao.loadData();
        return payments;
    }
}
