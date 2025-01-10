package com.yourcompany.rentalmanagement.service;

import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.view.PaymentsView;
import javafx.application.Platform;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;

public class PaymentEventListener implements PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener {
    private PaymentsView paymentsView;

    public PaymentEventListener(PaymentsView paymentsView) {
        this.paymentsView = paymentsView;
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        handlePaymentEvent(event.getEntity());
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        handlePaymentEvent(event.getEntity());
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        handlePaymentEvent(event.getEntity());
    }

    private void handlePaymentEvent(Object entity) {
        if (entity instanceof Payment) {
            Payment updatedPayment = (Payment) entity;
            Platform.runLater(() -> {
                if (updatedPayment != null) {
                    paymentsView.refreshData();
                    System.out.println("Payment updated/inserted: " + updatedPayment.getId());
                } else {
                    System.err.println("PaymentView is null. Cannot update UI.");
                }
            });
        }
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister entityPersister) {
        return false;
    }

}