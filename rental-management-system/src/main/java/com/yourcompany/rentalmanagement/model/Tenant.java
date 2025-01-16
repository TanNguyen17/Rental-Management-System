package com.yourcompany.rentalmanagement.model;

/**
 * @author FTech
 */

import java.util.ArrayList;
import java.util.List;

import com.yourcompany.rentalmanagement.service.UserEventListener;
import jakarta.persistence.*;

//@EntityListeners(UserEventListener.class)
@Entity
@Table(name = "Tenant", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id"})})
public class Tenant extends User {

    @ManyToMany(
            targetEntity = RentalAgreement.class,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "Tenant_RentalAgreement",
            joinColumns = {
                @JoinColumn(name = "tenant_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "rental_agreement_id")},
            uniqueConstraints = {
                @UniqueConstraint(columnNames = {"tenant_id, rental_agreement_id"})}
    )
    private List<RentalAgreement> rentalAgreements = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private List<Payment> payments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "paymentMethod")
    private Payment.paymentMethod paymentMethod;

    public List<RentalAgreement> getRentalAgreements() {
        return rentalAgreements;
    }

    public void setRentalAgreements(List<RentalAgreement> rentalAgreements) {
        this.rentalAgreements = rentalAgreements;
    }

    public void addRentalAgreement(RentalAgreement rentalAgreement) {
        rentalAgreements.add(rentalAgreement);
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public Payment.paymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Payment.paymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}