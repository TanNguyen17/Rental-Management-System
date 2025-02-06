package com.yourcompany.rentalmanagement.dao.impl;

/**
 * @author FTech
 */

import com.yourcompany.rentalmanagement.dao.UserDao;
import com.yourcompany.rentalmanagement.model.Address;
import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.model.Tenant;
import com.yourcompany.rentalmanagement.util.DataAccessException;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import com.yourcompany.rentalmanagement.util.TimeFormat;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TenantDaoImpl implements UserDao {

    private List<Tenant> tenants;
    private Tenant tenant;
    private Transaction transaction;
    private final Map<String, Object> result;

    public TenantDaoImpl() {
        transaction = null;
        result = new HashMap<>();
        tenants = new ArrayList<>();
    }

    public boolean createTenant(Tenant tenant) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(tenant);
            transaction.commit();

            return true;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Failed to create tenant.", e);
        }
    }

    @Override
    public List<Tenant> loadAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Query<Tenant> query = session.createQuery("FROM Tenant", Tenant.class);
            tenants = query.list();
            if (tenants == null) throw new DataAccessException("Failed to get all tenants.");

            transaction.commit();
            return query.list();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Failed to get all tenants.", e);
        }
    }

    @Override
    public Tenant getUserById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<Tenant> query = session.createQuery("FROM Tenant WHERE id = :id", Tenant.class);
            query.setParameter("id", id);
            tenant = query.uniqueResult();

            if (tenant == null) throw new DataAccessException("Failed to get tenant.");

            transaction.commit();
            return tenant;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Failed to get tenant.", e);
        }
    }

    @Override
    public long getNumberOfUser() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery("SELECT count(*) FROM Tenant");
            return query.getSingleResult();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Failed to get tenant.", e);
        }
    }

    @Override
    public boolean updateProfile(long id, Map<String, Object> profile) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            tenant = session.get(Tenant.class, id);

            if (tenant == null) throw new DataAccessException("Failed to get tenant.");

            tenant.setUsername((String) profile.get("username"));
            tenant.setDob(TimeFormat.stringToDate((String) profile.get("dob")));
            tenant.setDob(LocalDate.parse((String) profile.get("dob")));
            tenant.setEmail((String) profile.get("email"));
            tenant.setPhoneNumber(profile.get("phoneNumber").toString());
            tenant.setPaymentMethod((Payment.paymentMethod) profile.get("paymentMethod"));

            transaction.commit();
            return true;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Failed to update tenant.", e);
        }
    }

    @Override
    public boolean updateUserImage(long id, String imageLink) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            tenant = session.get(Tenant.class, id);
            if (tenant == null) throw new DataAccessException("Failed to get tenant.");

            tenant.setProfileImage(imageLink);
            session.persist(tenant);
            transaction.commit();
            return true;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Failed to update tenant.", e);
        }
    }

    @Override
    public boolean updateAddress(long id, Map<String, Object> data) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<Tenant> query = session.createQuery("SELECT t from Tenant t where t.id = :id", Tenant.class);
            query.setParameter("id", id);
            tenant = query.uniqueResult();

            if (tenant == null) throw new DataAccessException("Failed to get tenant.");

            Address address = tenant.getAddress();
            if (address == null) {
                Address newAddress = new Address();
                tenant.setAddress(newAddress);
            }

            address.setCity(data.get("province").toString());
            address.setDistrict(data.get("district").toString());
            address.setWard(data.get("ward").toString());
            address.setNumber(data.get("streetNumber").toString());
            address.setStreet(data.get("streetName").toString());
            session.merge(tenant);
            transaction.commit();
            return true;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Failed to update tenant.", e);
        }
    }

    @Override
    public boolean updatePassword(long id, String oldPassword, String newPassword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            tenant = session.get(Tenant.class, id);
            if (tenant == null) {
                throw new DataAccessException("No host found with id " + id);
            }

            if (!tenant.checkPassword(oldPassword)) {
                throw new DataAccessException("Password not match");
            }

            tenant.setPassword(newPassword);
            session.merge(tenant);
            transaction.commit();

            return true;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Error while updating password from database " + e.getMessage());
        }
    }
}