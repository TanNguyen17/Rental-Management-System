package com.yourcompany.rentalmanagement.service;

import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;

import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.view.PaymentsView;

import javafx.application.Platform;

public class PaymentEventListener implements PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener {

    private PaymentsView paymentsView;
    private static boolean isTestEnvironment = false;

    public PaymentEventListener(PaymentsView paymentsView) {
        this.paymentsView = paymentsView;
    }

    public static void setTestEnvironment(boolean isTest) {
        isTestEnvironment = isTest;
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        if (event.getEntity() instanceof Payment) {
            handlePaymentEvent((Payment) event.getEntity());
        }
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
            if (isTestEnvironment) {
                return;
            }
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
