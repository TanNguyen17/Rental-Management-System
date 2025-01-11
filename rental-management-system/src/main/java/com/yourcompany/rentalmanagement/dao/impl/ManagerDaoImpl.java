package com.yourcompany.rentalmanagement.dao.impl;

import java.util.List;

import com.yourcompany.rentalmanagement.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.yourcompany.rentalmanagement.dao.ManagerDao;
import com.yourcompany.rentalmanagement.model.Manager;
import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import com.yourcompany.rentalmanagement.util.HibernateUtil;

public class ManagerDaoImpl implements ManagerDao {

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
}