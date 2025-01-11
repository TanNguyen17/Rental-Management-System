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

    Map<String, Object> updateRentalAgreementById(long id, Map<String, Object> data);

    Map<String, Object> deleteRentalAgreementById(long id);


    RentalAgreement getRentalAgreementById(long id);

}