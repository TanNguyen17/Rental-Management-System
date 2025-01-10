package com.yourcompany.rentalmanagement.dao.impl;

import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.dao.PaymentDao;
import com.yourcompany.rentalmanagement.model.Tenant;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PaymentDaoImpl implements PaymentDao {

    private Transaction transaction = null;
    private List<Payment> payments = new ArrayList<Payment>();
    private Payment payment;
    private Map<String, Double> monthlyPayments;

    public PaymentDaoImpl() {
        //loadData();
    }

    @Override
    public List<Payment> loadData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Query<Payment> query = session.createQuery("from Payment", Payment.class);
            payments = query.list();

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
    public List<Payment> loadDataPag(int pageNumber) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Query<Payment> query = session.createQuery("from Payment", Payment.class);
            query.setFirstResult((pageNumber - 1) * 10);
            query.setMaxResults(10);
            payments = query.list();

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

    @Override
    public Long getTotalPaymentCount() {
        Long count = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Query<Long> query = session.createQuery("SELECT COUNT(*) FROM Payment", Long.class);
            count = (Long) query.uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public Map<String, Double> getMonthlyPayment(String id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query query = session.createQuery(
                    "SELECT MONTH(p.paymentDate) AS month, SUM(p.amount) AS totalPayment " +
                            "FROM Payment p " +
                            "JOIN p.rentalAgreement ra " +
                            "WHERE ra.host.id = :hostId " +
                            "GROUP BY MONTH(p.paymentDate) " +
                            "ORDER BY MONTH(p.paymentDate)"
            );
            query.setParameter("hostId", id);
            List<Object[]> results = query.getResultList();

                // Map to store the results
            monthlyPayments = new LinkedHashMap<>();

            // Helper array for month names
            String[] monthNames = {
                    "January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"
            };

            // Save the results into the map
            for (Object[] result : results) {
                Integer month = (Integer) result[0];
                Double totalPayment = (Double) result[1];
                String monthName = monthNames[month - 1]; // Convert month index to name
                monthlyPayments.put(monthName, totalPayment);
            }

            // Debugging: Print the map
            monthlyPayments.forEach((month, total) ->
                    System.out.println("Month: " + month + ", Total Payment: " + total)
            );
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return monthlyPayments;
    }

}
