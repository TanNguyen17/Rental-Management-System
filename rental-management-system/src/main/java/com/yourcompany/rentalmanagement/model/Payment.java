package com.yourcompany.rentalmanagement.model;

import jakarta.persistence.*;
import javafx.fxml.FXML;

import java.time.LocalDate;

@Entity
@Table(name = "Payment", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false, length = 10)
    private long id;

    @Column(name = "receipt", nullable = false, unique = true, updatable = false)
    private String receipt;

    @Column(name = "method", nullable = false)
    private String method;

    @Column(name = "amount", nullable = false, unique = true, updatable = false, length = 10)
    private double amount;

    @Column(name = "status", nullable = false, length = 10)
    private String status;

    @ManyToOne(targetEntity = Tenant.class)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @OneToOne(targetEntity = RentalAgreement.class)
    @JoinColumn(name = "rental_agreement_id")
    private RentalAgreement rentalAgreement;

    public Payment(long id, String receipt, String method, double amount, String status, LocalDate paymentDate) {
        this.id = id;
        this.receipt = receipt;
        this.method = method;
        this.amount = amount;
        this.status = status;
        this.tenant = null;
        this.rentalAgreement = null;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public RentalAgreement getRentalAgreement() {
        return rentalAgreement;
    }

    public void setRentalAgreement(RentalAgreement rentalAgreement) {
        this.rentalAgreement = rentalAgreement;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", receipt='" + receipt + '\'' +
                ", method='" + method + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", tenant=" + (tenant != null ? tenant.getId() : "null") +
                ", rentalAgreement=" + (rentalAgreement != null ? rentalAgreement.getId() : "null") +
                '}';
    }
}

