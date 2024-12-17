package com.yourcompany.rentalmanagement.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Owner", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Owner extends User {

    @ManyToMany(targetEntity = Host.class, cascade = {CascadeType.ALL})
    @JoinTable(
            name = "Owner_Host",
            joinColumns = { @JoinColumn(name = "owner_id") },
            inverseJoinColumns = { @JoinColumn(name = "host_id") }
    )
    private Set<Host> hosts;

    public Set<Host> getHosts() {
        return hosts;
    }

    public void setHosts(Set<Host> hosts) {
        this.hosts = hosts;
    }
}
