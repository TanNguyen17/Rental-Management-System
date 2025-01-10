package com.yourcompany.rentalmanagement.dao.impl;

import com.yourcompany.rentalmanagement.dao.UserDao;
import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.model.Tenant;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TenantDaoImp implements UserDao {
    private List<Tenant> tenants = new ArrayList<>();
    private List<String> tenantNames = new ArrayList<>();
    private Transaction transaction;
    private Map<String, Object> result;

    @Override
    public void loadData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Query<Tenant> query = session.createQuery("from Tenant", Tenant.class);
            tenants = query.list();

            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

    }

    @Override
    public <T> T getUserById(long id) {
        return null;
    }

    @Override
    public List<String> getAllUserName() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Query<String> query = session.createQuery("select username from Tenant", String.class);
            tenantNames = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return tenantNames;
    }

    @Override
    public Map<String, Object> updateProfile(long id, Map<String, Object> profile) {
        return result;
    }

    @Override
    public Map<String, Object> updateUserImage(long id, String imageLink) {
        return result;
    }

    @Override
    public Map<String, Object> updateAddress(long id, Map<String, Object> address){
        return result;
    }

    @Override
    public Map<String, Object> updatePassword(long id, String oldPassword, String newPassword) {
        return result;
    }

    @Override
    public <T extends Object> List<T> loadAll(){
        return null;
    }
}