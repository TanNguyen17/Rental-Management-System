package com.yourcompany.rentalmanagement.dao.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.yourcompany.rentalmanagement.dao.PropertyDao;
import com.yourcompany.rentalmanagement.model.CommercialProperty;
import com.yourcompany.rentalmanagement.model.Owner;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.ResidentialProperty;
import com.yourcompany.rentalmanagement.util.HibernateUtil;

public class PropertyDaoImpl implements PropertyDao {
    private Transaction transaction = null;
    private List<Property> properties = new ArrayList<>();
    private List<Long> propertyIds = new ArrayList<>();

    public List<Long> getIDs() {
        // Fetch the list of Property objects
        List<Property> properties = getAllProperties();

        // Extract the IDs from the Property objects
        List<Long> propertyIds = properties.stream()
                .map(Property::getId)
                .collect(Collectors.toList());
        return propertyIds;
    }

    @Override
    public void createProperty(Property property) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                property.setCreatedAt(LocalDate.now());
                property.setLastUpdated(LocalDate.now());
                session.persist(property);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    @Override
    public Property getPropertyById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Property.class, id);
        }
    }

    @Override
    public void updateProperty(Property property) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                property.setLastUpdated(LocalDate.now());
                session.merge(property);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    @Override
    public void deleteProperty(Property property) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.remove(property);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    @Override
    public void createCommercialProperty(CommercialProperty property) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                // Re-attach the owner
                Owner owner = session.get(Owner.class, property.getOwner().getId());
                property.setOwner(owner);

                property.setCreatedAt(LocalDate.now());
                property.setLastUpdated(LocalDate.now());
                session.persist(property);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    @Override
    public void createResidentialProperty(ResidentialProperty property) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                // Re-attach the owner
                Owner owner = session.get(Owner.class, property.getOwner().getId());
                property.setOwner(owner);

                property.setCreatedAt(LocalDate.now());
                property.setLastUpdated(LocalDate.now());
                session.persist(property);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    @Override
    public List<Property> getAllProperties() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {


            // Get residential properties
            Query<ResidentialProperty> residentialQuery = session.createQuery(
                    "FROM ResidentialProperty", ResidentialProperty.class);
            properties.addAll(residentialQuery.list());

            // Get commercial properties
            Query<CommercialProperty> commercialQuery = session.createQuery(
                    "FROM CommercialProperty", CommercialProperty.class);
            properties.addAll(commercialQuery.list());

            return properties;
        }
    }

    @Override
    public List<Property> getPropertiesByOwner(long ownerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Property> properties = new ArrayList<>();

            // Get residential properties for owner with eager loading
            Query<ResidentialProperty> residentialQuery = session.createQuery(
                    "FROM ResidentialProperty p "
                    + "LEFT JOIN FETCH p.address "
                    + "WHERE p.owner.id = :ownerId",
                    ResidentialProperty.class);
            residentialQuery.setParameter("ownerId", ownerId);
            properties.addAll(residentialQuery.list());

            // Get commercial properties for owner with eager loading
            Query<CommercialProperty> commercialQuery = session.createQuery(
                    "FROM CommercialProperty p "
                    + "LEFT JOIN FETCH p.address "
                    + "WHERE p.owner.id = :ownerId",
                    CommercialProperty.class);
            commercialQuery.setParameter("ownerId", ownerId);
            properties.addAll(commercialQuery.list());

            return properties;
        }
    }

//    @Override
//    public List<Property> getPropertiesByStatus(Property.propertyStatus status) {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Query<Property> query = session.createQuery(
//                    "FROM Property WHERE status = :status", Property.class);
//            query.setParameter("status", status);
//            return query.list();
//        }
//    }

    @Override
    public List<CommercialProperty> getAllCommercialProperties() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<CommercialProperty> query = session.createQuery(
                    "FROM CommercialProperty", CommercialProperty.class);
            return query.list();
        }
    }

    @Override
    public List<ResidentialProperty> getAllResidentialProperties() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ResidentialProperty> query = session.createQuery(
                    "FROM ResidentialProperty", ResidentialProperty.class);
            return query.list();
        }
    }
}
