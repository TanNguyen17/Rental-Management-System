package com.yourcompany.rentalmanagement.controller;

import com.yourcompany.rentalmanagement.dao.impl.PropertyDaoImpl;
import com.yourcompany.rentalmanagement.model.CommercialProperty;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.ResidentialProperty;
import com.yourcompany.rentalmanagement.model.Property.propertyStatus;
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
        propertyStatus status = propertyStatus.AVAILABLE;
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
        Map<String, Object> mockData = new HashMap<>();
        mockData.put("type", "residential");
        mockData.put("number_of_bedrooms", 3);
        mockData.put("garden_availability", true);
        mockData.put("pet_friendliness", true);

        when(propertyDaoMock.getResidentialPropertyById(propertyId)).thenReturn(mockData);

        Map<String, Object> result = propertyController.getResidentialPropertyData(propertyId);

        assertNotNull(result);
        assertEquals("residential", result.get("type"));
        assertEquals(3, result.get("number_of_bedrooms"));
        assertTrue((boolean) result.get("garden_availability"));
        assertTrue((boolean) result.get("pet_friendliness"));
        verify(propertyDaoMock, times(1)).getResidentialPropertyById(propertyId);
    }

    @Test
    void testGetCommercialPropertyData() {
        long propertyId = 2L;
        Map<String, Object> mockData = new HashMap<>();
        mockData.put("type", "commercial");
        mockData.put("business_type", "Retail");
        mockData.put("parking_space", true);
        mockData.put("square_footage", 2000.0);

        when(propertyDaoMock.getCommercialPropertyById(propertyId)).thenReturn(mockData);

        Map<String, Object> result = propertyController.getCommercialPropertyData(propertyId);

        assertNotNull(result);
        assertEquals("commercial", result.get("type"));
        assertEquals("Retail", result.get("business_type"));
        assertTrue((boolean) result.get("parking_space"));
        assertEquals(2000.0, result.get("square_footage"));
        verify(propertyDaoMock, times(1)).getCommercialPropertyById(propertyId);
    }
}
