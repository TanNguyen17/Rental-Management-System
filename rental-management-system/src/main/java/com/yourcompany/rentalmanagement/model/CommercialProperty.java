package com.yourcompany.rentalmanagement.model;

/**
 * @author FTech
 */

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "Commercial_Property", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id"})})
public class CommercialProperty extends Property {
    @Column(name = "business_type", nullable = false)
    private String businessType;

    @Column(name = "parking_space", nullable = false)
    private boolean parkingSpace;

    @Column(name = "square_footage", nullable = false)
    private double squareFootage;

    @ManyToMany(
            mappedBy = "commercialProperties",
            fetch = FetchType.EAGER
    )
    private List<Host> hosts = new ArrayList<>();


    public CommercialProperty() {
        this.hosts = new ArrayList<>();
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public boolean isParkingSpace() {
        return parkingSpace;
    }

    public void setParkingSpace(boolean parkingSpace) {
        this.parkingSpace = parkingSpace;
    }

    public double getSquareFootage() {
        return squareFootage;
    }

    public void setSquareFootage(double squareFootage) {
        this.squareFootage = squareFootage;
    }

    @Override
    public List<Host> getHosts() {
        return hosts;
    }

    public void setHosts(List<Host> hosts) {
        this.hosts = hosts;
    }

    public void addHost(Host host) {
        if (this.hosts == null) {
            this.hosts = new ArrayList<>();
        }
        hosts.add(host);
    }
}
