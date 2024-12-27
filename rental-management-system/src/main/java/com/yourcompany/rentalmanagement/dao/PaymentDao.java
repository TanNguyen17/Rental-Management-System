package com.yourcompany.rentalmanagement.dao;

import com.yourcompany.rentalmanagement.model.Payment;

import java.util.List;

public interface PaymentDao {
    public List<Payment> loadData();
}
