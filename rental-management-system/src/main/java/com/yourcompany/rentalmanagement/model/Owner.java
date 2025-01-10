package com.yourcompany.rentalmanagement.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "Owner", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id"})})
public class Owner extends User {

    @ManyToMany(targetEntity = Host.class, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "Owner_Host",
            joinColumns = {
                @JoinColumn(name = "owner_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "host_id")}
    )
    private List<Host> hosts = new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RentalAgreement> rentalAgreements = new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<ResidentialProperty> residentialProperties = new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<CommercialProperty> commercialProperties = new ArrayList<>();

    public List<Host> getHosts() {
        return hosts;
    }

    public void setHosts(List<Host> hosts) {
        this.hosts = hosts;
    }

    public void addHost(Host host) {
        hosts.add(host);
    }

    public List<RentalAgreement> getRentalAgreements() {
        return rentalAgreements;
    }

    public void setRentalAgreements(List<RentalAgreement> rentalAgreements) {
        this.rentalAgreements = rentalAgreements;
    }

    public void addRentalAgreement(RentalAgreement rentalAgreement) {
        rentalAgreements.add(rentalAgreement);
    }

    public List<ResidentialProperty> getResidentialProperties() {
        return residentialProperties;
    }

    public void setResidentialProperties(List<ResidentialProperty> residentialProperties) {
        this.residentialProperties = residentialProperties;
    }

    public List<CommercialProperty> getCommercialProperties() {
        return commercialProperties;
    }

    public void setCommercialProperties(List<CommercialProperty> commercialProperties) {
        this.commercialProperties = commercialProperties;
    }

    public void addResidentialProperty(ResidentialProperty property) {
        if (this.residentialProperties == null) {
            this.residentialProperties = new ArrayList<>();
        }
        this.residentialProperties.add(property);
    }

    public void addCommercialProperty(CommercialProperty property) {
        if (this.commercialProperties == null) {
            this.commercialProperties = new ArrayList<>();
        }
        this.commercialProperties.add(property);
    }
}

// // Old Code from Tan
// package com.yourcompany.rentalmanagement.model;

// import java.util.ArrayList;
// import java.util.List;

// import jakarta.persistence.CascadeType;
// import jakarta.persistence.Entity;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.JoinTable;
// import jakarta.persistence.ManyToMany;
// import jakarta.persistence.OneToMany;
// import jakarta.persistence.Table;
// import jakarta.persistence.UniqueConstraint;

// @Entity
// @Table(name = "Owner", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
// public class Owner extends User {

//     @ManyToMany(targetEntity = Host.class, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
//     @JoinTable(
//             name = "Owner_Host",
//             joinColumns = { @JoinColumn(name = "owner_id") },
//             inverseJoinColumns = { @JoinColumn(name = "host_id") },
//             uniqueConstraints = { @UniqueConstraint(columnNames = {"owner_id", "host_id"}) }
//     )
//     private List<Host> hosts = new ArrayList<>();

//     @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
//     private List<RentalAgreement> rentalAgreements = new ArrayList<>();

//     public List<Host> getHosts() {
//         return hosts;
//     }

//     public void setHosts(List<Host> hosts) {
//         this.hosts = hosts;
//     }

//     public void addHost(Host host) {
//         hosts.add(host);
//     }

//     public List<RentalAgreement> getRentalAgreements() {
//         return rentalAgreements;
//     }

//     public void setRentalAgreements(List<RentalAgreement> rentalAgreements) {
//         this.rentalAgreements = rentalAgreements;
//     }

//     public void addRentalAgreement(RentalAgreement rentalAgreement) {
//         rentalAgreements.add(rentalAgreement);
//     }
// }
