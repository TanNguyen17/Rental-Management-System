package com.yourcompany.rentalmanagement.model;

import jakarta.persistence.*;

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
}
