package com.yourcompany.rentalmanagement.dao;

import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import com.yourcompany.rentalmanagement.model.UserRole;

import java.util.List;
import java.util.Map;

public interface RentalAgreementDao {
    public List<RentalAgreement> getAllRentalAgreements();

    public List<RentalAgreement> getRentalAgreementsByRole(UserRole role, long userId);

    public Map<String, Object> createRentalAgreement(RentalAgreement rentalAgreement, long tenantId, Property property, long ownerId, long hostId, List<Long> subTenantIds);

    void loadData();

    List<RentalAgreement> getRentalAgreementsById(long id);

    void createRentalAgreement(RentalAgreement rentalAgreement);

    void updateRentalAgreement(RentalAgreement rentalAgreement);

    void deleteRentalAgreement(RentalAgreement rentalAgreement);


    RentalAgreement getRentalAgreementById(long id);

}