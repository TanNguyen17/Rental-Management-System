package com.yourcompany.rentalmanagement.controller;

import com.yourcompany.rentalmanagement.dao.RentalAgreementDao;
import com.yourcompany.rentalmanagement.dao.impl.RentalAgreementDaoImpl;
import com.yourcompany.rentalmanagement.model.RentalAgreement;

import java.util.ArrayList;
import java.util.List;

public class RentalAgreementController {
    RentalAgreementDaoImpl rentalAgreementDao = new RentalAgreementDaoImpl();

    // List<RentalAgreement> rentalAgreements = new ArrayList<>();

    public List<RentalAgreement> getAllRentalAgreements(){
        return rentalAgreementDao.getAllRentalAgreements();
    }

    public RentalAgreement getRentalAgreementById(long id){
        return rentalAgreementDao.getRentalAgreementById(id);
    }
}