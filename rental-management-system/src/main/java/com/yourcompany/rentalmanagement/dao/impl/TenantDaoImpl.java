package com.yourcompany.rentalmanagement.dao.impl;

import com.yourcompany.rentalmanagement.dao.UserDao;
import com.yourcompany.rentalmanagement.model.Address;
import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.model.Owner;
import com.yourcompany.rentalmanagement.model.Tenant;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import com.yourcompany.rentalmanagement.util.TimeFormat;
import org.hibernate.Hibernate;
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

    @Override
    public void loadData(){}

    @Override
    public List<String> getAllUserName(){
        return null;
    }

    public List<Tenant> getAllTenants() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Tenant> query = session.createQuery("FROM Tenant", Tenant.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Tenant> loadAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tenants = session.createQuery("from Tenant", Tenant.class).list();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return tenants;
    }

    @Override
    public Tenant getUserById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Tenant> query = session.createQuery("from Tenant where id = :id", Tenant.class);
            query.setParameter("id", id);
            tenant = query.uniqueResult();

            if (tenant != null) {
                Hibernate.initialize(tenant.getAddress());
            }

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return tenant;
    }

    @Override
    public long getTotalUsers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery("select count(*) from Tenant");
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

            tenant = session.get(Tenant.class, id);

            if (tenant != null) {
                tenant.setUsername((String) profile.get("username"));
                tenant.setDob(TimeFormat.stringToDate((String) profile.get("dob")));
                tenant.setDob(LocalDate.parse((String) profile.get("dob")));
                tenant.setEmail((String) profile.get("email"));
                tenant.setPhoneNumber(profile.get("phoneNumber").toString());
                tenant.setPaymentMethod((Payment.paymentMethod) profile.get("paymentMethod"));
            }
            transaction.commit();
            result.put("status", "success");
            result.put("message", "Address updated successfully");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            result.put("status", "failed");
            result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> updateUserImage(long id, String imageLink) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            tenant = session.get(Tenant.class, id);

            if (tenant != null) {
                tenant.setProfileImage(imageLink);
                session.persist(tenant);
            }
            transaction.commit();
            result.put("status", "success");
            result.put("message", "Image updated successfully");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            result.put("status", "failed");
            result.put("message", e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Map<String, Object> updateAddress(long id, Map<String, Object> data) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<Tenant> query = session.createQuery("SELECT t from Tenant t where t.id = :id", Tenant.class);
            query.setParameter("id", id);
            tenant = query.uniqueResult();

            if (tenant != null) {
                if (tenant.getAddress() != null) {
                    Address address = tenant.getAddress();
                    address.setCity(data.get("province").toString());
                    address.setDistrict(data.get("district").toString());
                    address.setWard(data.get("ward").toString());
                    address.setNumber(data.get("streetNumber").toString());
                    address.setStreet(data.get("streetNam" +
                            "e").toString());
                } else {
                    Address address = new Address();
                    address.setCity(data.get("province").toString());
                    address.setDistrict(data.get("district").toString());
                    address.setWard(data.get("ward").toString());
                    address.setNumber(data.get("streetNumber").toString());
                    address.setStreet(data.get("streetName").toString());
                    tenant.setAddress(address);
                }
            }

            transaction.commit();
            result.put("status", "success");
            result.put("message", "Address updated successfully");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            result.put("status", "failed");
            result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> updatePassword(long id, String oldPassword, String newPassword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            tenant = session.get(Tenant.class, id);
            if (tenant.checkPassword(oldPassword)) {
                tenant.setPassword(newPassword);
            } else {
                result.put("status", "failed");
                result.put("message", "Password does not match");
                return result;
            }
            transaction.commit();
            result.put("status", "success");
            result.put("message", "Password changed successfully");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            result.put("status", "failed");
            result.put("message", e.getMessage());
        }
        return result;
    }
}