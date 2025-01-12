package com.yourcompany.rentalmanagement.dao.impl;

/**
 * @author FTech
 */

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.yourcompany.rentalmanagement.model.Address;
import com.yourcompany.rentalmanagement.util.TimeFormat;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.yourcompany.rentalmanagement.dao.HostDao;
import com.yourcompany.rentalmanagement.model.Host;
import com.yourcompany.rentalmanagement.util.HibernateUtil;

public class HostDaoImpl implements HostDao {
    private Transaction transaction = null;
    private Host host = null;
    private Map<String, Object> result = new HashMap<>();

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

    @Override
    public Map<String, Object> updateProfile(long id, Map<String, Object> profile) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            host = session.get(Host.class, id);

            if (host != null) {
                host.setUsername((String) profile.get("username"));
                host.setDob(TimeFormat.stringToDate((String) profile.get("dob")));
                host.setDob(LocalDate.parse((String) profile.get("dob")));
                host.setEmail((String) profile.get("email"));
                host.setPhoneNumber(profile.get("phoneNumber").toString());
                result.put("status", "success");
                result.put("message", "Address updated successfully");
                transaction.commit();
                return result;
            }
            transaction.commit();
            result.put("status", "failed");
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

            host = session.get(Host.class, id);

            if (host != null) {
                host.setProfileImage(imageLink);
                session.persist(host);
                transaction.commit();
                result.put("status", "success");
                result.put("message", "Image updated successfully");
                return result;
            }
            transaction.commit();
            result.put("status", "failed");
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
            Query<Host> query = session.createQuery("SELECT h from Host h where h.id = :id", Host.class);
            query.setParameter("id", id);
            host = query.uniqueResult();

            if (host != null) {
                if (host.getAddress() != null) {
                    Address address = host.getAddress();
                    address.setCity(data.get("province").toString());
                    address.setDistrict(data.get("district").toString());
                    address.setWard(data.get("ward").toString());
                    address.setNumber(data.get("streetNumber").toString());
                    address.setStreet(data.get("streetName").toString());
                } else {
                    Address address = new Address();
                    address.setCity(data.get("province").toString());
                    address.setDistrict(data.get("district").toString());
                    address.setWard(data.get("ward").toString());
                    address.setNumber(data.get("streetNumber").toString());
                    address.setStreet(data.get("streetName").toString());
                    host.setAddress(address);
                }
                transaction.commit();
                result.put("status", "success");
                result.put("message", "Address updated successfully");
                return result;
            }

            transaction.commit();
            result.put("status", "failed");
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

            host = session.get(Host.class, id);
            if (host.checkPassword(oldPassword)) {
                host.setPassword(newPassword);
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

    public Map<String, Object> setPassword(long id, String newPassword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            host = session.get(Host.class, id);
            host.setHashedPassword(newPassword);
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

    @Override
    public List<String> getAllUserName() {
        return getAllHosts().stream().map(Host::getUsername).collect(Collectors.toList());
    }

    @Override
    public long getTotalUsers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery("select count(*) from Host");
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}