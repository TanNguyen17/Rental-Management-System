package com.yourcompany.rentalmanagement.dao.impl;

import com.yourcompany.rentalmanagement.dao.PaymentDao;
import com.yourcompany.rentalmanagement.model.*;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

public class PaymentDaoImpl implements PaymentDao {
    private Transaction transaction;
    private List<Payment> payments;
    private Payment payment;
    Map<String, Double> monthlyPayments;
    private Query<Payment> query;
    public static final int PAGE_SIZE = 10;
    private Map<String, String> data = new HashMap<>();

    public PaymentDaoImpl() {
        transaction = null;
        payments = new ArrayList<Payment>();
    }

    @Override
    public List<Payment> loadData(int pageNumber, Map<String, String> filterValue) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            query = session.createQuery("from Payment", Payment.class);
            if (filterValue != null) {
                String method = filterValue.get("method");
                String status = filterValue.get("status");

                if (method != null && status != null) {
                    query = session.createQuery("from Payment where method = :method AND status = :status", Payment.class);
                    query.setParameter("method", method);
                    query.setParameter("status", status);
                } else if (status != null) {
                    query = session.createQuery("from Payment where status = :status", Payment.class);
                    query.setParameter("status", status);
                } else if (method != null) {
                    query = session.createQuery("from Payment where method = :method", Payment.class);
                    query.setParameter("method", method);
                }

            }
            if (pageNumber > 0) {
                query.setFirstResult((pageNumber - 1) * 10);
                query.setMaxResults(10);
            }
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
    public Map<String, String> createPayment(Payment payment, long rentalAgreementId) {
        Payment lastPayment = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<RentalAgreement> query = session.createQuery("SELECT ra FROM RentalAgreement ra LEFT JOIN FETCH ra.tenants WHERE ra.id = :id", RentalAgreement.class);
            query.setParameter("id", rentalAgreementId);
            RentalAgreement rentalAgreement = query.getSingleResult();
            Tenant tenant = rentalAgreement.getTenants().get(0);
            if (rentalAgreement != null) {
                System.out.println(rentalAgreement.getId());
                payment.setMethod(tenant.getPaymentMethod() != null ? tenant.getPaymentMethod() : Payment.paymentMethod.CASH);
                payment.setRentalAgreement(rentalAgreement);
                payment.setTenant(rentalAgreement.getTenants().get(0));
                session.persist(payment);
                data.put("status", "success");
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            data.put("status", "failed");
        }
        return data;
    }

    @Override
    public List<Payment> loadDataByRole(int pageNumber, Map<String, String> filterValue, UserRole userRole, long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Payment> criteriaQuery = criteriaBuilder.createQuery(Payment.class);
            Root<Payment> paymentRoot = criteriaQuery.from(Payment.class);

            List<Predicate> predicates = new ArrayList<>();

            if (filterValue != null) {
                String method = filterValue.get("method");
                String status = filterValue.get("status");

                Predicate predicate = null;

                if (method != null) {
                    predicates.add(criteriaBuilder.equal(paymentRoot.get("method"), method));
                }
                if (status != null) {
                    predicates.add(criteriaBuilder.equal(paymentRoot.get("status"), status));
                }
            }

            if (userRole != null) {
                // Filtering based on userRole
                if (userRole.equals(UserRole.TENANT)) {
                    // Many to one relationship
                    Join<Payment, Tenant> tenant = paymentRoot.join("tenant", JoinType.INNER);
                    predicates.add(criteriaBuilder.equal(tenant.get("id"), userId));
//                    criteriaQuery.select(paymentRoot);
//                    criteriaQuery.where(criteriaBuilder.equal(tenant.get("id"), userId));
                } else if (userRole.equals(UserRole.HOST)) {
                    Join<Payment, RentalAgreement> rentalAgreementJoin = paymentRoot.join("rentalAgreement", JoinType.INNER);
                    Join<RentalAgreement, Host> rentalAgreementHostJoin = rentalAgreementJoin.join("host", JoinType.INNER);
                    predicates.add(criteriaBuilder.equal(rentalAgreementHostJoin.get("id"), userId));
                }
            }

            if (!predicates.isEmpty()) {
                criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
            }

            TypedQuery<Payment> query = session.createQuery(criteriaQuery);

            if (pageNumber > 0) {
                query.setFirstResult((pageNumber - 1) * 10);
                query.setMaxResults(10);
            }

            payments = query.getResultList();
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
    public Payment getLatestPayment(RentalAgreement rentalAgreement, LocalDate today) {
        Payment lastPayment = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Payment> query = session.createQuery("SELECT p FROM Payment p LEFT JOIN FETCH RentalAgreement rA ON rA.id = :agreement ORDER BY dueDate DESC", Payment.class);
            query.setParameter("agreement", rentalAgreement.getId());
            query.setMaxResults(1);
            lastPayment = query.uniqueResult();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return lastPayment;
    }


    @Override
    public Long getPaymentCount(Map<String, String> filterValue) {
        Long count = null;
        Query<Long> paymentCount = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            paymentCount = session.createQuery("SELECT COUNT(*) FROM Payment", Long.class);
            if (filterValue != null) {
                String method = filterValue.get("method");
                String status = filterValue.get("status");
                System.out.println(method);
                System.out.println(status);
                if (method != null && status != null) {
                    paymentCount = session.createQuery("SELECT COUNT(*) FROM Payment WHERE method = :method AND status = :status", Long.class);
                    paymentCount.setParameter("method", method);
                    paymentCount.setParameter("status", status);
                } else if (status != null) {
                    paymentCount = session.createQuery("SELECT COUNT(*) FROM Payment WHERE status = :status", Long.class);
                    paymentCount.setParameter("status", status);
                } else if (method != null) {
                    paymentCount = session.createQuery("SELECT COUNT(*) FROM Payment WHERE method = :method", Long.class);
                    paymentCount.setParameter("method", method);
                }
            }
            count = paymentCount.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public Map<String, String> updatePaymentStatus(long paymentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<Payment> query = session.createQuery("SELECT p FROM Payment p WHERE p.id = :id", Payment.class);
            query.setParameter("id", paymentId);
            Payment payment = query.uniqueResult();

            if (payment != null) {
                System.out.println(payment.getId());
                payment.setStatus(Payment.paymentStatus.PAID);
                session.persist(payment);
            } else {
                System.out.println("None");
            }
            transaction.commit();
            data.put("status", "success");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            data.put("status", "failed");
        }
        return data;
    }

    @Override
    public List<Payment> getAllPaidPayments(Payment.paymentStatus status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                Query<Payment> query = session.createQuery("from Payment where status = :status", Payment.class);
                query.setParameter("status", status);
                payments = query.list();
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e; // Re-throw the exception to allow higher-level handling
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return payments;
    }

    public List<Double> getMonthlyPayment(long id, String type) {
        List<Double> monthlyPayments = new ArrayList<>(Collections.nCopies(12, 0.0)); // Initialize with 12 zeros

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            String hql;

            // Modify the HQL query based on the type
            if ("expected".equalsIgnoreCase(type)) {
                hql = "SELECT MONTH(p.dueDate) AS month, SUM(p.amount) AS totalPayment " +
                        "FROM Payment p " +
                        "JOIN p.rentalAgreement ra " +
                        "WHERE ra.host.id = :hostId " +
                        "GROUP BY MONTH(p.dueDate) " +
                        "ORDER BY MONTH(p.dueDate)";
            } else if ("actual".equalsIgnoreCase(type)) {
                hql = "SELECT MONTH(p.dueDate) AS month, SUM(p.amount) AS totalPayment " +
                        "FROM Payment p " +
                        "JOIN p.rentalAgreement ra " +
                        "WHERE ra.host.id = :hostId AND p.status = 'PAID' " +
                        "GROUP BY MONTH(p.dueDate) " +
                        "ORDER BY MONTH(p.dueDate)";
            } else {
                throw new IllegalArgumentException("Invalid payment type. Must be 'expected' or 'actual'.");
            }

            Query query = session.createQuery(hql);
            query.setParameter("hostId", id);
            List<Object[]> results = query.getResultList();

            // Update the list with actual results
            for (Object[] result : results) {
                Integer month = (Integer) result[0];
                Double totalPayment = (Double) result[1] * 0.2;
                monthlyPayments.set(month - 1, totalPayment); // Update the corresponding month index
            }

            // Debugging: Print the list
            for (int i = 0; i < monthlyPayments.size(); i++) {
                System.out.println("Month: " + (i + 1) + ", Total Payment: " + monthlyPayments.get(i));
            }
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return monthlyPayments;
    }
}