package com.yourcompany.rentalmanagement.dao;

/**
 * @author FTech
 */

import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import com.yourcompany.rentalmanagement.model.Tenant;
import com.yourcompany.rentalmanagement.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PaymentDao {
    public List<Payment> loadData(int pageNumber, Map<String, String> filterValue);
    public Map<String, String> createPayment(Payment payment, long rentalAgreementId);
    public List<Payment> loadDataByRole(int pageNumber, Map<String, String> filterValue, User.UserRole userRole, long userId);
    public Tenant getTenant(long paymentId);
    public Payment getLatestPayment(RentalAgreement rentalAgreement, LocalDate today);
    public Long getPaymentCountByRole(Map<String, String> filterValue, User.UserRole userRole, long userId);
    public Map<String, String> updatePaymentStatus(long paymentId);
    public List<Payment> getAllPaidPayments(Payment.paymentStatus status);
}

