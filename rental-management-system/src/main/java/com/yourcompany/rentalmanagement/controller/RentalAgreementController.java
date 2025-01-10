package com.yourcompany.rentalmanagement.controller;

import com.yourcompany.rentalmanagement.dao.impl.RentalAgreementDaoImpl;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import com.yourcompany.rentalmanagement.view.RentalAgreementCreationView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RentalAgreementController {
    private RentalAgreementDaoImpl rentalAgreementDao;
    private RentalAgreementCreationView rentalAgreementCreationView;
    private Map<String, Object> data;
    public RentalAgreementController() {
        rentalAgreementDao = new RentalAgreementDaoImpl();
        data = new HashMap<>();
    }

    public void createRentalAgreement(RentalAgreement rentalAgreement, long tenantId, Property propertyId, long ownerId, long hostId, List<Long> subTenantIds) {
        data = rentalAgreementDao.createRentalAgreement(rentalAgreement, tenantId, propertyId, ownerId, hostId, subTenantIds);
        rentalAgreementCreationView = new RentalAgreementCreationView();
        if (data.get("status") == "success") {
            rentalAgreementCreationView.showSuccessAlert("Rental Agreement Creation", "Rental Agreement has been created successfully.");
        } else {
            rentalAgreementCreationView.showSuccessAlert("Rental Agreement Creation", "Rental Agreement failed to create.");
        }
    }
}
