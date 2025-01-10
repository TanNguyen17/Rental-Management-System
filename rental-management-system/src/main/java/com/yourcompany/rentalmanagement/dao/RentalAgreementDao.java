package com.yourcompany.rentalmanagement.dao;

import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.RentalAgreement;

import java.util.List;
import java.util.Map;

public interface RentalAgreementDao {

    void loadData();

    List<RentalAgreement> getRentalAgreementsById(long id);

    void createRentalAgreement(RentalAgreement rentalAgreement);

    void updateRentalAgreement(RentalAgreement rentalAgreement);

    void deleteRentalAgreement(RentalAgreement rentalAgreement);
}