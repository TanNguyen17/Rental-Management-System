package com.yourcompany.rentalmanagement.dao;

import com.yourcompany.rentalmanagement.model.RentalAgreement;

import java.util.List;

public interface RentalManagementDao {
    List<RentalAgreement> getAllRentalAgreements();

    RentalAgreement getRentalAgreementById(long Id);
}