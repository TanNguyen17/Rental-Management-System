package com.yourcompany.rentalmanagement.dao.impl;

/**
 * @author FTech
 */

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yourcompany.rentalmanagement.model.*;
import com.yourcompany.rentalmanagement.util.TimeFormat;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.yourcompany.rentalmanagement.dao.ManagerDao;
import com.yourcompany.rentalmanagement.util.HibernateUtil;

public class ManagerDaoImpl implements ManagerDao {
    Map<String, Object> result = new HashMap<String, Object>();
    Manager manager = null;
    Transaction transaction = null;

    @Override
    public void create(Manager manager) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(manager);
            transaction.commit();
        }
    }

    @Override
    public Manager read(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Manager.class, id);
        }
    }

    @Override
    public void update(Manager manager) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(manager);
            transaction.commit();
        }
    }

    @Override
    public void delete(Manager manager) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(manager);
            transaction.commit();
        }
    }

    @Override
    public Manager findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Manager> query = session.createQuery(
                    "FROM Manager WHERE username = :username",
                    Manager.class
            );
            query.setParameter("username", username);
            return query.uniqueResult();
        }
    }

    // Generic entity operations
    @Override
    public <T> void createEntity(T entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
        }
    }

    @Override
    public <T> T readEntity(Class<T> entityClass, Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(entityClass, id);
        }
    }

    @Override
    public <T> void updateEntity(T entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
        }
    }

    @Override
    public <T> void deleteEntity(T entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(entity);
            transaction.commit();
        }
    }

    // Entity listing operations
    @Override
    public List<User> getAllUsers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).list();
        }
    }

    @Override
    public List<RentalAgreement> getAllAgreements() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM RentalAgreement", RentalAgreement.class).list();
        }
    }

    @Override
    public List<Payment> getAllPayments() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Payment", Payment.class).list();
        }
    }

    @Override
    public List<Manager> getAllManagers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Manager", Manager.class).list();
        }
    }

    @Override
    public void softDeleteEntity(Object entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            if (entity instanceof User) {
                User user = (User) entity;
                // Implement soft delete logic
                // user.setDeleted(true);
                // user.setDeletedAt(LocalDateTime.now());
            }
            session.merge(entity);
            transaction.commit();
        }
    }

    @Override
    public void validateDataConsistency() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Check Users
            List<User> users = getAllUsers();
            for (User user : users) {
                // Validate user relationships
            }

            // Check RentalAgreements
            List<RentalAgreement> agreements = getAllAgreements();
            for (RentalAgreement agreement : agreements) {
                // Validate agreement relationships
            }

            // Check Payments
            List<Payment> payments = getAllPayments();
            for (Payment payment : payments) {
                // Validate payment relationships
            }
        }
    }
    @Override
    public Map<String, Object> updateProfile(long id, Map<String, Object> profile) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            manager = session.get(Manager.class, id);

            if (manager != null) {
                manager.setUsername((String) profile.get("username"));
                manager.setDob(TimeFormat.stringToDate((String) profile.get("dob")));
                manager.setDob(LocalDate.parse((String) profile.get("dob")));
                manager.setEmail((String) profile.get("email"));
                manager.setPhoneNumber(profile.get("phoneNumber").toString());
                result.put("status", "success");
                result.put("message", "Address updated successfully");
            } else {
                result.put("status", "failed");
                result.put("message", "Manager not found");
            }
            transaction.commit();

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

            manager = session.get(Manager.class, id);

            if (manager != null) {
                manager.setProfileImage(imageLink);
                session.persist(manager);
                result.put("status", "success");
                result.put("message", "Image updated successfully");
            }
            else {
                result.put("status", "failed");
            }
            transaction.commit();
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
            Query<Manager> query = session.createQuery("SELECT m from Manager m where m.id = :id", Manager.class);
            query.setParameter("id", id);
            manager = query.uniqueResult();

            if (manager != null) {
                if (manager.getAddress() != null) {
                    Address address = manager.getAddress();
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
                    manager.setAddress(address);
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

            manager = session.get(Manager.class, id);
            if (manager.checkPassword(oldPassword)) {
                manager.setPassword(newPassword);
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