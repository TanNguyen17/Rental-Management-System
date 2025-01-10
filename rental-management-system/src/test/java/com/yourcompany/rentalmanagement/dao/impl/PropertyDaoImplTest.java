package com.yourcompany.rentalmanagement.dao.impl;

import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.ResidentialProperty;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class PropertyDaoImplTest {
    private List<Property> propertyList = new ArrayList<>();
    Transaction transaction = null;

    @Test
    public void getPropertyWithAvailableHost() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            List<ResidentialProperty> allHostManagedResidential = session.createQuery(
                    "SELECT rp FROM ResidentialProperty rp JOIN rp.hosts h", ResidentialProperty.class).list();

            System.out.println("All Host-Managed Residential Properties:");
            allHostManagedResidential.forEach(rp -> System.out.println(rp.getTitle()));
            transaction.commit();

        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}