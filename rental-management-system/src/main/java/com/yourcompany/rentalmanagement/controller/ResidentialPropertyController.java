package com.yourcompany.rentalmanagement.controller;

import com.yourcompany.rentalmanagement.dao.impl.PropertyDaoImpl;
import com.yourcompany.rentalmanagement.model.ResidentialProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResidentialPropertyController {
    PropertyDaoImpl propertyDao = new PropertyDaoImpl();
    private Map<String, Object> data;

    public ResidentialPropertyController() {
        this.propertyDao = new PropertyDaoImpl();
        this.data = new HashMap<>();
    }

    public List<ResidentialProperty> getAllResidentialProperty() {
        return propertyDao.getAllResidentialProperties();
    }
}