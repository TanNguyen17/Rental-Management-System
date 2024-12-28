package com.yourcompany.rentalmanagement.model;


import jakarta.persistence.*;

@MappedSuperclass
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true ,updatable = false, nullable = false, length = 10)
    private long id;

    @OneToOne(targetEntity = Address.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false ,referencedColumnName = "id")
    private Address address;

    @Column(name = "price", nullable = false)
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private propertyStatus status;

    @Column(name = "imageLink")
    private String imageLink;

    @ManyToOne(targetEntity = Owner.class, cascade = {CascadeType.ALL})
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @OneToOne(targetEntity = RentalAgreement.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "rental_agreement_id", referencedColumnName = "id", unique = true)
    private RentalAgreement rentalAgreement;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public propertyStatus getStatus() {
        return status;
    }

    public void setStatus(propertyStatus status) {
        this.status = status;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public RentalAgreement getRentalAgreement() {
        return rentalAgreement;
    }

    public void setRentalAgreement(RentalAgreement rentalAgreement) {
        this.rentalAgreement = rentalAgreement;
    }

    public enum propertyStatus {
        AVAILABLE,
        RENTED,
        UNDER_MAINTENANCE
    }
}