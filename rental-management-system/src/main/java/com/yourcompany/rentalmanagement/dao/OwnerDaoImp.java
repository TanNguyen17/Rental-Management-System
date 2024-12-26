package com.yourcompany.rentalmanagement.dao;

import com.yourcompany.rentalmanagement.model.Owner;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class OwnerDaoImp implements UserDao {
    private List<Owner> owners = new ArrayList<>();
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
    public Owner getUserById(long id) {
        Owner owner = null;
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
        Owner owner = null;
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
}
