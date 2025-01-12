package com.yourcompany.rentalmanagement.controller;

/**
 * @author FTech
 */

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yourcompany.rentalmanagement.dao.impl.RentalAgreementDaoImpl;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import com.yourcompany.rentalmanagement.model.UserRole;
import com.yourcompany.rentalmanagement.view.RentalAgreementCreationView;

public class RentalAgreementController {

    private RentalAgreementDaoImpl rentalAgreementDao;
    private RentalAgreementCreationView rentalAgreementCreationView;
    private Map<String, Object> data;

    public RentalAgreementController() {
        rentalAgreementDao = new RentalAgreementDaoImpl();
        data = new HashMap<>();
    }

    public List<RentalAgreement> getAllRentalAgreements(UserRole userRole, long userId) {
        if (userRole.equals(UserRole.TENANT)) {
            return rentalAgreementDao.getRentalAgreementsByRole(userRole, userId);
        } else {
            return rentalAgreementDao.getAllRentalAgreements();
        }
    }

    public List<RentalAgreement> getActiveRentalAgreements(LocalDate today) {
        return rentalAgreementDao.getActiveRentalAgreements(today);
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

    public void createRentalAgreement(Map<String, Object> data) {
        rentalAgreementDao.createRentalAgreement(data);
    }

    public void updateRentalAgreementById(long id, Map<String, Object> data) {
        rentalAgreementDao.updateRentalAgreementById(id, data);
    }

    public boolean deleteRentalAgreementById(long id) {
        Map<String, Object> result = rentalAgreementDao.deleteRentalAgreementById(id);
        return "success".equals(result.get("status"));
    }

    public RentalAgreement getRentalAgreementById(long id) {
        return rentalAgreementDao.getRentalAgreementById(id);
    }


}
