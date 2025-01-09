package com.yourcompany.rentalmanagement.dao;

import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.model.Tenant;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PaymentDao {
    public List<Payment> loadData(int pageNumber, Map<String, String> filterValue);
    public Tenant getTenant(long paymentId);
    public Long getPaymentCount(Map<String, String> filterValue);
}

