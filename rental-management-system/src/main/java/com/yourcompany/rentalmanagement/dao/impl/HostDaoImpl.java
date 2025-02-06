package com.yourcompany.rentalmanagement.dao.impl;

/**
 * @author FTech
 */

import com.yourcompany.rentalmanagement.dao.UserDao;
import com.yourcompany.rentalmanagement.model.Address;
import com.yourcompany.rentalmanagement.model.Host;
import com.yourcompany.rentalmanagement.util.DataAccessException;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import com.yourcompany.rentalmanagement.util.TimeFormat;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HostDaoImpl implements UserDao {
    private Transaction transaction = null;
    private Host host = null;
    private List<Host> hosts = new ArrayList<>();
    private Map<String, Object> result = new HashMap<>();

    @Override
    public List<Host> loadAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<Host> query = session.createQuery("FROM Host", Host.class);
            hosts = query.getResultList();
            if (hosts.isEmpty()) {
                throw new DataAccessException("No hosts found");
            }
            return hosts;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Error while retrieving all hosts" + e.getMessage());
        }
    }

    @Override
    public Host getUserById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            host = session.get(Host.class, id);
            if (host != null) {
                throw new DataAccessException("No host found with id " + id);
            }
            transaction.commit();
            return host;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Error retrieving host from database " + e.getMessage());
        }
    }

    @Override
    public boolean updateProfile(long id, Map<String, Object> profile) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            host = session.find(Host.class, id);

            if (host == null) throw new DataAccessException("No host found with id " + id);

            host.setUsername((String) profile.get("username"));
            host.setDob(TimeFormat.stringToDate((String) profile.get("dob")));
            host.setDob(LocalDate.parse((String) profile.get("dob")));
            host.setEmail((String) profile.get("email"));
            host.setPhoneNumber(profile.get("phoneNumber").toString());
            result.put("status", "success");
            result.put("message", "Address updated successfully");
            session.merge(host);
            transaction.commit();

            return true;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Error while updating profile " + e.getMessage());
        }
    }

    @Override
    public boolean updateUserImage(long id, String imageLink) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            host = session.get(Host.class, id);

            if (host == null) throw new DataAccessException("No host found with id " + id);
            host.setProfileImage(imageLink);
            session.persist(host);
            transaction.commit();

            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Error while updating user image " + e.getMessage());
        }
    }

    @Override
    public boolean updateAddress(long id, Map<String, Object> data) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<Host> query = session.createQuery(
                    "SELECT h FROM Host h JOIN FETCH h.address WHERE h.id = :id",
                    Host.class
            );
            query.setParameter("id", id);
            host = query.uniqueResult();

            if (host == null) {
                throw new DataAccessException("No host found with id " + id);
            }

            Address address = host.getAddress();
            if (address == null) {
                address = new Address();
                host.setAddress(address);
            }

            address.setCity(data.get("province").toString());
            address.setDistrict(data.get("district").toString());
            address.setWard(data.get("ward").toString());
            address.setNumber(data.get("streetNumber").toString());
            address.setStreet(data.get("streetName").toString());
            session.merge(host);
            transaction.commit();

            return true;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Error while updating address" + e.getMessage());
        }
    }

    @Override
    public boolean updatePassword(long id, String oldPassword, String newPassword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            host = session.get(Host.class, id);
            if (host == null) {
                throw new DataAccessException("No host found with id " + id);
            }

            if (!host.checkPassword(oldPassword)) {
                throw new DataAccessException("Password not match");
            }

            host.setPassword(newPassword);
            session.merge(host);
            transaction.commit();
            return true;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Error while updating password from database " + e.getMessage());
        }
    }

    @Override
    public long getNumberOfUser() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<Long> query = session.createQuery("SELECT count(*) FROM Host");
            Long count = query.getSingleResult();
            transaction.commit();

            return count;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Error while retrieving total hosts from database " + e.getMessage());
        }
    }
}