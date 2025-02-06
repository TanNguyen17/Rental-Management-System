package com.yourcompany.rentalmanagement.dao.impl;
/**
 * @author FTech
 */
import com.yourcompany.rentalmanagement.model.*;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import org.hibernate.Session;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PropertyDaoImplTest {

    private static PropertyDaoImpl propertyDao;

    @BeforeAll
    static void setUp() {
        propertyDao = new PropertyDaoImpl();
    }

    @Test
    @Order(1)
    void testCreateResidentialProperty() {
        ResidentialProperty property = new ResidentialProperty();
        property.setTitle("Luxury Apartment");
        property.setDescription("A spacious luxury apartment");
        property.setPrice(1500000.0);
        property.setStatus(Property.PropertyStatus.AVAILABLE);
        property.setCreatedAt(LocalDateTime.now());
        property.setLastUpdated(LocalDateTime.now());

        // Simulate adding an owner
        Owner owner = new Owner();
        owner.setId(80L); // Assume an owner with ID 1 exists in the database
        property.setOwner(owner);

        assertDoesNotThrow(() -> propertyDao.createResidentialProperty(property));
        assertNotNull(property.getId(), "Property ID should be generated after persisting");
    }

    @Test
    @Order(2)
    void testCreateCommercialProperty() {
        CommercialProperty property = new CommercialProperty();
        property.setTitle("Downtown Office");
        property.setDescription("Prime location office space");
        property.setPrice(5000000.0);
        property.setStatus(Property.PropertyStatus.AVAILABLE);
        property.setCreatedAt(LocalDateTime.now());
        property.setLastUpdated(LocalDateTime.now());

        // Simulate adding an owner
        Owner owner = new Owner();
        owner.setId(1L); // Assume an owner with ID 1 exists in the database
        property.setOwner(owner);

        assertDoesNotThrow(() -> propertyDao.createCommercialProperty(property));
        assertNotNull(property.getId(), "Property ID should be generated after persisting");
    }

    @Test
    @Order(3)
    void testUpdateProperty() {
        ResidentialProperty property = new ResidentialProperty();
        property.setId(1L); // Assume a property with ID 1 exists in the database
        property.setTitle("Updated Luxury Apartment");
        property.setDescription("Updated description for luxury apartment");
        property.setPrice(1600000.0);
        property.setLastUpdated(LocalDateTime.now());

        assertDoesNotThrow(() -> propertyDao.updateProperty(property));
    }

    @Test
    @Order(4)
    void testDeleteProperty() {
        ResidentialProperty property = new ResidentialProperty();
        assertDoesNotThrow(() -> propertyDao.deleteProperty(property));
    }

    @Test
    @Order(5)
    void testGetResidentialPropertyById() {
        long propertyId = 22; // Assume a property with this ID exists
        ResidentialProperty propertyData = propertyDao.getResidentialPropertyById(propertyId);

        assertNotNull(propertyData, "Property data should not be null");
        assertEquals(propertyId, propertyData.getId(), "IDs should match");
    }

    @Test
    @Order(6)
    void testGetCommercialPropertyById() {
        long propertyId = 29; // Assume a property with this ID exists
        CommercialProperty propertyData = propertyDao.getCommercialPropertyById(propertyId);

        assertNotNull(propertyData, "Property data should not be null");
        assertEquals(propertyId, propertyData.getId(), "IDs should match");
    }

    @Test
    @Order(7)
    void testGetPropertiesByOwner() {
        long ownerId = 73; // Assume an owner with this ID exists
        List<Property> properties = propertyDao.getPropertiesByOwner(ownerId);

        assertNotNull(properties, "Properties list should not be null");
        assertFalse(properties.isEmpty(), "Properties list should not be empty");
    }

    @Test
    @Order(8)
    void testCalculateTotalIncomeByProperty() {
        long hostId = 19; // Assume a host with this ID exists
        Map<Long, Double> incomeByProperty = propertyDao.calculateTotalIncomeByProperty(hostId);

        assertNotNull(incomeByProperty, "Income map should not be null");
        assertFalse(incomeByProperty.isEmpty(), "Income map should not be empty");
    }

    @Test
    @Order(9)
    void testGetPropertiesAvailableForRenting() {
        Property.PropertyStatus status = Property.PropertyStatus.AVAILABLE;
        List<Property> properties = propertyDao.getPropertiesAvailableForRenting(status);

        assertNotNull(properties, "Properties list should not be null");
        assertFalse(properties.isEmpty(), "Properties list should not be empty");
        assertTrue(properties.stream().allMatch(p -> p.getStatus() == status), "All properties should have the specified status");
    }
}