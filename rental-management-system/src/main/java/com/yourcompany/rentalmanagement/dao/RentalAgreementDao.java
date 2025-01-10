package com.yourcompany.rentalmanagement.dao;

import com.yourcompany.rentalmanagement.model.RentalAgreement;

import java.util.List;

public interface RentalAgreementDao {
    public void loadData();
    public List<RentalAgreement> getRentalAgreementsById(long id);
    public void createRentalAgreement(RentalAgreement rentalAgreement);
    public void updateRentalAgreement(RentalAgreement rentalAgreement);
    public void deleteRentalAgreement(RentalAgreement rentalAgreement);
}
