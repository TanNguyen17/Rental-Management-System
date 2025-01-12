package com.yourcompany.rentalmanagement.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Residential_Property")
public class ResidentialProperty extends Property {

    @Column(name = "number_of_bedrooms", nullable = false)
    private int numberOfBedrooms;

    @Column(name = "garden_availability", nullable = false)
    private boolean gardenAvailability;

    @Column(name = "pet_friendliness", nullable = false)
    private boolean petFriendliness;

    @ManyToMany(mappedBy = "residentialProperties",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Host> hosts = new ArrayList<>();

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



    @Override
    public List<Host> getHosts() {
        return hosts;
    }

    public void setHosts(List<Host> hosts) {
        this.hosts = hosts;
    }
}