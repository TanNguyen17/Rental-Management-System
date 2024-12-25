package com.yourcompany.rentalmanagement.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Tenant", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Tenant extends User{
    @ManyToMany(targetEntity = RentalAgreement.class, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "Tenant_RentalAgreement",
            joinColumns = { @JoinColumn(name = "tenant_id") },
            inverseJoinColumns = { @JoinColumn(name = "rental_agreement_id") }
    )
    private List<RentalAgreement> rentalAgreements = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH })
    private List<Payment> payments = new ArrayList<>();

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
}
