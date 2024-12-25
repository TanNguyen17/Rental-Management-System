package com.yourcompany.rentalmanagement.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Commercial_Property", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class CommercialProperty extends Property {
    @Column(name = "business_type", nullable = false)
    private String businessType;

    @Column(name = "parking_space", nullable = false)
    private boolean parkingSpace;

    @Column(name = "square_footage", nullable = false)
    private double squareFootage;

    @ManyToMany(mappedBy = "commercialProperties")
    private List<Host> hosts;

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

    public List<Host> getHosts() {
        return hosts;
    }

    public void setHosts(List<Host> hosts) {
        this.hosts = hosts;
    }

    public void addHost(Host host) {
        hosts.add(host);
    }
}
