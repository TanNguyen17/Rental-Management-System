package com.yourcompany.rentalmanagement.model;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ManyToOne;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Rental_Agreement", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class RentalAgreement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false, length = 10)
    private long id;

    @ManyToOne(targetEntity = Owner.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @ManyToOne(targetEntity = Host.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "host_id", nullable = false)
    private Host host;

    @ManyToMany(mappedBy = "rentalAgreements")
    private Set<Tenant> tenants;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<Tenant> getTenants() {
        return tenants;
    }

    public void setTenants(Set<Tenant> tenants) {
        if (tenants == null) {
            this.tenants = new HashSet<>();
        }
        this.tenants.addAll(tenants);
    }

    public void addTenant(Tenant tenant) {
        if (tenant == null) {
            this.tenants = new HashSet<>();
        }
        this.tenants.add(tenant);
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }
}
