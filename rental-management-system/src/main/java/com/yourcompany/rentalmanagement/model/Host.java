package com.yourcompany.rentalmanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.ManyToMany;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Host", uniqueConstraints = {@UniqueConstraint(columnNames = {"hostID"})})
public class Host extends User {
    @ManyToMany(mappedBy = "hosts")
    private Set<Owner> owners = new HashSet<>();

    public Set<Owner> getOwners() {
        return owners;
    }

    public void setOwners(Set<Owner> owners) {
        this.owners = owners;
    }
}
