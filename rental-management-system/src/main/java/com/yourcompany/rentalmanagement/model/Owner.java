package com.yourcompany.rentalmanagement.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Owner", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Owner extends User {

    @ManyToMany(targetEntity = Host.class, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "Owner_Host",
            joinColumns = { @JoinColumn(name = "owner_id") },
            inverseJoinColumns = { @JoinColumn(name = "host_id") }
    )
    private Set<Host> hosts = new HashSet<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RentalAgreement> rentalAgreements = new HashSet<>();

    public Set<Host> getHosts() {
        return hosts;
    }

    public void setHosts(Set<Host> hosts) {
        this.hosts = hosts;
    }

    public void addHost(Host host) {
        hosts.add(host);
    }

    public Set<RentalAgreement> getRentalAgreements() {
        return rentalAgreements;
    }

    public void setRentalAgreements(Set<RentalAgreement> rentalAgreements) {
        this.rentalAgreements = rentalAgreements;
    }
}
