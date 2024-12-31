package com.yourcompany.rentalmanagement.dao;

import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.model.Tenant;

import java.util.List;
import java.util.Map;

public interface PaymentDao {
    public List<Payment> loadData();
    public List<Payment> loadDataPag(int pageNumber);
    public Tenant getTenant(long paymentId);
    public List<Payment> filterData(Map<String, String> filterValue);
    public Long getTotalPaymentCount();
}

