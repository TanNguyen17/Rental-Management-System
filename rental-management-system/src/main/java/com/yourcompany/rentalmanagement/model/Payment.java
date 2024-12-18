package com.yourcompany.rentalmanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Payment", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false, length = 10)
    private long id;

    @ManyToOne(targetEntity = Tenant.class)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @OneToOne(targetEntity = RentalAgreement.class)
    @JoinColumn(name = "rental_agreement_id", nullable = false)
    private RentalAgreement rentalAgreement;
}
