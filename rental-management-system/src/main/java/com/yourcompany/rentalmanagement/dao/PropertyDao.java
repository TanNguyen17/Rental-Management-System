package com.yourcompany.rentalmanagement.dao;

import java.util.List;

import com.yourcompany.rentalmanagement.model.CommercialProperty;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.ResidentialProperty;

public interface PropertyDao {

    // Basic CRUD operations
    void createProperty(Property property);

    Property getPropertyById(long id);

    void updateProperty(Property property);

    void deleteProperty(Property property);

    // Specific property type operations
    void createCommercialProperty(CommercialProperty property);

    void createResidentialProperty(ResidentialProperty property);

    // Query operations
    List<Property> getAllProperties();

    List<Property> getPropertiesByOwner(long ownerId);

    //List<Property> getPropertiesByStatus(Property.propertyStatus status);

    List<CommercialProperty> getAllCommercialProperties();

    List<ResidentialProperty> getAllResidentialProperties();
}
