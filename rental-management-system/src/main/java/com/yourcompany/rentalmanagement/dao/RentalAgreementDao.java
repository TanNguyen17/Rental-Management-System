package com.yourcompany.rentalmanagement.dao;

import com.yourcompany.rentalmanagement.model.RentalAgreement;

public interface RentalAgreementDao {
    public void loadData();
    public void getRentalAgreementsById(long id);
    public void createRentalAgreement(RentalAgreement rentalAgreement);
    public void updateRentalAgreement(RentalAgreement rentalAgreement);
    public void deleteRentalAgreement(RentalAgreement rentalAgreement);
}
