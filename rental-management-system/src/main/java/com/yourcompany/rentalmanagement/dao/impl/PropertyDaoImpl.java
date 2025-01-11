package com.yourcompany.rentalmanagement.dao.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    public List<Property> getAllPropertiesByHostID(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Property> properties = new ArrayList<>();

            Query<ResidentialProperty> residentialQuery = session.createQuery(
                    "FROM ResidentialProperty rp JOIN rp.hosts h WHERE h.id =: id", ResidentialProperty.class).setParameter("id", id);
            properties.addAll(residentialQuery.list());

            Query<CommercialProperty> commercialQuery = session.createQuery(
                    "FROM CommercialProperty rp JOIN rp.hosts h WHERE h.id =: id", CommercialProperty.class).setParameter("id", id);
            properties.addAll(commercialQuery.list());

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

    public long getTotalPropertyCount() {
        long residentialCount = 0;
        long commercialCount = 0;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Count Residential Properties
            Query<Long> residentialQuery = session.createQuery("SELECT COUNT(r) FROM ResidentialProperty r", Long.class);
            residentialCount = residentialQuery.uniqueResult() != null ? residentialQuery.uniqueResult() : 0L;

            // Count Commercial Properties
            Query<Long> commercialQuery = session.createQuery("SELECT COUNT(c) FROM CommercialProperty c", Long.class);
            commercialCount = commercialQuery.uniqueResult() != null ? commercialQuery.uniqueResult() : 0L;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return the total count
        return residentialCount + commercialCount;
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

    public Map<Long, List<Long>> getStayDurationsByProperty(long hostId) {
        Map<Long, List<Long>> stayDurationsByProperty = new HashMap<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // HQL query to fetch property IDs and contract dates
            Query<Object[]> query = session.createQuery(
                    "SELECT CASE WHEN rp.id IS NOT NULL THEN rp.id ELSE cp.id END AS propertyId, " +
                            "ra.contractDate, ra.endContractDate " +
                            "FROM RentalAgreement ra " +
                            "LEFT JOIN ra.residentialProperty rp " +
                            "LEFT JOIN ra.commercialProperty cp " +
                            "LEFT JOIN rp.hosts r_h " +
                            "LEFT JOIN cp.hosts c_h " +
                            "WHERE (r_h.id = :hostId OR c_h.id = :hostId) " +
                            "AND ra.contractDate IS NOT NULL " +
                            "AND ra.endContractDate IS NOT NULL",
                    Object[].class
            );
            query.setParameter("hostId", hostId);

            // Process results
            List<Object[]> results = query.getResultList();
            for (Object[] result : results) {
                Long propertyId = (Long) result[0];
                LocalDate contractDate = (LocalDate) result[1];
                LocalDate endContractDate = (LocalDate) result[2];

                // Calculate the stay duration
                long stayDuration = ChronoUnit.DAYS.between(contractDate, endContractDate);

                // Group stay durations by property ID
                stayDurationsByProperty
                        .computeIfAbsent(propertyId, k -> new ArrayList<>())
                        .add(stayDuration);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stayDurationsByProperty;
    }

    public Map<Long, Double> calculateTotalIncomeByProperty(long hostId) {
        Map<Long, Double> incomeByProperty = new HashMap<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Query for Residential Properties
            Query<Object[]> residentialQuery = session.createQuery(
                    "SELECT rp.id, SUM(p.amount) " +
                            "FROM Payment p " +
                            "JOIN p.rentalAgreement ra " +
                            "JOIN ra.residentialProperty rp " +
                            "JOIN rp.hosts h " +
                            "WHERE h.id = :hostId AND p.status = 'PAID' " +
                            "GROUP BY rp.id",
                    Object[].class
            );
            residentialQuery.setParameter("hostId", hostId);

            // Query for Commercial Properties
            Query<Object[]> commercialQuery = session.createQuery(
                    "SELECT cp.id, SUM(p.amount) " +
                            "FROM Payment p " +
                            "JOIN p.rentalAgreement ra " +
                            "JOIN ra.commercialProperty cp " +
                            "JOIN cp.hosts h " +
                            "WHERE h.id = :hostId AND p.status = 'PAID' " +
                            "GROUP BY cp.id",
                    Object[].class
            );
            commercialQuery.setParameter("hostId", hostId);

            // Process Residential Results
            List<Object[]> residentialResults = residentialQuery.getResultList();
            for (Object[] result : residentialResults) {
                Long propertyId = (Long) result[0];
                Double totalIncome = (Double) result[1] * 0.2;
                incomeByProperty.put(propertyId, totalIncome);
            }

            // Process Commercial Results
            List<Object[]> commercialResults = commercialQuery.getResultList();
            for (Object[] result : commercialResults) {
                Long propertyId = (Long) result[0];
                Double totalIncome = (Double) result[1] * 0.2;
                incomeByProperty.put(propertyId, totalIncome);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return incomeByProperty;
    }

    public long getTotalCommercialPropertyCount() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery("select count(*) from CommercialProperty ");
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long getTotalResidentialPropertyCount() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery("select count(*) from ResidentialProperty ");
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
