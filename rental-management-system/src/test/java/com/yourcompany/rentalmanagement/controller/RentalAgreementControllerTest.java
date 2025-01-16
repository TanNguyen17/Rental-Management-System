package com.yourcompany.rentalmanagement.controller;

import com.yourcompany.rentalmanagement.dao.impl.RentalAgreementDaoImpl;
import com.yourcompany.rentalmanagement.model.*;
import com.yourcompany.rentalmanagement.view.RentalAgreementCreationView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RentalAgreementControllerTest {

    private RentalAgreementController rentalAgreementController;
    private RentalAgreementDaoImpl rentalAgreementDaoMock;
    private RentalAgreementCreationView rentalAgreementCreationViewMock;
    private Map<String, Object> result;

    @BeforeEach
    void setup() {
        rentalAgreementDaoMock = mock(RentalAgreementDaoImpl.class);
        rentalAgreementCreationViewMock = mock(RentalAgreementCreationView.class);
        rentalAgreementController = new RentalAgreementController();
        result = new HashMap<>();

        // Inject mocks using reflection
        try {
            var daoField = RentalAgreementController.class.getDeclaredField("rentalAgreementDao");
            daoField.setAccessible(true);
            daoField.set(rentalAgreementController, rentalAgreementDaoMock);

            var viewField = RentalAgreementController.class.getDeclaredField("rentalAgreementCreationView");
            viewField.setAccessible(true);
            viewField.set(rentalAgreementController, rentalAgreementCreationViewMock);
        } catch (Exception e) {
            fail("Failed to set up mocks: " + e.getMessage());
        }
    }

    @Test
    void testGetAllRentalAgreementsAsTenant() {
        User.UserRole userRole = User.UserRole.TENANT;
        long userId = 1L;
        List<RentalAgreement> mockAgreements = Arrays.asList(new RentalAgreement());

        when(rentalAgreementDaoMock.getRentalAgreementsByRole(userRole, userId)).thenReturn(result);

        List<RentalAgreement> rentalAgreements = rentalAgreementController.getAllRentalAgreements(userRole, userId);

        assertNotNull(rentalAgreements);
        assertEquals(1, rentalAgreements.size());
        verify(rentalAgreementDaoMock, times(1)).getRentalAgreementsByRole(userRole, userId);
    }

    @Test
    void testGetAllRentalAgreementsAsOwner() {
        User.UserRole userRole = User.UserRole.OWNER;
        long userId = 2L;
        List<RentalAgreement> mockAgreements = Arrays.asList(new RentalAgreement(), new RentalAgreement());

        when(rentalAgreementDaoMock.getAllRentalAgreements()).thenReturn(result);

        List<RentalAgreement> rentalAgreements = rentalAgreementController.getAllRentalAgreements(userRole, userId);

        assertNotNull(rentalAgreements);
        assertEquals(2, rentalAgreements.size());
        verify(rentalAgreementDaoMock, times(1)).getAllRentalAgreements();
    }

    @Test
    void testGetActiveRentalAgreements() {
        LocalDate today = LocalDate.now();
        List<RentalAgreement> mockAgreements = Collections.singletonList(new RentalAgreement());

        when(rentalAgreementDaoMock.getActiveRentalAgreements(today)).thenReturn(result);

        List<RentalAgreement> rentalAgreements = rentalAgreementController.getActiveRentalAgreements(today);

        assertNotNull(rentalAgreements);
        assertEquals(1, rentalAgreements.size());
        verify(rentalAgreementDaoMock, times(1)).getActiveRentalAgreements(today);
    }

    @Test
    void testCreateRentalAgreementSuccess() {
        RentalAgreement rentalAgreement = new RentalAgreement();
        Property property = new ResidentialProperty();
        Map<String, Object> mockData = new HashMap<>();
        mockData.put("status", "success");

        when(rentalAgreementDaoMock.createRentalAgreement(
                eq(rentalAgreement), eq(1L), eq(property), eq(2L), eq(3L), anyList())
        ).thenReturn(mockData);

        rentalAgreementController.createRentalAgreement(rentalAgreement, 1L, property, 2L, 3L, List.of(4L));

        verify(rentalAgreementDaoMock, times(1)).createRentalAgreement(
                eq(rentalAgreement), eq(1L), eq(property), eq(2L), eq(3L), anyList()
        );
        verify(rentalAgreementCreationViewMock, times(1))
                .showSuccessAlert(eq("Rental Agreement Creation"), contains("created successfully"));
    }

    @Test
    void testCreateRentalAgreementFailure() {
        RentalAgreement rentalAgreement = new RentalAgreement();
        Property property = new CommercialProperty();
        Map<String, Object> mockData = new HashMap<>();
        mockData.put("status", "failure");

        when(rentalAgreementDaoMock.createRentalAgreement(
                eq(rentalAgreement), eq(1L), eq(property), eq(2L), eq(3L), anyList())
        ).thenReturn(mockData);

        rentalAgreementController.createRentalAgreement(rentalAgreement, 1L, property, 1L, 1L, List.of(2L));

        verify(rentalAgreementDaoMock, times(1)).createRentalAgreement(
                eq(rentalAgreement), eq(1L), eq(property), eq(2L), eq(3L), anyList()
        );
        verify(rentalAgreementCreationViewMock, times(1))
                .showSuccessAlert(eq("Rental Agreement Creation"), contains("failed to create"));
    }

    @Test
    void testUpdateRentalAgreementById() {
        long id = 1L;
        Map<String, Object> mockData = new HashMap<>();

        rentalAgreementController.updateRentalAgreementById(id, mockData);

        verify(rentalAgreementDaoMock, times(1)).updateRentalAgreementById(id, mockData);
    }

    @Test
    void testDeleteRentalAgreementById() {
        long id = 1L;

        rentalAgreementController.deleteRentalAgreementById(id);

        verify(rentalAgreementDaoMock, times(1)).deleteRentalAgreementById(id);
    }

    @Test
    void testGetRentalAgreementById() {
        long id = 1L;
        RentalAgreement mockAgreement = new RentalAgreement();

        when(rentalAgreementDaoMock.getRentalAgreementById(id)).thenReturn(result);

        mockAgreement = rentalAgreementController.getRentalAgreementById(id);

        assertNotNull(mockAgreement);
        verify(rentalAgreementDaoMock, times(1)).getRentalAgreementById(id);
    }
}
