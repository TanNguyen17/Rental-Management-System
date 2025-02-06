package com.yourcompany.rentalmanagement.dao;

/**
 * @author FTech
 */

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;

import com.yourcompany.rentalmanagement.model.CommercialProperty;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.ResidentialProperty;

public interface PropertyDao {

    // Basic CRUD operations
    public boolean createProperty(Property property);

    public ResidentialProperty getResidentialPropertyById(long id);

    public CommercialProperty getCommercialPropertyById(long id);

    public boolean updateProperty(Property property);

    public boolean deleteProperty(Property property);

    // Specific property type operations
    public boolean createCommercialProperty(CommercialProperty property);

    public boolean createResidentialProperty(ResidentialProperty property);

    // Query operations
    public List<Property> getAllProperties();

    public List<Property> getPropertiesByOwner(long ownerId);

    public List<Property> getPropertiesByStatus(Property.PropertyStatus status);

    public List<Property> getPropertiesAvailableForRenting(Property.PropertyStatus status);

    public List<Property> getPropertiesAvailableForRenting(Property.PropertyStatus status, Map<String, Object> filter);

    public List<CommercialProperty> getAllCommercialProperties();

    List<ResidentialProperty> getAllResidentialProperties();

    List<Property> getAllPropertiesPaginated(int page, int pageSize);

    List<Property> getAllPropertiesAfterPage(int page, int pageSize);

    List<Property> getPropertiesPage(int page, int pageSize, long ownerId);

    long getTotalPropertyCount(long ownerId);

    void loadPropertiesAsync(int page, int pageSize, long ownerId,
            Consumer<List<Property>> onSuccess,
            Consumer<Throwable> onError) throws CompletionException;

    List<Property> getPropertiesByHostID(long hostId);
}
