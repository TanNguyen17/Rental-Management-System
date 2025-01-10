package com.yourcompany.rentalmanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinTable;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.*;

@Entity
@Table(name = "Host", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Host extends User {
    @ManyToMany(mappedBy = "hosts", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Owner> owners = new ArrayList<>();

    @ManyToMany(targetEntity = ResidentialProperty.class, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "Host_ResidentialProperty",
            joinColumns = { @JoinColumn(name = "host_id") },
            inverseJoinColumns = { @JoinColumn(name = "residential_property_id") }
    )
    private List<ResidentialProperty> residentialProperties = new ArrayList<>();

    @ManyToMany(targetEntity = CommercialProperty.class, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "Host_CommercialProperty",
            joinColumns = { @JoinColumn(name = "host_id"), },
            inverseJoinColumns = { @JoinColumn(name = "commercial_property_id") }
    )
    private List<CommercialProperty> commercialProperties;

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL)
    private List<RentalAgreement> rentalAgreements = new ArrayList<>();

    public List<Owner> getOwners() {
        return owners;
    }

    public void setOwners(List<Owner> owners) {
        this.owners = owners;
    }

    public void addOwner(Owner owner) {
        owners.add(owner);
    }

    public List<RentalAgreement> getRentalAgreements() {
        return rentalAgreements;
    }

    public void setRentalAgreements(List<RentalAgreement> rentalAgreements) {
        this.rentalAgreements = rentalAgreements;
    }

    public  List<CommercialProperty> getCommercialProperties() {
        return commercialProperties;
    }

    public void setCommercialProperties(List<CommercialProperty> commercialProperties) {
        this.commercialProperties = commercialProperties;
    }

    public void addCommercialProperty(CommercialProperty commercialProperty) {
        commercialProperties.add(commercialProperty);
    }

    public List<ResidentialProperty> getResidentialProperties() {
        return residentialProperties;
    }

    public void setResidentialProperties(List<ResidentialProperty> residentialProperties) {
        this.residentialProperties = residentialProperties;
    }

    public void addResidentialProperty(ResidentialProperty residentialProperty) {
        residentialProperties.add(residentialProperty);
    }

    @Override
    public String toString() {
        return this.getUsername();
    }
}