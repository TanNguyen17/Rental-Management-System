package com.yourcompany.rentalmanagement.dao.impl;

/**
 * @author FTech
 */

import com.yourcompany.rentalmanagement.dao.PropertyDao;
import com.yourcompany.rentalmanagement.model.*;
import com.yourcompany.rentalmanagement.util.DataAccessException;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import javafx.application.Platform;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

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

public class PropertyDaoImpl implements PropertyDao {

    private List<Property> properties = new ArrayList<>();
    private Property property = null;
    private Map<String, Object> data = new HashMap<>();
    private List<Host> hosts = new ArrayList<>();
    private Transaction transaction;

    // Add cache for properties
    private static Map<String, List<Property>> propertyCache = new HashMap<>();
    private static final int CACHE_DURATION_MINUTES = 5;
    private static LocalDateTime lastCacheUpdate;

    private boolean isCacheValid() {
        return lastCacheUpdate != null
                && LocalDateTime.now().minusMinutes(CACHE_DURATION_MINUTES).isBefore(lastCacheUpdate);
    }

    // Check again
    @Override
    public boolean createProperty(Property property) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.persist(property);

            transaction.commit();
            return true;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Error creating property", e);
        }
    }

    @Override
    public ResidentialProperty getResidentialPropertyById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Query<ResidentialProperty> query = session.createQuery(
                    "SELECT p FROM ResidentialProperty p " +
                            "LEFT JOIN FETCH p.hosts " +
                            "LEFT JOIN FETCH p.rentalAgreement rA " +
                            "WHERE p.id = :id",
                    ResidentialProperty.class
            );

            query.setParameter("id", id);
            ResidentialProperty residentialProperty = query.uniqueResult();

            if (residentialProperty == null) throw new DataAccessException("Residential Property not found");

            transaction.commit();

            return residentialProperty;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Error getting residential property", e);
        }
    }

    @Override
    public CommercialProperty getCommercialPropertyById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Query<CommercialProperty> query = session.createQuery(
                    "SELECT p FROM CommercialProperty p " +
                            "LEFT JOIN FETCH p.hosts " +
                            "LEFT JOIN FETCH p.rentalAgreement rA " +
                            "WHERE p.id = :id",
                    CommercialProperty.class
            );

            query.setParameter("id", id);
            CommercialProperty commercialProperty = query.uniqueResult();

            if (commercialProperty == null) throw new DataAccessException("Commercial Property not found");

            transaction.commit();

            return commercialProperty;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Error getting commercial property", e);
        }
    }

    @Override
    public boolean updateProperty(Property property) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();


            property.setLastUpdated(LocalDateTime.now());
            session.merge(property);
            transaction.commit();

            return true;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Error updating property", e);
        }
    }

    @Override
    public boolean deleteProperty(Property property) {
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
                return true;
            } catch (HibernateException e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw new DataAccessException("Error deleting property", e);
            }
        }
    }

    @Override
    public boolean createCommercialProperty(CommercialProperty property) {
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
                return true;
            } catch (HibernateException e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw new DataAccessException("Error creating commercial property", e);
            }
        }
    }

    @Override
    public boolean createResidentialProperty(ResidentialProperty property) {
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
                return true;
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw new DataAccessException("Error creating residential property", e);
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
    public List<Property> getPropertiesByHostID(long hostId) {
        List<Property> properties = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Query for commercial properties
            Query<CommercialProperty> commercialQuery = session.createQuery(
                    "SELECT DISTINCT cp FROM CommercialProperty cp "
                            + "JOIN cp.hosts h WHERE h.id = :hostId", CommercialProperty.class);
            commercialQuery.setParameter("hostId", hostId);
            properties.addAll(commercialQuery.list());

            // Query for residential properties
            Query<ResidentialProperty> residentialQuery = session.createQuery(
                    "SELECT DISTINCT rp FROM ResidentialProperty rp "
                            + "JOIN rp.hosts h WHERE h.id = :hostId", ResidentialProperty.class);
            residentialQuery.setParameter("hostId", hostId);
            properties.addAll(residentialQuery.list());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
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
    public List<Property> getPropertiesAvailableForRenting(Property.PropertyStatus status) {
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
    public List<Property> getPropertiesAvailableForRenting(Property.PropertyStatus status, Map<String, Object> filter) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<ResidentialProperty> residentialQuery = criteriaBuilder.createQuery(ResidentialProperty.class);
            CriteriaQuery<CommercialProperty> commercialQuery = criteriaBuilder.createQuery(CommercialProperty.class);

            Root<ResidentialProperty> residentialPropertyRoot = residentialQuery.from(ResidentialProperty.class);
            Root<CommercialProperty> commercialPropertyRoot = commercialQuery.from(CommercialProperty.class);

            List<Predicate> residentialPredicates = new ArrayList<>();
            List<Predicate> commercialPredicates = new ArrayList<>();

            Join<ResidentialProperty, Address> residentialPropertyAddressJoin = residentialPropertyRoot.join("address");
            Join<CommercialProperty, Address> commercialPropertyAddressJoin = commercialPropertyRoot.join("address");

            if (filter != null) {
                String province = (String) filter.get("province");
                String district = (String) filter.get("district");
                String ward = (String) filter.get("ward");

                Predicate predicate = null;

                if (province != null) {
                    residentialPredicates.add(criteriaBuilder.equal(residentialPropertyAddressJoin.get("city"), province));
                    commercialPredicates.add(criteriaBuilder.equal(commercialPropertyAddressJoin.get("city"), province));
                }

                if (district != null) {
                    residentialPredicates.add(criteriaBuilder.equal(commercialPropertyAddressJoin.get("district"), district));
                    commercialPredicates.add(criteriaBuilder.equal(residentialPropertyAddressJoin.get("district"), district));
                }

                if (ward != null) {
                    residentialPredicates.add(criteriaBuilder.equal(commercialPropertyAddressJoin.get("ward"), ward));
                    commercialPredicates.add(criteriaBuilder.equal(residentialPropertyAddressJoin.get("ward"), ward));
                }
            }

            if (!residentialPredicates.isEmpty()) {
                residentialQuery.where(residentialPredicates.toArray(new Predicate[residentialPredicates.size()]));
            }
            if (!commercialPredicates.isEmpty()) {
                commercialQuery.where(commercialPredicates.toArray(new Predicate[commercialPredicates.size()]));
            }

            TypedQuery<CommercialProperty> commercialPropertyQuery = session.createQuery(commercialQuery);
            TypedQuery<ResidentialProperty> residentialPropertyQuery = session.createQuery(residentialQuery);

            properties.addAll(commercialPropertyQuery.getResultList());
            properties.addAll(residentialPropertyQuery.getResultList());

//            Query<ResidentialProperty> residentialPropertyQuery = session.createQuery(
//                    "SELECT rp FROM ResidentialProperty rp JOIN rp.hosts h WHERE status = :status", ResidentialProperty.class);
//            residentialPropertyQuery.setParameter("status", status);
//
//            Query<CommercialProperty> commercialPropertyQuery = session.createQuery(
//                    "SELECT cp FROM CommercialProperty cp JOIN cp.hosts h WHERE status = :status", CommercialProperty.class);
//            commercialPropertyQuery.setParameter("status", status);
//
//            properties.addAll(residentialPropertyQuery.list());
//            properties.addAll(commercialPropertyQuery.list());
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return properties;
    }

    @Override
    public List<CommercialProperty> getAllCommercialProperties() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<CommercialProperty> query = session.createQuery(
                    "FROM CommercialProperty", CommercialProperty.class);
            return query.list();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ResidentialProperty> getAllResidentialProperties() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ResidentialProperty> query = session.createQuery(
                    "FROM ResidentialProperty", ResidentialProperty.class);
            System.out.println("Get residential property successfully!");
            return query.list();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return null;
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
            String residentialHql = "SELECT COUNT(p) FROM ResidentialProperty p WHERE p.owner.id = :ownerId";
            String commercialHql = "SELECT COUNT(p) FROM CommercialProperty p WHERE p.owner.id = :ownerId";
            return session.createQuery(residentialHql, Long.class).setParameter("ownerId", ownerId).uniqueResult() + session.createQuery(commercialHql, Long.class).setParameter("ownerId", ownerId).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long getTotalResidentialPropertyCount() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String residentialHql = "SELECT COUNT(p) FROM ResidentialProperty p";
            String commercialHql = "SELECT COUNT(p) FROM CommercialProperty p";
            return session.createQuery(residentialHql, Long.class).uniqueResult() + session.createQuery(commercialHql, Long.class).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long getTotalCommercialPropertyCount() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String commercialHql = "SELECT COUNT(p) FROM CommercialProperty p";
            return session.createQuery(commercialHql, Long.class).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Long getTotalResidentialPropertyCount(long ownerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String commercialHql = "SELECT COUNT(p) FROM CommercialProperty p";
            return session.createQuery(commercialHql, Long.class).uniqueResult();
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
                    "SELECT CASE WHEN rp.id IS NOT NULL THEN rp.id ELSE cp.id END AS propertyId, "
                            + "ra.startContractDate, ra.endContractDate "
                            + "FROM RentalAgreement ra "
                            + "LEFT JOIN ra.residentialProperty rp "
                            + "LEFT JOIN ra.commercialProperty cp "
                            + "LEFT JOIN rp.hosts r_h "
                            + "LEFT JOIN cp.hosts c_h "
                            + "WHERE (r_h.id = :hostId OR c_h.id = :hostId) "
                            + "AND ra.startContractDate IS NOT NULL "
                            + "AND ra.endContractDate IS NOT NULL",
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
                    "SELECT rp.id, SUM(p.amount) "
                            + "FROM Payment p "
                            + "JOIN p.rentalAgreement ra "
                            + "JOIN ra.residentialProperty rp "
                            + "JOIN rp.hosts h "
                            + "WHERE h.id = :hostId AND p.status = 'PAID' "
                            + "GROUP BY rp.id",
                    Object[].class
            );
            residentialQuery.setParameter("hostId", hostId);

            // Query for Commercial Properties
            Query<Object[]> commercialQuery = session.createQuery(
                    "SELECT cp.id, SUM(p.amount) "
                            + "FROM Payment p "
                            + "JOIN p.rentalAgreement ra "
                            + "JOIN ra.commercialProperty cp "
                            + "JOIN cp.hosts h "
                            + "WHERE h.id = :hostId AND p.status = 'PAID' "
                            + "GROUP BY cp.id",
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

    public List<Property> getPropertiesByStatus(Property.PropertyStatus status) {
        return null;
    }
}
