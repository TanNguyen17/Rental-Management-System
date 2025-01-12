package com.yourcompany.rentalmanagement.dao.impl;

import com.yourcompany.rentalmanagement.model.*;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentDaoImplTest {

    private PaymentDaoImpl paymentDao;

    @BeforeAll
    void setup() {
        paymentDao = new PaymentDaoImpl();
    }

    @Test
    void testLoadData_WithFilter() {
        Map<String, String> filter = new HashMap<>();
        filter.put("method", "CREDIT_CARD");
        filter.put("status", "PENDING");

        List<Payment> payments = paymentDao.loadData(1, filter);

        assertNotNull(payments);
        assertTrue(payments.size() <= PaymentDaoImpl.PAGE_SIZE, "Page size exceeded");
        payments.forEach(payment -> {
            assertEquals("CREDIT_CARD", payment.getMethod());
            assertEquals("PENDING", payment.getStatus());
        });
    }

    @Test
    void testLoadData_WithoutFilter() {
        List<Payment> payments = paymentDao.loadData(1, null);

        assertNotNull(payments);
        assertTrue(payments.size() <= PaymentDaoImpl.PAGE_SIZE, "Page size exceeded");
    }

    @Test
    void testCreatePayment_Success() {
        Payment payment = new Payment();
        payment.setAmount(100.0);
        payment.setDueDate(LocalDate.now().plusDays(30));
        payment.setMethod(Payment.paymentMethod.CASH);
        payment.setStatus(Payment.paymentStatus.PAID);

        Map<String, String> result = paymentDao.createPayment(payment, 1L);

        assertNotNull(result);
        assertEquals("success", result.get("status"));
    }

    @Test
    void testCreatePayment_Failure() {
        Payment payment = new Payment();
        Map<String, String> result = paymentDao.createPayment(payment, -1L);

        assertNotNull(result);
        assertEquals("failed", result.get("status"));
    }

    @Test
    void testLoadDataByRole_Tenant() {
        Map<String, String> filter = new HashMap<>();
        filter.put("status", "PAID");

        List<Payment> payments = paymentDao.loadDataByRole(1, filter, UserRole.TENANT, 1L);

        assertNotNull(payments);
        payments.forEach(payment -> assertEquals("PAID", payment.getStatus()));
    }

    @Test
    void testGetTenant() {
        Tenant tenant = paymentDao.getTenant(1L);

        assertNotNull(tenant);
        assertEquals(1L, tenant.getId());
    }

    @Test
    void testGetLatestPayment() {
        RentalAgreement rentalAgreement = new RentalAgreement();
        rentalAgreement.setId(1L);

        Payment payment = paymentDao.getLatestPayment(rentalAgreement, LocalDate.now());

        assertNotNull(payment);
        assertEquals(rentalAgreement.getId(), payment.getRentalAgreement().getId());
    }

    @Test
    void testGetPaymentCount_WithFilter() {
        Map<String, String> filter = new HashMap<>();
        filter.put("method", "CASH");

        Long count = paymentDao.getPaymentCount(filter);

        assertNotNull(count);
        assertTrue(count >= 0, "Count cannot be negative");
    }

    @Test
    void testUpdatePaymentStatus() {
        Map<String, String> result = paymentDao.updatePaymentStatus(1L);

        assertNotNull(result);
        assertEquals("success", result.get("status"));
    }

    @Test
    void testGetMonthlyPayment_Expected() {
        List<Double> payments = paymentDao.getMonthlyPayment(1L, "expected");

        assertNotNull(payments);
        assertEquals(12, payments.size(), "Expected 12 months of data");
    }

    @Test
    void testGetMonthlyPayment_Actual() {
        List<Double> payments = paymentDao.getMonthlyPayment(1L, "actual");

        assertNotNull(payments);
        assertEquals(12, payments.size(), "Expected 12 months of data");
    }

    @Test
    void testGetMonthlyPayment_InvalidType() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentDao.getMonthlyPayment(1L, "invalid")
        );

        assertEquals("Invalid payment type. Must be 'expected' or 'actual'.", exception.getMessage());
    }
}