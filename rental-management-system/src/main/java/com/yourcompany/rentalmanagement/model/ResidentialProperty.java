package com.yourcompany.rentalmanagement.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "Residential_Property", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class ResidentialProperty extends Property {

    @Column(name = "number_of_bedrooms", nullable = false)
    private int numberOfBedrooms;

    @Column(name = "garden_availability", nullable = false)
    private boolean gardenAvailability;

    @Column(name = "pet_friendliness", nullable = false)
    private boolean petFriendliness;

    @ManyToMany(mappedBy = "residentialProperties")
    private Set<Host> hosts;

    public int getNumberOfBedrooms() {
        return numberOfBedrooms;
    }

    public void setNumberOfBedrooms(int numberOfBedrooms) {
        this.numberOfBedrooms = numberOfBedrooms;
    }

    public boolean isGardenAvailability() {
        return gardenAvailability;
    }

    public void setGardenAvailability(boolean gardenAvailability) {
        this.gardenAvailability = gardenAvailability;
    }

    public boolean isPetFriendliness() {
        return petFriendliness;
    }

    public void setPetFriendliness(boolean petFriendliness) {
        this.petFriendliness = petFriendliness;
    }

    public Set<Host> getHosts() {
        return hosts;
    }

    public void setHosts(Set<Host> hosts) {
        this.hosts = hosts;
    }
}
