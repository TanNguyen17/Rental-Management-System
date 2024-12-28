package com.yourcompany.rentalmanagement.dao;

import com.yourcompany.rentalmanagement.model.Owner;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

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
}
