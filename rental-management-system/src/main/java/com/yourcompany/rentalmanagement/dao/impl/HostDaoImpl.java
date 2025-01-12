package com.yourcompany.rentalmanagement.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.yourcompany.rentalmanagement.dao.HostDao;
import com.yourcompany.rentalmanagement.model.Host;
import com.yourcompany.rentalmanagement.util.HibernateUtil;

public class HostDaoImpl implements HostDao {
    private List<Host> hosts = new ArrayList<>();
    private List<String> hostNames = new ArrayList<>();
    private Host host;
    private Transaction transaction;
    private Map<String, Object> result;

    @Override
    public List<String> getAllUserName() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Query<String> query = session.createQuery("select username from Host ", String.class);
            hostNames = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return hostNames;
    }

    @Override
    public List<Host> getAllHosts() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Host> query = session.createQuery("FROM Host", Host.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Host getHostById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Host.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}