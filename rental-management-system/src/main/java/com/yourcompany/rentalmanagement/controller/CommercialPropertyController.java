package com.yourcompany.rentalmanagement.controller;

import com.yourcompany.rentalmanagement.dao.impl.PropertyDaoImpl;
import com.yourcompany.rentalmanagement.model.CommercialProperty;
import com.yourcompany.rentalmanagement.model.ResidentialProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommercialPropertyController {
    PropertyDaoImpl propertyDao = new PropertyDaoImpl();
    private Map<String, Object> data;

    public CommercialPropertyController() {
        this.propertyDao = new PropertyDaoImpl();
        this.data = new HashMap<>();
    }

    public List<CommercialProperty> getAllCommercialProperties() {
        return propertyDao.getAllCommercialProperties();
    }
}