package com.yourcompany.rentalmanagement.dao.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yourcompany.rentalmanagement.model.Host;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.yourcompany.rentalmanagement.dao.UserDao;
import com.yourcompany.rentalmanagement.model.Address;
import com.yourcompany.rentalmanagement.model.Owner;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import com.yourcompany.rentalmanagement.util.TimeFormat;

public class OwnerDaoImpl implements UserDao {
    private List<Owner> owners = new ArrayList<>();
    private List<String> ownerNames = new ArrayList<>();
    private Owner owner;
    private Transaction transaction;
    private Map<String, Object> result;

    public OwnerDaoImpl() {
        transaction = null;
    }

    @Override
    public void loadData(){}

    @Override
    public List<Owner> loadAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            owners = session.createQuery("from Owner", Owner.class).list();
            // owners.forEach(System.out::println);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return owners;
    }

    public List<Owner> getOwners() {
        return this.owners;
    }

    @Override
    public List<String> getAllUserName() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Query<String> query = session.createQuery("select username from Owner ", String.class);
            ownerNames = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return ownerNames;
    }

    public List<Owner> getAllOwners() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Owner> query = session.createQuery("FROM Owner", Owner.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    public long getTotalUsers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery("select count(*) from Owner");
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Map<String, Object> updateProfile(long id, Map<String, Object> profile) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            owner = session.get(Owner.class, id);

            if (owner != null) {
                owner.setUsername((String) profile.get("username"));
                owner.setDob(TimeFormat.stringToDate((String) profile.get("dob")));
                owner.setDob(LocalDate.parse((String) profile.get("dob")));
                owner.setEmail((String) profile.get("email"));
                owner.setPhoneNumber(profile.get("phoneNumber").toString());
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Owner getUserById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Owner> query = session.createQuery("from Owner where id = :id", Owner.class);
            query.setParameter("id", id);
            owner = query.uniqueResult();

            if (owner != null) {
                Hibernate.initialize(owner.getAddress());
            }

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return owner;
    }

    @Override
    public Map<String, Object> updateUserImage(long id, String imageLink) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            owner = session.get(Owner.class, id);

            if (owner != null) {
                owner.setProfileImage(imageLink);
                session.persist(owner);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Map<String, Object> updateAddress(long id, Map<String, Object> data) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            owner = session.get(Owner.class, id);

            if (owner != null) {
                Address address = owner.getAddress();
                address.setCity(data.get("province").toString());
                address.setNumber(data.get("streetNumber").toString());
                address.setStreet(data.get("streetName").toString());
                address.setWard(data.get("ward").toString());
                address.setDistrict(data.get("district").toString());
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Map<String, Object> updatePassword(long id, String oldPassword, String newPassword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            owner = session.get(Owner.class, id);

            if (owner != null) {
                owner.setPassword(newPassword);
                session.persist(owner);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return result;
    }
}