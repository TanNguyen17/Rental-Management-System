package com.yourcompany.rentalmanagement.model;

/**
 * @author FTech
 */

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "startContractDate", nullable = false)
    private LocalDate startContractDate;

    @Column(name = "endContractDate", nullable = false)
    private LocalDate endContractDate;

    @Column(name = "rentingFee", nullable = false)
    private double rentingFee;

    @ManyToOne(
            targetEntity = Owner.class,
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @ManyToOne(
            targetEntity = Host.class,
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "host_id", nullable = false)
    private Host host;

    @ManyToMany(
            mappedBy = "rentalAgreements",
            fetch = FetchType.LAZY
    )
    private List<Tenant> tenants = new ArrayList<>();

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

    public rentalAgreementStatus getStatus() {
        return status;
    }

    public void setStatus(rentalAgreementStatus status) {
        this.status = status;
    }

    public LocalDate getStartContractDate() {
        return startContractDate;
    }

    public void setStartContractDate(LocalDate startContractDate) {
        this.startContractDate = startContractDate;
    }

    public LocalDate getEndContractDate() {
        return endContractDate;
    }

    public void setEndContractDate(LocalDate endContractDate) {
        this.endContractDate = endContractDate;
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