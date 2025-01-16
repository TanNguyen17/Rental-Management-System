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
import com.yourcompany.rentalmanagement.model.User;
import com.yourcompany.rentalmanagement.view.RentalAgreementCreationView;

public class RentalAgreementController {

    private RentalAgreementDaoImpl rentalAgreementDao;
    private RentalAgreementCreationView rentalAgreementCreationView;
    private Map<String, Object> result;

    public RentalAgreementController() {
        rentalAgreementDao = new RentalAgreementDaoImpl();
        result = new HashMap<>();
    }

    public List<RentalAgreement> getAllRentalAgreements(User.UserRole userRole, long userId) {

        if (userRole.equals(User.UserRole.TENANT)) {
            result = rentalAgreementDao.getRentalAgreementsByRole(userRole, userId);
            if (result.get("status").equals("success")) {
                return (List<RentalAgreement>) result.get("rentalAgreements");
            }
        } else {
            result = rentalAgreementDao.getAllRentalAgreements();
            if (result.get("status").equals("success")) {
                return (List<RentalAgreement>) result.get("rentalAgreements");
            }
        }

        return null;
    }

    public List<RentalAgreement> getActiveRentalAgreements(LocalDate today) {
        result = rentalAgreementDao.getActiveRentalAgreements(today);
        if (result.get("status").equals("success")) {
            return (List<RentalAgreement>) result.get("rentalAgreements");
        }
        return null;
    }

    public void createRentalAgreement(RentalAgreement rentalAgreement, long tenantId, Property propertyId, long ownerId, long hostId, List<Long> subTenantIds) {
        result = rentalAgreementDao.createRentalAgreement(rentalAgreement, tenantId, propertyId, ownerId, hostId, subTenantIds);
        rentalAgreementCreationView = new RentalAgreementCreationView();
        if (result.get("status") == "success") {
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
        result = rentalAgreementDao.getRentalAgreementById(id);
        if (result.get("status").equals("success")) {
            return (RentalAgreement) result.get("rentalAgreement");
        }
        return null;
    }

    public Map<String, Object> getFullRentalAgreementById(long id) {
        result = rentalAgreementDao.getFullRentalAgreement(id);
        return result;
    }
}
