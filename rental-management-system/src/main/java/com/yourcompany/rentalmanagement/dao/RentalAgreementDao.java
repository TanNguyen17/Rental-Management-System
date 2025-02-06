package com.yourcompany.rentalmanagement.dao;

/**
 * @author FTech
 */

import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import com.yourcompany.rentalmanagement.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RentalAgreementDao {
    public Map<String, Object> getAllRentalAgreements();

    public Map<String, Object> getRentalAgreementsByRole(User.UserRole role, long userId);

    public Map<String, Object> createRentalAgreement(RentalAgreement rentalAgreement, long tenantId, Property property, long ownerId, long hostId, List<Long> subTenantIds);

    public Map<String, Object> createRentalAgreement(Map<String, Object> data);

    public Map<String, Object> getRentalAgreementsByHostId(long id);

    public Map<String, Object> getFullRentalAgreement(long id);

    public Map<String, Object> createRentalAgreement(RentalAgreement rentalAgreement);

    public Map<String, Object> updateRentalAgreementById(long id, Map<String, Object> data);

    public Map<String, Object> deleteRentalAgreementById(long id);


    public Map<String, Object> getRentalAgreementById(long id);

    public Map<String, Object> getActiveRentalAgreements(LocalDate today);
}