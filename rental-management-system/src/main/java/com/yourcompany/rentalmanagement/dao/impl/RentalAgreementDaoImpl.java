package com.yourcompany.rentalmanagement.dao.impl;

/**
 * @author FTech
 */

import com.yourcompany.rentalmanagement.dao.RentalAgreementDao;
import com.yourcompany.rentalmanagement.model.*;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RentalAgreementDaoImpl implements RentalAgreementDao {
    private List<RentalAgreement> rentalAgreements;
    private Query<RentalAgreement> query;
    private RentalAgreement rentalAgreement;
    private Transaction transaction;
    Map<String, Object> data = new HashMap<>();
    Map<String, Object> result = new HashMap<>();

    public RentalAgreementDaoImpl() {
        transaction = null;
        rentalAgreements = new ArrayList<>();
    }

    // get all rental agreements
    @Override
    public Map<String, Object> getAllRentalAgreements() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            result.clear();

            rentalAgreements = session.createQuery("from RentalAgreement", RentalAgreement.class).getResultList();
            if (rentalAgreements.size() > 0) {
                result.put("rentalAgreements", rentalAgreements);
                result.put("status", "success");
            } else {
                result.put("status", "fail");
            }

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            result.put("status", "fail");
        }
        return result;
    }

    // get rental agreement with property data
    @Override
    public Map<String, Object> getFullRentalAgreement(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Get property and rental agreement
            Query<Property> propertyQuery = session.createQuery("SELECT p FROM Property p JOIN FETCH p.rentalAgreement rA where rA.id = :id", Property.class);
            propertyQuery.setParameter("id", id);
            Property property = propertyQuery.uniqueResult();
            rentalAgreement = session.get(RentalAgreement.class, id);

            result.clear();
            // Check if exist
            if (rentalAgreement != null && property != null) {
                result.put("rentalAgreement", rentalAgreement);
                result.put("property", property);
                result.put("status", "success");
            } else {
                result.put("status", "fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "fail");
        }
        return result;
    }

    // get active rental agreements
    @Override
    public Map<String, Object> getActiveRentalAgreements(LocalDate today) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            result.clear();

            Query<RentalAgreement> query = session.createQuery(
                    "FROM RentalAgreement WHERE startContractDate <= :today " +
                            "AND endContractDate >= :today",
                    RentalAgreement.class);
            query.setParameter("today", today);
            rentalAgreements = query.list();

            if (rentalAgreements.size() > 0) {
                data.put("rentalAgreements", rentalAgreements);
                data.put("status", "success");
            } else {
                data.put("status", "fail");
            }

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            data.put("status", "fail");
        }
        return data;
    }

    @Override
    public Map<String, Object> getRentalAgreementsByHostId(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            result.clear();

            rentalAgreements = session.createQuery(
                            "FROM RentalAgreement r WHERE r.host.id = :hostId",
                            RentalAgreement.class)
                    .setParameter("hostId", id)
                    .getResultList();

            if (rentalAgreements.size() > 0) {
                result.put("rentalAgreements", rentalAgreements);
                result.put("result", "success");
            } else {
                result.put("result", "fail");
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            result.put("result", "fail");
        }
        return result;
    }

    public Map<String, Object> getRelatedPayments(List<RentalAgreement> rentalAgreements) {
        List<Payment> relatedPayments = new ArrayList<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            result.clear();

            Transaction transaction = session.beginTransaction();
            // Collect IDs of all rental agreements
            List<Long> rentalAgreementIds = rentalAgreements.stream()
                    .map(RentalAgreement::getId)
                    .collect(Collectors.toList());

            // Query to fetch payments related to all rental agreements
            relatedPayments = session.createQuery("FROM Payment p WHERE p.rentalAgreement.id IN :rentalAgreementIds", Payment.class)
                    .setParameter("rentalAgreementIds", rentalAgreementIds)
                    .getResultList();

            // Commit the transaction
            transaction.commit();

            if (relatedPayments.size() > 0) {
                result.put("relatedPayments", relatedPayments);
                result.put("status", "success");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "fail");
        }

        return result;
    }

    @Override
    public Map<String, Object> createRentalAgreement(RentalAgreement rentalAgreement) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            result.clear();
            transaction = session.beginTransaction();
            session.save(rentalAgreement);
            transaction.commit();
            result.put("rentalAgreement", rentalAgreement);
            result.put("status", "success");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            result.put("status", "fail");
        }
        return result;
    }

    @Override
    public Map<String, Object> updateRentalAgreementById(long id, Map<String, Object> data) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Get rental agreement
            rentalAgreement = session.get(RentalAgreement.class, id);

            // Get property to add new and delete old
            if (data.get("property") != null) {
                // Get old property
                TypedQuery<Property> oldPropertyQuery = session.createQuery(
                        "SELECT p FROM Property p JOIN FETCH p.rentalAgreement r WHERE r.id = :id",
                        Property.class);
                Property oldProperty = oldPropertyQuery.setParameter("id", id).getSingleResult();
                oldProperty.setStatus(Property.PropertyStatus.AVAILABLE);
                oldProperty.setRentalAgreement(null);

                // Get new property
                Property newProperty = session.get(Property.class, ((Property) data.get("property")).getId());
                newProperty.setRentalAgreement(rentalAgreement);
                newProperty.setStatus(Property.PropertyStatus.RENTED);
            }

            if (data.get("status") != null) {
                rentalAgreement.setStatus((RentalAgreement.rentalAgreementStatus) data.get("status"));
            } else {
                rentalAgreement.setStatus(RentalAgreement.rentalAgreementStatus.NEW);
            }

            if (data.get("host") != null) {
                Host host = session.get(Host.class, ((Host) data.get("host")).getId());
                rentalAgreement.setHost(host);
            } else {
                rentalAgreement.setHost(null);
            }

            if (data.get("owner") != null) {
                rentalAgreement.setOwner((Owner) data.get("owner"));
            } else {
                rentalAgreement.setOwner(null);
            }

            rentalAgreement.setStartContractDate((LocalDate) data.get("startDate"));
            rentalAgreement.setEndContractDate((LocalDate) data.get("endDate"));
            rentalAgreement.setRentingFee((Double) data.get("rentingFee"));
            rentalAgreement.setTenants((List<Tenant>) data.get("subTenants"));

            session.persist(rentalAgreement);

            transaction.commit();
            result.put("status", "success");
            result.put("message", "Address updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "failed");
            result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> deleteRentalAgreementById(long rentalAgreementId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Retrieve the RentalAgreement
            RentalAgreement rentalAgreement = session.get(RentalAgreement.class, rentalAgreementId);
            if (rentalAgreement != null) {

                // Get property and remove the rental agreement
                TypedQuery<Property> currrentProperty = session.createQuery("SELECT p FROM Property p JOIN FETCH p.rentalAgreement r WHERE r.id = :id", Property.class);
                Property oldProperty = currrentProperty.setParameter("id", rentalAgreementId).getSingleResult();
                oldProperty.setRentalAgreement(null);

                // Remove rental agreement from tenant
                for (Tenant tenant : rentalAgreement.getTenants()) {
                    tenant.getRentalAgreements().removeIf(ra -> ra.getId() == rentalAgreementId);
                }
                rentalAgreement.getTenants().clear();

                // Delete the RentalAgreement
                session.remove(rentalAgreement);

                transaction.commit();
                result.put("status", "success");
                result.put("message", "Rental agreement delete successfully");
            } else {
                System.out.println("RentalAgreement not found with ID: " + rentalAgreementId);
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            result.put("status", "failed");
            result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> getRentalAgreementById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            result.clear();

            transaction = session.beginTransaction();
            rentalAgreement = session.get(RentalAgreement.class, id);
            transaction.commit();
            if (rentalAgreement != null) {
                result.put("rentalAgreement", rentalAgreement);
                result.put("status", "success");
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            result.put("status", "failed");
        }
        return result;
    }


    @Override
    public Map<String, Object> getRentalAgreementsByRole(User.UserRole role, long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            result.clear();

            transaction = session.beginTransaction();
            Query<RentalAgreement> query;
            if (role.equals(User.UserRole.TENANT)) {
                query = session.createQuery("SELECT rA from RentalAgreement rA LEFT JOIN FETCH Tenant t ON t.id = :id", RentalAgreement.class);
                // Query to fetch rental agreements for tenants
                query = session.createQuery(
                        "SELECT rA FROM RentalAgreement rA JOIN rA.tenants t WHERE t.id = :id",
                        RentalAgreement.class
                );
                query.setParameter("id", userId);
            } else {
                // Handle other roles or throw an exception
                throw new IllegalArgumentException("Unsupported role: " + role);
            }
            rentalAgreements = query.getResultList();

            if (rentalAgreements != null) {
                result.put("rentalAgreements", rentalAgreements);
                result.put("status", "success");
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            result.put("status", "failed");
        }
        return result;
    }

    @Override
    public Map<String, Object> createRentalAgreement(RentalAgreement rentalAgreement, long tenantId, Property property, long ownerId, long hostId, List<Long> subTenantIds) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            List<Tenant> subTenants = new ArrayList<>();

            for (Long id : subTenantIds) {
                subTenants.add(session.get(Tenant.class, id));
            }

            Tenant tenant = session.get(Tenant.class, tenantId);
            Owner owner = session.get(Owner.class, ownerId);
            Host host = session.get(Host.class, hostId);

            rentalAgreement.setOwner(owner);
            rentalAgreement.setHost(host);

            tenant.addRentalAgreement(rentalAgreement);
            for (Tenant subTenant : subTenants) {
                subTenant.addRentalAgreement(rentalAgreement);
            }

            property.setRentalAgreement(rentalAgreement);
            property.setStatus(Property.PropertyStatus.RENTED);

            session.persist(rentalAgreement);
            transaction.commit();

            data.put("rentalAgreement", rentalAgreement);
            data.put("status", "success");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public Map<Integer, Long> getYearlyRentalAgreements(String type) {
        Map<Integer, Long> yearlyRentalCounts = new HashMap<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Object[]> query;
            if (type.equalsIgnoreCase("residential")) {
                query = session.createQuery(
                        "SELECT YEAR(ra.startContractDate), COUNT(ra) " +
                                "FROM RentalAgreement ra JOIN ra.residentialProperty " +
                                "WHERE ra.startContractDate IS NOT NULL " +
                                "GROUP BY YEAR(ra.startContractDate) " +
                                "ORDER BY YEAR(ra.startContractDate)",
                        Object[].class
                );
            } else if (type.equalsIgnoreCase("commercial")) {
                query = session.createQuery(
                        "SELECT YEAR(ra.startContractDate), COUNT(ra) " +
                                "FROM RentalAgreement ra JOIN ra.commercialProperty " +
                                "WHERE ra.startContractDate IS NOT NULL " +
                                "GROUP BY YEAR(ra.startContractDate) " +
                                "ORDER BY YEAR(ra.startContractDate)",
                        Object[].class
                );
            } else {
                throw new IllegalArgumentException("Invalid type. Must be 'residential' or 'commercial'.");
            }

            // Process query results
            List<Object[]> results = query.getResultList();
            for (Object[] result : results) {
                int year = (Integer) result[0]; // Extract the year
                long count = (Long) result[1]; // Extract the count
                yearlyRentalCounts.put(year, count);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return yearlyRentalCounts;
    }

    public void printMonthlyRentalCounts(String type) {
        // Call the getMonthlyRentalAgreements method to get the data
        Map<Integer, Long> yearlyRentalCounts = getYearlyRentalAgreements(type);

        // Print the type for clarity
        System.out.println("Yearly Rental Counts for " + type + " properties:");

        // Iterate and print each month with its rental count
        yearlyRentalCounts.forEach((year, count) ->
                System.out.println(year + ": " + count + " agreements")
        );
    }

    @Override
    public Map<String, Object> createRentalAgreement(Map<String, Object> data) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            // Get property
            if (data.get("property") != null) {
                TypedQuery<Property> propertyQuery = session.createQuery(
                        "SELECT p FROM Property p WHERE p.id = :id",
                        Property.class);
                Property property = propertyQuery.setParameter("id", data.get("property")).getSingleResult();
                property.setRentalAgreement(rentalAgreement);
                property.setStatus(Property.PropertyStatus.RENTED);
            }

            // For status
            rentalAgreement.setStatus(RentalAgreement.rentalAgreementStatus.NEW);

            // For host
            if (data.get("host") != null) {
                Host hostInput = (Host) data.get("host");
                long hostId = hostInput.getId();
                Host host = session.get(Host.class, hostId);
                rentalAgreement.setHost(host);
            } else {
                rentalAgreement.setHost(null);
            }

            // For owner
            if (data.get("owner") != null) {
                Owner ownerInput = (Owner) data.get("owner");
                long ownerId = ownerInput.getId();
                Owner owner = session.get(Owner.class, ownerId);
                rentalAgreement.setOwner(owner);
            } else {
                rentalAgreement.setOwner(null);
            }

            // For tenant
            List<Tenant> subTenantsInput = (List<Tenant>) data.get("subTenants");
            List<Tenant> subTenants = new ArrayList<>();
            for (Tenant subTenant : subTenantsInput) {
                long tenantId = subTenant.getId();
                subTenants.add(session.get(Tenant.class, tenantId));
            }

            for (Tenant subTenant : subTenants) {
                subTenant.addRentalAgreement(rentalAgreement);
            }

            rentalAgreement.setStartContractDate((LocalDate) data.get("startDate"));
            rentalAgreement.setEndContractDate((LocalDate) data.get("endDate"));
            rentalAgreement.setRentingFee((Double) data.get("rentingFee"));
            rentalAgreement.setTenants((List<Tenant>) data.get("subTenants"));

            session.persist(rentalAgreement);
            transaction.commit();

            result.put("status", "success");
            result.put("message", "New rental agreement added successfully");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "failed");
            result.put("message", e.getMessage());
        }
        return result;
    }
}
