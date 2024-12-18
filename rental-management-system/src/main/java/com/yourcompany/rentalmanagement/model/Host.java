package com.yourcompany.rentalmanagement.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Host", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Host extends User {
    @ManyToMany(mappedBy = "hosts", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Owner> owners;

    @ManyToMany(targetEntity = ResidentialProperty.class, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "Host_ResidentialProperty",
            joinColumns = { @JoinColumn(name = "host_id") },
            inverseJoinColumns = { @JoinColumn(name = "residential_property_id") }
    )
    private Set<ResidentialProperty> residentialProperties;

    @ManyToMany(targetEntity = CommercialProperty.class, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "Host_CommercialProperty",
            joinColumns = { @JoinColumn(name = "host_id"), },
            inverseJoinColumns = { @JoinColumn(name = "commercial_property_id") }
    )
    private Set<CommercialProperty> commercialProperties;

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL)
    private Set<RentalAgreement> rentalAgreements;

    public Set<Owner> getOwners() {
        return owners;
    }

    public void setOwners(Set<Owner> owners) {
        if (owners == null) {
            this.owners = new HashSet<>();
        }
        this.owners.addAll(owners);
    }

    public void addOwner(Owner owner) {
        if (owners == null) {
            owners = new HashSet<>();
        }
        owners.add(owner);
    }

    public Set<RentalAgreement> getRentalAgreements() {
        return rentalAgreements;
    }

    public void setRentalAgreements(Set<RentalAgreement> rentalAgreements) {
        this.rentalAgreements = rentalAgreements;
    }

    public Set<CommercialProperty> getCommercialProperties() {
        return commercialProperties;
    }

    public void setCommercialProperties(Set<CommercialProperty> commercialProperties) {
        this.commercialProperties = commercialProperties;
    }

    public Set<ResidentialProperty> getResidentialProperties() {
        return residentialProperties;
    }

    public void setResidentialProperties(Set<ResidentialProperty> residentialProperties) {
        this.residentialProperties = residentialProperties;
    }
}
