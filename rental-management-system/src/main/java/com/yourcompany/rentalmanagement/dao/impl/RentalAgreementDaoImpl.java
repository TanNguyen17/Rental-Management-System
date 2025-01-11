package com.yourcompany.rentalmanagement.dao.impl;

import com.yourcompany.rentalmanagement.dao.RentalManagementDao;
import com.yourcompany.rentalmanagement.model.*;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RentalAgreementDaoImpl implements RentalManagementDao {
    private List<RentalAgreement> rentalAgreements;
    private Query<RentalAgreement> query;
    private Transaction transaction;
    Map<String, Object> data = new HashMap<>();

    public RentalAgreementDaoImpl() {
        transaction = null;
        rentalAgreements = new ArrayList<>();
    }

    @Override
    public List<RentalAgreement> getAllRentalAgreements() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            query = session.createQuery("from RentalAgreement", RentalAgreement.class);
            return query.list();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return rentalAgreements;
    }

    @Override
    public List<RentalAgreement> getRentalAgreementByRole(UserRole role, Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            if (role.equals(UserRole.TENANT)) {
                query = session.createQuery("from RentalAgreement", RentalAgreement.class);
                query = session.createQuery("SELECT rA from RentalAgreement JOIN rA.tenants t WHERE t.id = :id", RentalAgreement.class);
                query.setParameter("id", userId);
            }
            rentalAgreements = query.list();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return rentalAgreements;
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
            property.setStatus(Property.propertyStatus.RENTED);

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

    public static void main(String[] args) {
        RentalAgreementDaoImpl dao = new RentalAgreementDaoImpl();
        dao.printMonthlyRentalCounts("commercial");
        dao.printMonthlyRentalCounts("residential");
    }
}
