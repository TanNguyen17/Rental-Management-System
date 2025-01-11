package com.yourcompany.rentalmanagement.dao.impl;

import com.yourcompany.rentalmanagement.dao.RentalManagementDao;
import com.yourcompany.rentalmanagement.model.*;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
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
    public List<RentalAgreement> getActiveRentalAgreements(LocalDate today) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<RentalAgreement> query = session.createQuery(
                    "FROM RentalAgreement WHERE startContractDate <= :today AND endContractDate >= :today", RentalAgreement.class);
            query.setParameter("today", today);
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
    public List<RentalAgreement> getRentalAgreementByRole(UserRole role, Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            if (role.equals(UserRole.TENANT)) {
                query = session.createQuery("SELECT rA from RentalAgreement rA LEFT JOIN FETCH Tenant t ON t.id = :id", RentalAgreement.class);
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

//    public static void main(String[] args) {
//        RentalManagementDao test = new RentalAgreementDaoImpl();
//        List<RentalAgreement> db = test.getAllRentalAgreements();
//        System.out.println("======================");
//        for (RentalAgreement rentalAgreement : db){
//            System.out.println(rentalAgreement.getTenants());
//        }
//    }
}