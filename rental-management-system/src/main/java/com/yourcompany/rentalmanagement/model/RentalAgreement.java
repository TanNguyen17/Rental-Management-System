package com.yourcompany.rentalmanagement.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "Rental_Agreement", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id"})})
public class RentalAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false, length = 10)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RentalAgreement.rentalAgreementStatus status;

    @Column(name = "contractDate", nullable = false)
    private LocalDate contractDate;

    @Column(name = "rentingFee", nullable = false)
    private double rentingFee;

    @ManyToOne(targetEntity = Owner.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @ManyToOne(targetEntity = Host.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "host_id", nullable = false)
    private Host host;

    @ManyToMany(mappedBy = "rentalAgreements")
    private List<Tenant> tenants = new ArrayList<>();

    @OneToOne(mappedBy = "rentalAgreement")
    private CommercialProperty commercialProperty;

    @OneToOne(mappedBy = "rentalAgreement")
    private ResidentialProperty residentialProperty;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Tenant> getTenants() {
        return tenants;
    }

    public void setTenants(List<Tenant> tenants) {
        this.tenants = tenants;
    }

    public void addTenant(Tenant tenant) {
        this.tenants.add(tenant);
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public String getOwnerName() {
        return this.owner.getUsername();
    }

    public Host getHost() {
        return host;
    }

    public String getHostName() {
        return this.host.getUsername();
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public CommercialProperty getCommercialProperty() {
        return commercialProperty;
    }

    public void setCommercialProperty(CommercialProperty commercialProperty) {
        this.commercialProperty = commercialProperty;
    }

    public ResidentialProperty getResidentialProperty() {
        return residentialProperty;
    }

    public void setResidentialProperty(ResidentialProperty residentialProperty) {
        this.residentialProperty = residentialProperty;
    }

    public rentalAgreementStatus getStatus() {
        return status;
    }

    public void setStatus(rentalAgreementStatus status) {
        this.status = status;
    }

    public LocalDate getContractDate() {
        return contractDate;
    }

    public void setContractDate(LocalDate contractDate) {
        this.contractDate = contractDate;
    }

    public double getRentingFee() {
        return rentingFee;
    }

    public void setRentingFee(double rentingFee) {
        this.rentingFee = rentingFee;
    }

    public enum rentalAgreementStatus {
        NEW,
        ACTIVE,
        COMPLETED
    }
}
