package com.yourcompany.rentalmanagement.controller;

import com.yourcompany.rentalmanagement.dao.impl.PropertyDaoImpl;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.view.RentalAgreementCreationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyController {
    private List<Property> propertyList;
    private PropertyDaoImpl propertyDao;
    private Map<String, Object> data = new HashMap<>();
    private RentalAgreementCreationView rentalAgreementCreationView ;

    public PropertyController() {
        propertyDao = new PropertyDaoImpl();
        propertyList = new ArrayList<>();
    }

    public List<Property> getPropertyList() {
        return propertyDao.getAllProperties();
    }

    public List<Property> getPropertyByStatus(Property.propertyStatus propertyStatus) {
        return propertyDao.getPropertiesAvailableForRenting(propertyStatus);
    }

    public Map<String, Object> getResidentialPropertyData(long propertyId) {
        return propertyDao.getResidentialPropertyById(propertyId);
    }

    public Map<String, Object>  getCommercialPropertyData(long propertyId) {
        return propertyDao.getCommercialPropertyById(propertyId);
    }
}
