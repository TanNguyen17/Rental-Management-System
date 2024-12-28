package com.yourcompany.rentalmanagement.dao;

import com.yourcompany.rentalmanagement.model.RentalAgreement;

public interface RentalAgreementDao {
    public void loadData();
    public void createRentalAgreement(RentalAgreement rentalAgreement);
}
