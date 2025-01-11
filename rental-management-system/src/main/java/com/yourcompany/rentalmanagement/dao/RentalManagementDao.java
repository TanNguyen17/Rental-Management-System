package com.yourcompany.rentalmanagement.dao;

import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import com.yourcompany.rentalmanagement.model.UserRole;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RentalManagementDao {
    public List<RentalAgreement> getAllRentalAgreements();
    public List<RentalAgreement> getActiveRentalAgreements(LocalDate today);
    public List<RentalAgreement> getRentalAgreementByRole(UserRole role, Long userId);

    public Map<String, Object> createRentalAgreement(RentalAgreement rentalAgreement, long tenantId, Property property, long ownerId, long hostId, List<Long> subTenantIds);
}