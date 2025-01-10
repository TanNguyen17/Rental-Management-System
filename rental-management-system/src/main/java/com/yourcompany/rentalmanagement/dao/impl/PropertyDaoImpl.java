package com.yourcompany.rentalmanagement.dao.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.yourcompany.rentalmanagement.dao.PropertyDao;
import com.yourcompany.rentalmanagement.model.CommercialProperty;
import com.yourcompany.rentalmanagement.model.Host;
import com.yourcompany.rentalmanagement.model.Owner;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.ResidentialProperty;
import com.yourcompany.rentalmanagement.util.HibernateUtil;

import javafx.application.Platform;

public class PropertyDaoImpl implements PropertyDao {

    private List<Property> properties = new ArrayList<>();
    private Property property = null;
    private Map<String, Object> data = new HashMap<>();
    private List<Host> hosts = new ArrayList<>();

    // Add cache for properties
    private static Map<String, List<Property>> propertyCache = new HashMap<>();
    private static final int CACHE_DURATION_MINUTES = 5;
    private static LocalDateTime lastCacheUpdate;

    private boolean isCacheValid() {
        return lastCacheUpdate != null
                && LocalDateTime.now().minusMinutes(CACHE_DURATION_MINUTES).isBefore(lastCacheUpdate);
    }

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
                clearCache();
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
                // Merge the property first to ensure it's in managed state
                property = session.merge(property);

                // Clear host relationships first
                if (property instanceof ResidentialProperty) {
                    String deleteJoinTableQuery = """
                        DELETE FROM host_residentialproperty 
                        WHERE residential_property_id = :propertyId
                        """;
                    session.createNativeQuery(deleteJoinTableQuery)
                            .setParameter("propertyId", property.getId())
                            .executeUpdate();

                } else if (property instanceof CommercialProperty) {
                    String deleteJoinTableQuery = """
                        DELETE FROM host_commercialproperty 
                        WHERE commercial_property_id = :propertyId
                        """;
                    session.createNativeQuery(deleteJoinTableQuery)
                            .setParameter("propertyId", property.getId())
                            .executeUpdate();
                }

                // Delete the property (address will be deleted via cascade)
                session.remove(property);

                transaction.commit();
                clearCache();
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

                property.setCreatedAt(LocalDateTime.now());
                property.setLastUpdated(LocalDateTime.now());

                Owner owner = session.get(Owner.class, property.getOwner().getId());
                property.setOwner(owner);

                session.persist(property);
                session.flush();

                if (property.getHostId() != null) {
                    Host host = session.get(Host.class, property.getHostId());
                    if (host != null) {
                        host.addCommercialProperty(property);
                        session.merge(host);
                    }
                }

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
                property.setCreatedAt(LocalDateTime.now());
                property.setLastUpdated(LocalDateTime.now());

                Owner owner = session.get(Owner.class, property.getOwner().getId());
                property.setOwner(owner);

                session.persist(property);
                session.flush();

                if (property.getHostId() != null) {
                    Host host = session.get(Host.class, property.getHostId());
                    if (host != null) {
                        host.addResidentialProperty(property);
                        session.merge(host);
                    }
                }

                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    @Override
    public List<Property> getAllProperties() {
        // Check cache first
        String cacheKey = "all_properties";
        if (propertyCache.containsKey(cacheKey) && isCacheValid()) {
            return propertyCache.get(cacheKey);
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Property> properties = new ArrayList<>();

            // Use separate queries with consistent aliases
            String residentialHql = """
                SELECT rp FROM ResidentialProperty rp 
                LEFT JOIN FETCH rp.address a 
                LEFT JOIN FETCH rp.owner o
                """;
            properties.addAll(session.createQuery(residentialHql, Property.class).list());

            String commercialHql = """
                SELECT cp FROM CommercialProperty cp 
                LEFT JOIN FETCH cp.address a 
                LEFT JOIN FETCH cp.owner o
                """;
            properties.addAll(session.createQuery(commercialHql, Property.class).list());

            // Update cache
            propertyCache.put(cacheKey, properties);
            lastCacheUpdate = LocalDateTime.now();

            return properties;
        }
    }

    @Override
    public List<Property> getPropertiesByOwner(long ownerId) {
        List<Property> properties = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String residentialHql = """
                FROM ResidentialProperty p 
                LEFT JOIN FETCH p.hosts 
                LEFT JOIN FETCH p.address 
                WHERE p.owner.id = :ownerId
                """;
            List<ResidentialProperty> residentialProperties = session.createQuery(residentialHql, ResidentialProperty.class)
                    .setParameter("ownerId", ownerId)
                    .list();
            properties.addAll(residentialProperties);

            String commercialHql = """
                FROM CommercialProperty p 
                LEFT JOIN FETCH p.hosts 
                LEFT JOIN FETCH p.address 
                WHERE p.owner.id = :ownerId
                """;
            List<CommercialProperty> commercialProperties = session.createQuery(commercialHql, CommercialProperty.class)
                    .setParameter("ownerId", ownerId)
                    .list();
            properties.addAll(commercialProperties);
        }
        return properties;
    }

    @Override
    public List<Property> getPropertiesAvailableForRenting(Property.propertyStatus status) {
        String cacheKey = "available_" + status;
        if (propertyCache.containsKey(cacheKey) && isCacheValid()) {
            return propertyCache.get(cacheKey);
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Property> properties = new ArrayList<>();

            // Use separate queries with consistent aliases
            String residentialHql = """
                SELECT rp FROM ResidentialProperty rp 
                LEFT JOIN FETCH rp.address a 
                LEFT JOIN FETCH rp.hosts h 
                LEFT JOIN FETCH rp.owner o
                WHERE rp.status = :status
                """;
            properties.addAll(session.createQuery(residentialHql, Property.class)
                    .setParameter("status", status)
                    .list());

            String commercialHql = """
                SELECT cp FROM CommercialProperty cp 
                LEFT JOIN FETCH cp.address a 
                LEFT JOIN FETCH cp.hosts h 
                LEFT JOIN FETCH cp.owner o
                WHERE cp.status = :status
                """;
            properties.addAll(session.createQuery(commercialHql, Property.class)
                    .setParameter("status", status)
                    .list());

            propertyCache.put(cacheKey, properties);
            lastCacheUpdate = LocalDateTime.now();

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

    @Override
    public List<Property> getAllPropertiesPaginated(int page, int pageSize) {
        List<Property> properties = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Load residential properties
            Query<ResidentialProperty> residentialQuery = session.createQuery(
                    "FROM ResidentialProperty", ResidentialProperty.class);
            residentialQuery.setFirstResult(page * pageSize);
            residentialQuery.setMaxResults(pageSize);
            properties.addAll(residentialQuery.list());

            // Load commercial properties
            Query<CommercialProperty> commercialQuery = session.createQuery(
                    "FROM CommercialProperty", CommercialProperty.class);
            commercialQuery.setFirstResult(page * pageSize);
            commercialQuery.setMaxResults(pageSize);
            properties.addAll(commercialQuery.list());
        }
        return properties;
    }

    @Override
    public List<Property> getAllPropertiesAfterPage(int page, int pageSize) {
        List<Property> properties = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Load remaining residential properties
            Query<ResidentialProperty> residentialQuery = session.createQuery(
                    "FROM ResidentialProperty", ResidentialProperty.class);
            residentialQuery.setFirstResult((page + 1) * pageSize);
            properties.addAll(residentialQuery.list());

            // Load remaining commercial properties
            Query<CommercialProperty> commercialQuery = session.createQuery(
                    "FROM CommercialProperty", CommercialProperty.class);
            commercialQuery.setFirstResult((page + 1) * pageSize);
            properties.addAll(commercialQuery.list());
        }
        return properties;
    }

    @Override
    public List<Property> getPropertiesPage(int page, int pageSize, long ownerId) {
        // Check page cache first
        String cacheKey = "page_" + page + "_" + ownerId;
        if (propertyCache.containsKey(cacheKey) && isCacheValid()) {
            return propertyCache.get(cacheKey);
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Use separate queries with pagination
            List<Property> properties = new ArrayList<>();

            String residentialHql = """
                SELECT rp FROM ResidentialProperty rp 
                LEFT JOIN FETCH rp.address a 
                LEFT JOIN FETCH rp.owner o
                WHERE rp.owner.id = :ownerId
                ORDER BY rp.createdAt DESC
                """;

            properties.addAll(session.createQuery(residentialHql, Property.class)
                    .setParameter("ownerId", ownerId)
                    .setFirstResult(page * pageSize)
                    .setMaxResults(pageSize)
                    .list());

            String commercialHql = """
                SELECT cp FROM CommercialProperty cp 
                LEFT JOIN FETCH cp.address a 
                LEFT JOIN FETCH cp.owner o
                WHERE cp.owner.id = :ownerId
                ORDER BY cp.createdAt DESC
                """;

            properties.addAll(session.createQuery(commercialHql, Property.class)
                    .setParameter("ownerId", ownerId)
                    .setFirstResult(page * pageSize)
                    .setMaxResults(pageSize)
                    .list());

            // Cache the page
            propertyCache.put(cacheKey, properties);
            lastCacheUpdate = LocalDateTime.now();

            return properties;
        }
    }

    @Override
    public void loadPropertiesAsync(int page, int pageSize, long ownerId,
            Consumer<List<Property>> onSuccess,
            Consumer<Throwable> onError) {
        CompletableFuture.supplyAsync(() -> {
            try {
                return getPropertiesPage(page, pageSize, ownerId);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }).thenAcceptAsync(onSuccess, Platform::runLater)
                .exceptionally(throwable -> {
                    Platform.runLater(() -> onError.accept(throwable));
                    return null;
                });
    }

    @Override
    public long getTotalPropertyCount(long ownerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(p) FROM Property p WHERE p.owner.id = :ownerId";
            return session.createQuery(hql, Long.class)
                    .setParameter("ownerId", ownerId)
                    .uniqueResult();
        }
    }

    public List<ResidentialProperty> getResidentialPropertiesByOwner(long ownerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM ResidentialProperty WHERE owner.id = :ownerId",
                    ResidentialProperty.class)
                    .setParameter("ownerId", ownerId)
                    .list();
        }
    }

    public List<CommercialProperty> getCommercialPropertiesByOwner(long ownerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM CommercialProperty WHERE owner.id = :ownerId",
                    CommercialProperty.class)
                    .setParameter("ownerId", ownerId)
                    .list();
        }
    }

    // Add method to clear cache when needed (e.g., after updates)
    public void clearCache() {
        propertyCache.clear();
        lastCacheUpdate = null;
    }

    // Add method for infinite scroll
    public void loadMoreProperties(int currentPage, int pageSize, long ownerId,
            Consumer<List<Property>> onSuccess, Consumer<Throwable> onError) {
        CompletableFuture.supplyAsync(() -> {
            try {
                return getPropertiesPage(currentPage + 1, pageSize, ownerId);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        })
                .thenAcceptAsync(properties -> {
                    Platform.runLater(() -> onSuccess.accept(properties));
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> onError.accept(throwable));
                    return null;
                });
    }
}
