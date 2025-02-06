package com.yourcompany.rentalmanagement.dao.impl;

/**
 * @author FTech
 */

import com.yourcompany.rentalmanagement.dao.UserDao;
import com.yourcompany.rentalmanagement.model.Address;
import com.yourcompany.rentalmanagement.model.Owner;
import com.yourcompany.rentalmanagement.util.DataAccessException;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import com.yourcompany.rentalmanagement.util.TimeFormat;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OwnerDaoImpl implements UserDao {
    private List<Owner> owners = new ArrayList<>();
    private List<String> ownerNames = new ArrayList<>();
    private Owner owner;
    private Transaction transaction;
    private Map<String, Object> result;
    private Long ownerCount = 0L;

    public OwnerDaoImpl() {
        transaction = null;
    }

    @Override
    public List<Owner> loadAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            owners = session.createQuery("FROM Owner", Owner.class).list();
            if (owners.isEmpty()) {
                throw new DataAccessException("No owners found");
            }
            transaction.commit();
            return owners;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Got error while retrieving owners", e);
        }
    }

    @Override
    public long getNumberOfUser() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery("SELECT count(*) FROM Owner");
            ownerCount = query.getSingleResult();

            return ownerCount;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Got error while retrieving owners", e);
        }
    }

    @Override
    public boolean updateProfile(long id, Map<String, Object> profile) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            owner = session.get(Owner.class, id);
            if (owner == null) throw new DataAccessException("Owner not found");

            owner.setUsername((String) profile.get("username"));
            owner.setDob(TimeFormat.stringToDate((String) profile.get("dob")));
            owner.setDob(LocalDate.parse((String) profile.get("dob")));
            owner.setEmail((String) profile.get("email"));
            owner.setPhoneNumber(profile.get("phoneNumber").toString());
            transaction.commit();
            return true;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Got error while updating profile", e);
        }
    }

    @Override
    public Owner getUserById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Owner> query = session.createQuery("FROM Owner WHERE id = :id", Owner.class);
            query.setParameter("id", id);
            owner = query.uniqueResult();

            if (owner == null) {
                throw new DataAccessException("Owner not found");
            }

            return owner;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Got error while retrieving owner", e);
        }
    }

    @Override
    public boolean updateUserImage(long id, String imageLink) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            owner = session.get(Owner.class, id);

            if (owner == null) throw new DataAccessException("Owner not found");

            owner.setProfileImage(imageLink);
            session.persist(owner);
            transaction.commit();
            return true;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Got error while updating profile", e);
        }
    }

    @Override
    public boolean updateAddress(long id, Map<String, Object> data) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            owner = session.get(Owner.class, id);

            if (owner == null) throw new DataAccessException("Owner not found");

            Address address = owner.getAddress();
            address.setCity(data.get("province").toString());
            address.setNumber(data.get("streetNumber").toString());
            address.setStreet(data.get("streetName").toString());
            address.setWard(data.get("ward").toString());
            address.setDistrict(data.get("district").toString());

            transaction.commit();
            return true;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Got error while updating profile", e);
        }
    }

    @Override
    public boolean updatePassword(long id, String oldPassword, String newPassword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            owner = session.get(Owner.class, id);

            if (owner == null) throw new DataAccessException("Owner not found");

            owner.setPassword(newPassword);
            session.persist(owner);
            transaction.commit();

            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Got error while updating profile", e);
        }
    }
}