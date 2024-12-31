package com.yourcompany.rentalmanagement.dao;

import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.model.Tenant;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PaymentDaoImp implements PaymentDao {

    private Transaction transaction = null;
    private List<Payment> payments = new ArrayList<Payment>();
    private Payment payment;

    public PaymentDaoImp() {

    }

    @Override
    public List<Payment> loadData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            payments = session.createQuery("from Payment").list();
            transaction.commit();
            
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return payments;
    }

    @Override
    public Tenant getTenant(long paymentId) {
        Tenant tenant = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            payment = session.get(Payment.class, paymentId);
            if (payment != null) {
                tenant = payment.getTenant();
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
    public List<Payment> filterData(Map<String, String> filterValue) {
        String method = filterValue.get("method");
        String status = filterValue.get("status");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            if (method != null && status != null) {
                Query<Payment> query = session.createQuery("from Payment where method = :method AND status = :status", Payment.class);
                query.setParameter("method", method);
                query.setParameter("status", status);
                payments = query.list();
            }

            else if (status != null) {
                Query<Payment> query = session.createQuery("from Payment where status = :status", Payment.class);
                query.setParameter("status", status);
                payments = query.list();
            } else if (method != null) {
                Query<Payment> query = session.createQuery("from Payment where method = :method", Payment.class);
                query.setParameter("method", method);
                payments = query.list();
            }

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return payments;
    }


}
