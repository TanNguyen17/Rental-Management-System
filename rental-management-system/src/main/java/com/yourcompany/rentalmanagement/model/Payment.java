package com.yourcompany.rentalmanagement.model;

/**
 * @author FTech
 */

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "Payment", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id"})})
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false, length = 10)
    private long id;

    @Column(name = "receipt", nullable = false, unique = true, updatable = false)
    private String receipt;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false)
    private paymentMethod method;

    @Column(name = "amount", nullable = false, length = 18)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private paymentStatus status;

    @Column(name = "dueDate", nullable = false)
    private LocalDate dueDate;

    @ManyToOne(
            targetEntity = Tenant.class,
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @OneToOne(
            targetEntity = RentalAgreement.class,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "rental_agreement_id")
    private RentalAgreement rentalAgreement;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

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

    public paymentMethod getMethod() {
        return method;
    }

    public void setMethod(paymentMethod method) {
        this.method = method;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public paymentStatus getStatus() {
        return status;
    }

    public void setStatus(paymentStatus status) {
        this.status = status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public enum paymentStatus {
        PAID,
        UNPAID,
    }

    public enum paymentMethod {
        CARD,
        CASH,
        ONLINE_WALLET
    }
}