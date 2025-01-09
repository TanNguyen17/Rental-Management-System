package com.yourcompany.rentalmanagement.dao;

import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.RentalAgreement;

import java.util.List;
import java.util.Map;

public interface RentalAgreementDao {
    public List<RentalAgreement> getAllRentalAgreements();
    public Map<String, Object> createRentalAgreement(RentalAgreement rentalAgreement, long tenantId, Property property, long ownerId, long hostId, List<Long> subTenantIds);
}
