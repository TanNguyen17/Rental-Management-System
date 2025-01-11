package com.yourcompany.rentalmanagement.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.yourcompany.rentalmanagement.dao.HostDao;
import com.yourcompany.rentalmanagement.model.Host;
import com.yourcompany.rentalmanagement.util.HibernateUtil;

public class HostDaoImpl implements HostDao {

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