package com.yourcompany.rentalmanagement.model;

import jakarta.persistence.*;

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
    private Set<RentalAgreement> rentalAgreements;

    @OneToMany(mappedBy = "tenant", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH })
    private Set<Payment> payments;

    public Set<RentalAgreement> getRentalAgreements() {
        return rentalAgreements;
    }
}
