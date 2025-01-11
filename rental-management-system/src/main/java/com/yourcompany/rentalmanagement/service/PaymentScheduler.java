package com.yourcompany.rentalmanagement.service;

import com.yourcompany.rentalmanagement.controller.PaymentController;
import com.yourcompany.rentalmanagement.controller.RentalAgreementController;
import com.yourcompany.rentalmanagement.dao.impl.RentalAgreementDaoImpl;
import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.model.RentalAgreement;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PaymentScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private RentalAgreementController rentalAgreementController = new RentalAgreementController();
    private PaymentController paymentController = new PaymentController();

    public void startPaymentGeneration() {
        Runnable paymentGenerator = () -> {
            LocalDate today = LocalDate.now();
            List<RentalAgreement> activeRentalAgreement = rentalAgreementController.getActiveRentalAgreements(today);
            for (RentalAgreement rentalAgreement : activeRentalAgreement) {
                if (paymentController.shouldGeneratePayment(rentalAgreement, today)) {
                    // Create payment
                    Payment payment = createPayment(rentalAgreement, today);
                    paymentController.createPayment(payment, rentalAgreement.getId(), rentalAgreement.getTenants().get(0).getId());
                }
            }
        };

        long initialDelay = calculateInitialDelay();
        scheduler.scheduleAtFixedRate(paymentGenerator, initialDelay, 24, TimeUnit.HOURS);
    }

    private long calculateInitialDelay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMidnight = LocalDate.now().plusDays(1).atStartOfDay();
        return ChronoUnit.SECONDS.between(now, nextMidnight);
    }

    private Payment createPayment(RentalAgreement agreement, LocalDate dueDate) {
        Payment payment = new Payment();
        payment.setReceipt("RECURRING-" + agreement.getId() + "-" + dueDate); // Unique receipt
        payment.setMethod("AUTOMATIC");
        payment.setAmount(agreement.getRentingFee());
        payment.setStatus(Payment.paymentStatus.UNPAID);
        payment.setDueDate(dueDate);
//        payment.setTenant(agreement.getTenants().get(0));
//        payment.setRentalAgreement(agreement);
        return payment;
    }
}
