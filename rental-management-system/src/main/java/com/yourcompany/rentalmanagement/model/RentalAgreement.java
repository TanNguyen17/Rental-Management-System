package com.yourcompany.rentalmanagement.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Rental_Agreement", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class RentalAgreement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false, length = 10)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RentalAgreement.rentalAgreementStatus status;

    @Column(name = "contractDate", nullable = false)
    private Date contractDate;

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

    public enum rentalAgreementStatus {
        NEW,
        ACTIVE,
        COMPLETED
    }
}
