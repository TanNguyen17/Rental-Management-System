package com.yourcompany.rentalmanagement.dao.impl;

import com.yourcompany.rentalmanagement.dao.UserDao;
import com.yourcompany.rentalmanagement.model.Address;
import com.yourcompany.rentalmanagement.model.Host;
import com.yourcompany.rentalmanagement.model.Tenant;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import com.yourcompany.rentalmanagement.util.TimeFormat;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HostDaoImp implements UserDao {
    private List<Host> hosts = new ArrayList<Host>();
    private List<String> hostNames = new ArrayList<>();
    private Host host;
    private Transaction transaction = null;

    public HostDaoImp() {
        transaction = null;
    }

    @Override
    public void loadData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            hosts = session.createQuery("from Host", Host.class).list();
            //hosts.forEach(System.out::println);
            System.out.println(hosts.size());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<Host> getHosts() {
        return this.hosts;
    }

    @Override
    public List<String> getAllUserName() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Query<String> query = session.createQuery("select username from Host", String.class);
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
    public void updateProfile(long id, Map<String, Object> profile) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            host = session.get(Host.class, id);

            if (host != null) {
                host.setUsername((String) profile.get("username"));
                host.setDob(TimeFormat.stringToDate((String) profile.get("dob")));
                host.setEmail((String) profile.get("email"));
                host.setPhoneNumber(profile.get("phoneNumber").toString());
                session.persist(host);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public Host getUserById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Host> query = session.createQuery("from Host where id = :id", Host.class);
            query.setParameter("id", id);
            host = query.uniqueResult();

            if (host != null) {
                Hibernate.initialize(host.getAddress());
            }

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return host;
    }

    @Override
    public void updateUserImage(long id, String imageLink) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            host = session.get(Host.class, id);

            if (host != null) {
                host.setProfileImage(imageLink);
                session.persist(host);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void updateAddress(long id, Map<String, Object> data) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            host = session.get(Host.class, id);

            if (host != null) {
                Address address = host.getAddress();
                address.setCity(data.get("city").toString());
//                address.setState(data.get("state").toString());
                address.setNumber(data.get("number").toString());
                address.setStreet(data.get("street").toString());
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void updatePassword(long id, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            host = session.get(Host.class, id);

            if (host != null) {
                host.setPassword(password);
                session.persist(host);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

}
