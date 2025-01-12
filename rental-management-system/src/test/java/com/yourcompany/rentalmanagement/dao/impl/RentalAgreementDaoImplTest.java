package com.yourcompany.rentalmanagement.dao.impl;

import com.yourcompany.rentalmanagement.model.*;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RentalAgreementDaoImplTest {

    private static RentalAgreementDaoImpl rentalAgreementDao;

    @BeforeAll
    static void setup() {
        rentalAgreementDao = new RentalAgreementDaoImpl();
    }

    @Test
    void testCreateRentalAgreement_Success() {
        RentalAgreement rentalAgreement = new RentalAgreement();
        rentalAgreement.setStartContractDate(LocalDate.of(2025, 1, 1));
        rentalAgreement.setEndContractDate(LocalDate.of(2025, 12, 31));
        rentalAgreement.setRentingFee(1500.0);
        rentalAgreement.setStatus(RentalAgreement.rentalAgreementStatus.NEW);

        Owner owner = new Owner();
        owner.setId(1L); // Assuming owner exists
        rentalAgreement.setOwner(owner);

        Host host = new Host();
        host.setId(1L); // Assuming host exists
        rentalAgreement.setHost(host);

        ResidentialProperty property = new ResidentialProperty();
        property.setId(1L); // Assuming property exists
        rentalAgreement.setResidentialProperty(property);

        List<Tenant> tenants = new ArrayList<>();
        Tenant tenant = new Tenant();
        tenant.setId(1L); // Assuming tenant exists
        tenants.add(tenant);
        rentalAgreement.setTenants(tenants);

        assertDoesNotThrow(() -> rentalAgreementDao.createRentalAgreement(rentalAgreement));
        assertNotNull(rentalAgreement.getId(), "Rental Agreement ID should be generated after persisting");
    }

    @Test
    void testCreateRentalAgreement_NoHost() {
        RentalAgreement rentalAgreement = new RentalAgreement();
        rentalAgreement.setStartContractDate(LocalDate.of(2025, 1, 1));
        rentalAgreement.setEndContractDate(LocalDate.of(2025, 12, 31));
        rentalAgreement.setRentingFee(1500.0);
        rentalAgreement.setStatus(RentalAgreement.rentalAgreementStatus.NEW);

        Owner owner = new Owner();
        owner.setId(1L); // Assuming owner exists
        rentalAgreement.setOwner(owner);

        rentalAgreement.setHost(null);

        ResidentialProperty property = new ResidentialProperty();
        property.setId(1L); // Assuming property exists
        rentalAgreement.setResidentialProperty(property);

        List<Tenant> tenants = new ArrayList<>();
        Tenant tenant = new Tenant();
        tenant.setId(1L); // Assuming tenant exists
        tenants.add(tenant);
        rentalAgreement.setTenants(tenants);

        assertDoesNotThrow(() -> rentalAgreementDao.createRentalAgreement(rentalAgreement));
        assertNotNull(rentalAgreement.getId(), "Rental Agreement ID should be generated after persisting");
    }

    @Test
    void testGetRentalAgreementById_ValidId() {
        long id = 1L; // Assuming this ID exists
        RentalAgreement rentalAgreement = rentalAgreementDao.getRentalAgreementById(id);

        assertNotNull(rentalAgreement, "Rental Agreement should not be null");
        assertEquals(id, rentalAgreement.getId(), "Rental Agreement ID should match the queried ID");
    }

    @Test
    void testGetRentalAgreementById_InvalidId() {
        long id = 9999L; // Assuming this ID does not exist
        RentalAgreement rentalAgreement = rentalAgreementDao.getRentalAgreementById(id);

        assertNull(rentalAgreement, "Rental Agreement should be null for invalid ID");
    }

    @Test
    void testGetActiveRentalAgreements_ValidDate() {
        LocalDate today = LocalDate.now();
        List<RentalAgreement> activeAgreements = rentalAgreementDao.getActiveRentalAgreements(today);

        assertNotNull(activeAgreements, "Active agreements list should not be null");
        assertFalse(activeAgreements.isEmpty(), "Active agreements list should not be empty");
        assertTrue(activeAgreements.stream()
                        .allMatch(agreement -> !agreement.getStartContractDate().isAfter(today) &&
                                !agreement.getEndContractDate().isBefore(today)),
                "All agreements should be active as of today");
    }

    @Test
    void testGetActiveRentalAgreements_NoActiveAgreements() {
        LocalDate futureDate = LocalDate.of(2100, 1, 1); // Assuming no agreements are active this far in the future
        List<RentalAgreement> activeAgreements = rentalAgreementDao.getActiveRentalAgreements(futureDate);

        assertNotNull(activeAgreements, "Active agreements list should not be null");
        assertTrue(activeAgreements.isEmpty(), "Active agreements list should be empty for a future date");
    }

    @Test
    void testUpdateRentalAgreementById_Success() {
        long id = 1L; // Assuming this ID exists
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("status", RentalAgreement.rentalAgreementStatus.ACTIVE);
        updateData.put("rentingFee", 2000.0);

        Map<String, Object> result = rentalAgreementDao.updateRentalAgreementById(id, updateData);

        assertEquals("success", result.get("status"), "Update status should be success");
    }

    @Test
    void testUpdateRentalAgreementById_InvalidId() {
        long id = 9999L; // Assuming this ID does not exist
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("status", RentalAgreement.rentalAgreementStatus.ACTIVE);

        Map<String, Object> result = rentalAgreementDao.updateRentalAgreementById(id, updateData);

        assertEquals("failed", result.get("status"), "Update status should be failed for invalid ID");
    }

    @Test
    void testDeleteRentalAgreementById_Success() {
        long id = 2L; // Assuming this ID exists
        Map<String, Object> result = rentalAgreementDao.deleteRentalAgreementById(id);

        assertEquals("success", result.get("status"), "Deletion status should be success");
    }

    @Test
    void testDeleteRentalAgreementById_InvalidId() {
        long id = 9999L; // Assuming this ID does not exist
        Map<String, Object> result = rentalAgreementDao.deleteRentalAgreementById(id);

        assertEquals("failed", result.get("status"), "Deletion status should be failed for invalid ID");
    }

    @Test
    void testGetRelatedPayments_ValidAgreements() {
        long rentalAgreementId = 1L; // Assuming this ID exists
        RentalAgreement rentalAgreement = rentalAgreementDao.getRentalAgreementById(rentalAgreementId);
        List<RentalAgreement> agreements = Collections.singletonList(rentalAgreement);

        List<Payment> payments = rentalAgreementDao.getRelatedPayments(agreements);

        assertNotNull(payments, "Payments list should not be null");
        assertFalse(payments.isEmpty(), "Payments list should not be empty");
        assertTrue(payments.stream().allMatch(payment -> payment.getRentalAgreement().getId() == rentalAgreementId),
                "All payments should be associated with the given rental agreement");
    }

    @Test
    void testGetRelatedPayments_NoAgreements() {
        List<RentalAgreement> agreements = new ArrayList<>();
        List<Payment> payments = rentalAgreementDao.getRelatedPayments(agreements);

        assertNotNull(payments, "Payments list should not be null");
        assertTrue(payments.isEmpty(), "Payments list should be empty for no agreements");
    }
}
