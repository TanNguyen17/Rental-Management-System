package com.yourcompany.rentalmanagement.controller;

import com.yourcompany.rentalmanagement.dao.impl.OwnerDaoImpl;
import com.yourcompany.rentalmanagement.model.Owner;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    OwnerDaoImpl ownerDao = new OwnerDaoImpl();

    @Test
    void testGetAllOwners() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Start a transaction to ensure test isolation
            Transaction transaction = session.beginTransaction();

            // Retrieve all owners using the method
            List<Owner> owners = ownerDao.getAllOwners();

            // Assert that the list is not null
            assertNotNull(owners, "The list of owners should not be null");

            // Assert that it contains owners (assuming the database is not empty)
            assertFalse(owners.isEmpty(), "The list of owners should not be empty");

            // Optionally print or verify specific details about the owners
            for (Owner owner : owners) {
                assertNotNull(owner.getId(), "Owner ID should not be null");
                assertNotNull(owner.getUsername(), "Owner username should not be null");
            }

            transaction.commit();
        } catch (Exception e) {
            fail("Exception should not occur while retrieving all owners");
        }
    }

}