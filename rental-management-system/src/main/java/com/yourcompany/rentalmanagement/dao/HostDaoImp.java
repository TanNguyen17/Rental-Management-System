package com.yourcompany.rentalmanagement.dao;

import com.yourcompany.rentalmanagement.model.Host;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class HostDaoImp implements UserDao{
    private List<Host> hosts = new ArrayList<Host>();
    private Transaction transaction;

    public HostDaoImp() {
        loadData();
        transaction = null;
    }

    @Override
    public void loadData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            hosts = session.createQuery("from Host", Host.class).list();
            hosts.forEach(System.out::println);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
