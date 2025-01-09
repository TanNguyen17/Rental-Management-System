package com.yourcompany.rentalmanagement.dao;

import java.util.List;
import java.util.Map;

import com.yourcompany.rentalmanagement.model.CommercialProperty;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.ResidentialProperty;

public interface PropertyDao {

    // Basic CRUD operations
    public void createProperty(Property property);

    public Map<String, Object> getResidentialPropertyById(long id);

    public Map<String, Object> getCommercialPropertyById(long id);

    public void updateProperty(Property property);

    public void deleteProperty(Property property);

    // Specific property type operations
    public void createCommercialProperty(CommercialProperty property);

    public void createResidentialProperty(ResidentialProperty property);

    // Query operations
    public List<Property> getAllProperties();

    public List<Property> getPropertiesByOwner(long ownerId);

    public List<Property> getPropertiesAvailableForRenting(Property.propertyStatus status);

    public List<CommercialProperty> getAllCommercialProperties();

    public List<ResidentialProperty> getAllResidentialProperties();
}
