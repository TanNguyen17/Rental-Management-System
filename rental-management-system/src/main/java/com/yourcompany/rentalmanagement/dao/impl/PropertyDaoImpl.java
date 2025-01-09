package com.yourcompany.rentalmanagement.dao.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yourcompany.rentalmanagement.model.*;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.yourcompany.rentalmanagement.dao.PropertyDao;
import com.yourcompany.rentalmanagement.util.HibernateUtil;

public class PropertyDaoImpl implements PropertyDao {

    private List<Property> properties = new ArrayList<>();
    private Property property = null;
    private Map<String, Object> data = new HashMap<>();
    private List<Host> hosts = new ArrayList<>();

    @Override
    public void createProperty(Property property) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                property.setCreatedAt(LocalDateTime.now());
                property.setLastUpdated(LocalDateTime.now());
                session.persist(property);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    @Override
    public Map<String, Object> getResidentialPropertyById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            property = session.get(ResidentialProperty.class, id);
            if (property != null) {
                Query<Host> hostQuery = session.createQuery(
                        "SELECT h FROM Host h JOIN h.residentialProperties rp WHERE rp.id = :id", Host.class);

//                ResidentialProperty property = session.createQuery(
//                                "FROM ResidentialProperty p LEFT JOIN FETCH p.hosts WHERE p.id = :id", ResidentialProperty.class)
//                        .setParameter("id", id) -> single result

                hostQuery.setParameter("id", id);
                hosts = hostQuery.getResultList();

                data.put("hosts", hosts);
                data.put("property", property);
                data.put("owner", property.getOwner());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public Map<String, Object> getCommercialPropertyById(long id) {
        CommercialProperty commercialProperty = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            commercialProperty = session.get(CommercialProperty.class, id);
            if (property != null) {
                Hibernate.initialize(commercialProperty.getOwner());
                commercialProperty.getHosts();
                Hibernate.initialize(commercialProperty.getHosts());

                Query<Host> hostQuery = session.createQuery(
                        "SELECT h FROM Host h JOIN h.commercialProperties rp WHERE rp.id = :id", Host.class);
                hostQuery.setParameter("id", id);
                hosts = hostQuery.getResultList();

                data.put("hosts", hosts);
                data.put("property", property);
                data.put("owner", property.getOwner());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public void updateProperty(Property property) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                property.setLastUpdated(LocalDateTime.now());
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

                property.setCreatedAt(LocalDateTime.now());
                property.setLastUpdated(LocalDateTime.now());
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

                property.setCreatedAt(LocalDateTime.now());
                property.setLastUpdated(LocalDateTime.now());
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
            List<Property> properties = new ArrayList<>();

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

    @Override
    public List<Property> getPropertiesAvailableForRenting(Property.propertyStatus status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            Query<ResidentialProperty> residentialPropertyQuery = session.createQuery(
                    "SELECT rp FROM ResidentialProperty rp JOIN rp.hosts h WHERE status = :status", ResidentialProperty.class);
            residentialPropertyQuery.setParameter("status", status);

            Query<CommercialProperty> commercialPropertyQuery = session.createQuery(
                    "SELECT cp FROM CommercialProperty cp JOIN cp.hosts h WHERE status = :status", CommercialProperty.class);
            commercialPropertyQuery.setParameter("status", status);

            properties.addAll(residentialPropertyQuery.list());
            properties.addAll(commercialPropertyQuery.list());
            return properties;
        }
    }

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
