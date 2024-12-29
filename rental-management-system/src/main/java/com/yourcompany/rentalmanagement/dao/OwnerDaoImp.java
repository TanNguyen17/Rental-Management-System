package com.yourcompany.rentalmanagement.dao;

import com.yourcompany.rentalmanagement.model.Address;
import com.yourcompany.rentalmanagement.model.Owner;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import com.yourcompany.rentalmanagement.util.TimeFormat;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OwnerDaoImp implements UserDao {
    private List<Owner> owners = new ArrayList<>();
    private Owner owner;
    private Transaction transaction;

    public OwnerDaoImp() {
        transaction = null;
    }

    @Override
    public void loadData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            owners = session.createQuery("from Owner", Owner.class).list();
            owners.forEach(System.out::println);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void updateProfile(long id, Map<String, Object> profile) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            owner = session.get(Owner.class, id);

            if (owner != null) {
                owner.setUsername((String) profile.get("username"));
                owner.setDob(TimeFormat.stringToDate((String) profile.get("dob")));
                owner.setEmail((String) profile.get("email"));
                owner.setPhoneNumber(profile.get("phoneNumber").toString());
                session.persist(owner);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
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
    public void updateUserImage(long id, String imageLink) {
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
    }

    @Override
    public void updateAddress(long id, Map<String, Object> data) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            owner = session.get(Owner.class, id);

            if (owner != null) {
                Address address = owner.getAddress();
                address.setCity(data.get("city").toString());
//                address.setState(data.get("state").toString());
                address.setNumber(data.get("number").toString());
                address.setStreet(data.get("street").toString());
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void updatePassword(long id, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            owner = session.get(Owner.class, id);

            if (owner != null) {
                owner.setPassword(password);
                session.persist(owner);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
