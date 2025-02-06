package com.yourcompany.rentalmanagement.controller;

import com.yourcompany.rentalmanagement.dao.impl.PropertyDaoImpl;
import com.yourcompany.rentalmanagement.model.CommercialProperty;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.ResidentialProperty;
import com.yourcompany.rentalmanagement.model.Property.PropertyStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PropertyControllerTest {

    private PropertyController propertyController;
    private PropertyDaoImpl propertyDaoMock;

    @BeforeEach
    void setup() {
        propertyDaoMock = mock(PropertyDaoImpl.class);
        propertyController = new PropertyController();

        // Inject the mocked PropertyDaoImpl into PropertyController using reflection
        try {
            var field = PropertyController.class.getDeclaredField("propertyDao");
            field.setAccessible(true);
            field.set(propertyController, propertyDaoMock);
        } catch (Exception e) {
            fail("Failed to set up mocks: " + e.getMessage());
        }
    }

    @Test
    void testGetPropertyList() {
        List<Property> mockProperties = Arrays.asList(
                new ResidentialProperty(),
                new CommercialProperty()
        );

        when(propertyDaoMock.getAllProperties()).thenReturn(mockProperties);

        List<Property> result = propertyController.getPropertyList();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0) instanceof ResidentialProperty);
        assertTrue(result.get(1) instanceof CommercialProperty);
        verify(propertyDaoMock, times(1)).getAllProperties();
    }

    @Test
    void testGetPropertyByStatus() {
        PropertyStatus status = PropertyStatus.AVAILABLE;
        Map<String, Object> filter = new HashMap<>();
        filter.put("city", "New York");

        List<Property> mockProperties = Collections.singletonList(
                new ResidentialProperty()
        );

        when(propertyDaoMock.getPropertiesAvailableForRenting(status, filter)).thenReturn(mockProperties);

        List<Property> result = propertyController.getPropertyByStatus(status, filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof ResidentialProperty);
        verify(propertyDaoMock, times(1)).getPropertiesAvailableForRenting(status, filter);
    }

    @Test
    void testGetResidentialPropertyData() {
        long propertyId = 1L;
        ResidentialProperty mockData = new ResidentialProperty();

        when(propertyDaoMock.getResidentialPropertyById(propertyId)).thenReturn(mockData);

        ResidentialProperty result = propertyController.getResidentialPropertyData(propertyId);

        assertNotNull(result);
        assertEquals(3, result.getNumberOfBedrooms());
        assertTrue(result.isPetFriendliness());
        assertTrue(result.isGardenAvailability());
        verify(propertyDaoMock, times(1)).getResidentialPropertyById(propertyId);
    }

    @Test
    void testGetCommercialPropertyData() {
        long propertyId = 2L;
        CommercialProperty mockData = new CommercialProperty();
        mockData.setBusinessType("Retail");
        mockData.setParkingSpace(true);
        mockData.setSquareFootage(2000.0);

        when(propertyDaoMock.getCommercialPropertyById(propertyId)).thenReturn(mockData);

        CommercialProperty result = propertyController.getCommercialPropertyData(propertyId);

        assertNotNull(result);
        assertEquals("Retail", result.getBusinessType());
        assertTrue(result.isParkingSpace());
        assertEquals(result.getSquareFootage(), 2000.0);
        verify(propertyDaoMock, times(1)).getCommercialPropertyById(propertyId);
    }
}
