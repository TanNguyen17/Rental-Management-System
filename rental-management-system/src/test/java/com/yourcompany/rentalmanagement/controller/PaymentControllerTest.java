package com.yourcompany.rentalmanagement.controller;

import com.yourcompany.rentalmanagement.dao.impl.PaymentDaoImpl;
import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import com.yourcompany.rentalmanagement.model.Tenant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentControllerTest {

    @InjectMocks
    private PaymentController paymentController;

    @Mock
    private PaymentDaoImpl paymentDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePayment_Success() {
        Payment payment = new Payment();
        long rentalAgreementId = 1L;
        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("status", "success");

        when(paymentDao.createPayment(payment, rentalAgreementId)).thenReturn(successResponse);

        assertTrue(paymentController.createPayment(payment, rentalAgreementId));
        verify(paymentDao, times(1)).createPayment(payment, rentalAgreementId);
    }

    @Test
    void testCreatePayment_Failure() {
        Payment payment = new Payment();
        long rentalAgreementId = 1L;
        Map<String, String> failureResponse = new HashMap<>();
        failureResponse.put("status", "failed");

        when(paymentDao.createPayment(payment, rentalAgreementId)).thenReturn(failureResponse);

        assertFalse(paymentController.createPayment(payment, rentalAgreementId));
        verify(paymentDao, times(1)).createPayment(payment, rentalAgreementId);
    }

    @Test
    void testGetPayments() {
        int pageNumber = 1;
        Map<String, String> filterValue = new HashMap<>();
        List<Payment> payments = Collections.emptyList();

        when(paymentDao.loadDataByRole(pageNumber, filterValue, null, 1)).thenReturn(payments);

        assertEquals(payments, paymentController.getPayments(pageNumber, filterValue));
        verify(paymentDao, times(1)).loadDataByRole(pageNumber, filterValue, null, 1);
    }

    @Test
    void testShouldGeneratePayment() {
        RentalAgreement rentalAgreement = new RentalAgreement();
        LocalDate today = LocalDate.now();
        Payment latestPayment = new Payment();
        latestPayment.setDueDate(today.minusMonths(1));

        when(paymentDao.getLatestPayment(rentalAgreement, today)).thenReturn(latestPayment);

        assertTrue(paymentController.shouldGeneratePayment(rentalAgreement, today));
        verify(paymentDao, times(1)).getLatestPayment(rentalAgreement, today);
    }

    @Test
    void testShouldGeneratePayment_NoLatestPayment() {
        RentalAgreement rentalAgreement = new RentalAgreement();
        LocalDate today = LocalDate.now();

        when(paymentDao.getLatestPayment(rentalAgreement, today)).thenReturn(null);

        assertTrue(paymentController.shouldGeneratePayment(rentalAgreement, today));
        verify(paymentDao, times(1)).getLatestPayment(rentalAgreement, today);
    }

    @Test
    void testGetTenant() {
        long paymentId = 1L;
        Tenant tenant = new Tenant();

        when(paymentDao.getTenant(paymentId)).thenReturn(tenant);

        assertEquals(tenant, paymentController.getTenant(paymentId));
        verify(paymentDao, times(1)).getTenant(paymentId);
    }

    @Test
    void testGetPaymentCount() {
        Map<String, String> filterValue = new HashMap<>();
        long count = 10L;

        when(paymentDao.getPaymentCount(filterValue)).thenReturn(count);

        assertEquals(count, paymentController.getPaymentCount(filterValue));
        verify(paymentDao, times(1)).getPaymentCount(filterValue);
    }

    @Test
    void testChangePaymentStatus_Success() {
        long paymentId = 1L;
        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("status", "success");

        when(paymentDao.updatePaymentStatus(paymentId)).thenReturn(successResponse);

        assertTrue(paymentController.changePaymentStatus(paymentId));
        verify(paymentDao, times(1)).updatePaymentStatus(paymentId);
    }

    @Test
    void testChangePaymentStatus_Failure() {
        long paymentId = 1L;
        Map<String, String> failureResponse = new HashMap<>();
        failureResponse.put("status", "failed");

        when(paymentDao.updatePaymentStatus(paymentId)).thenReturn(failureResponse);

        assertFalse(paymentController.changePaymentStatus(paymentId));
        verify(paymentDao, times(1)).updatePaymentStatus(paymentId);
    }
}