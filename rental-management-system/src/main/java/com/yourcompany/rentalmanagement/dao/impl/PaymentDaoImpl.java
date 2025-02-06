package com.yourcompany.rentalmanagement.dao.impl;


/**
 * @author FTech
 */

import com.yourcompany.rentalmanagement.dao.PaymentDao;
import com.yourcompany.rentalmanagement.model.*;
import com.yourcompany.rentalmanagement.util.DataAccessException;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.*;

public class PaymentDaoImpl implements PaymentDao {
    private Transaction transaction;
    private List<Payment> payments;
    private Payment payment;
    Map<String, Double> monthlyPayments;
    private Query<Payment> query;
    public static final int PAGE_SIZE = 10;
    private Map<String, String> data = new HashMap<>();
    private Tenant tenant;
    private Long count = null;

    public PaymentDaoImpl() {
        transaction = null;
        payments = new ArrayList<Payment>();
    }

    public List<Payment> getAllPayments() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Start the transaction
            transaction = session.beginTransaction();

            // Execute the query to fetch all Payment records
            Query<Payment> query = session.createQuery("FROM Payment", Payment.class);
            payments = query.list();

            if (payments.isEmpty()) throw new DataAccessException("No payment found");

            // Commit the transaction
            transaction.commit();

            return payments;
        } catch (HibernateException e) {
            // Rollback transaction in case of an error
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Error getting all payments", e);
        }
    }

    @Override
    public boolean createPayment(Payment payment, long rentalAgreementId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Get rental agreement
            Query<RentalAgreement> query = session.createQuery("SELECT ra FROM RentalAgreement ra JOIN FETCH ra.tenants WHERE ra.id = :id", RentalAgreement.class);
            query.setParameter("id", rentalAgreementId);
            RentalAgreement rentalAgreement = query.getSingleResult();

            if (rentalAgreement == null) throw new DataAccessException("No rental agreement found");

            Tenant tenant = rentalAgreement.getTenants().get(0);
            if (tenant == null) throw new DataAccessException("No tenant found");

            payment.setMethod(tenant.getPaymentMethod() != null ? tenant.getPaymentMethod() : Payment.paymentMethod.CASH);
            payment.setRentalAgreement(rentalAgreement);
            payment.setTenant(rentalAgreement.getTenants().get(0));
            session.persist(payment);

            transaction.commit();
            return true;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Error creating payment", e);
        }
    }

    @Override
    public List<Payment> loadDataByRole(int pageNumber, Map<String, String> filterValue, User.UserRole userRole, long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Payment> criteriaQuery = criteriaBuilder.createQuery(Payment.class);
            Root<Payment> paymentRoot = criteriaQuery.from(Payment.class);

            List<Predicate> predicates = new ArrayList<>();

            // Filter base on condition
            if (filterValue != null) {
                String method = filterValue.get("method");
                String status = filterValue.get("status");

                if (method != null) {
                    predicates.add(criteriaBuilder.equal(paymentRoot.get("method"), method));
                }
                if (status != null) {
                    predicates.add(criteriaBuilder.equal(paymentRoot.get("status"), status));
                }
            }

            // Filtering based on userRole
            if (userRole != null) {
                if (userRole.equals(User.UserRole.TENANT)) {
                    Join<Payment, Tenant> tenant = paymentRoot.join("tenant", JoinType.INNER);
                    predicates.add(criteriaBuilder.equal(tenant.get("id"), userId));
                } else if (userRole.equals(User.UserRole.HOST)) {
                    Join<Payment, RentalAgreement> rentalAgreementJoin = paymentRoot.join("rentalAgreement", JoinType.INNER);
                    Join<RentalAgreement, Host> rentalAgreementHostJoin = rentalAgreementJoin.join("host", JoinType.INNER);
                    predicates.add(criteriaBuilder.equal(rentalAgreementHostJoin.get("id"), userId));
                }
            }

            // Add predicate to the query
            if (!predicates.isEmpty()) {
                criteriaQuery.where(predicates.toArray(new Predicate[0]));
            }

            TypedQuery<Payment> query = session.createQuery(criteriaQuery);

            // Get the result from a range
            if (pageNumber > 0) {
                query.setFirstResult((pageNumber - 1) * 10);
                query.setMaxResults(10);
            }

            payments = query.getResultList();

            if (payments.isEmpty()) throw new DataAccessException("No payment found");

            transaction.commit();
            return payments;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Error loading data", e);
        }
    }


    @Override
    public Tenant getTenant(long paymentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Query<Payment> paymentQuery = session.createQuery(
                    "SELECT p FROM Payment p JOIN FETCH p.tenant t WHERE t.id = :id",
                    Payment.class
            );
            paymentQuery.setParameter("id", paymentId);
            payment = paymentQuery.getSingleResult();

            if (payment == null) throw new DataAccessException("No payment found");
            transaction.commit();
            return payment.getTenant();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Error getting tenant", e);
        }
    }

    @Override
    public Payment getLatestPayment(RentalAgreement rentalAgreement, LocalDate today) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Payment> query = session.createQuery("SELECT p FROM Payment p LEFT JOIN FETCH RentalAgreement rA ON rA.id = :agreement ORDER BY dueDate DESC", Payment.class);
            query.setParameter("agreement", rentalAgreement.getId());
            query.setMaxResults(1);
            payment = query.uniqueResult();
            return payment;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Error getting latest payment", e);
        }
    }


    @Override
    public Long getPaymentCountByRole(Map<String, String> filterValue, User.UserRole userRole, long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            // Set up criteria query
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            Root<Payment> paymentRoot = criteriaQuery.from(Payment.class);
            // Set up criteria query count
            criteriaQuery.select(criteriaBuilder.count(paymentRoot));

            List<Predicate> predicates = new ArrayList<>();

            // Filter base on condition
            if (filterValue != null) {
                String method = filterValue.get("method");
                String status = filterValue.get("status");

                if (method != null) {
                    predicates.add(criteriaBuilder.equal(paymentRoot.get("method"), method));
                }
                if (status != null) {
                    predicates.add(criteriaBuilder.equal(paymentRoot.get("status"), status));
                }
            }

            // Filtering based on userRole
            if (userRole != null) {
                if (userRole.equals(User.UserRole.TENANT)) {
                    Join<Payment, Tenant> tenant = paymentRoot.join("tenant", JoinType.INNER);
                    predicates.add(criteriaBuilder.equal(tenant.get("id"), userId));
                } else if (userRole.equals(User.UserRole.HOST)) {
                    Join<Payment, RentalAgreement> rentalAgreementJoin = paymentRoot.join("rentalAgreement", JoinType.INNER);
                    Join<RentalAgreement, Host> rentalAgreementHostJoin = rentalAgreementJoin.join("host", JoinType.INNER);
                    predicates.add(criteriaBuilder.equal(rentalAgreementHostJoin.get("id"), userId));
                }
            }

            // Add predicate to the query
            if (!predicates.isEmpty()) {
                criteriaQuery.where(predicates.toArray(new Predicate[0]));
            }

            Query<Long> paymentCount = session.createQuery(criteriaQuery);
            count = paymentCount.getSingleResult();

            if (count == null) return 0L;
            return count;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Error getting payment count", e);
        }
    }

    @Override
    public boolean updatePaymentStatus(long paymentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<Payment> query = session.createQuery("SELECT p FROM Payment p WHERE p.id = :id", Payment.class);
            query.setParameter("id", paymentId);
            Payment payment = query.uniqueResult();

            if (payment == null) throw new DataAccessException("No payment found");

            payment.setStatus(Payment.paymentStatus.PAID);
            session.merge(payment);

            transaction.commit();
            return true;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Error updating payment status", e);
        }
    }

    @Override
    public List<Payment> getPaymentsByStatus(Payment.paymentStatus status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Query<Payment> query = session.createQuery(
                    "SELECT p FROM Payment p WHERE p.status = :status",
                    Payment.class
            );
            query.setParameter("status", status);
            payments = query.list();
            transaction.commit();
            return payments;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Error loading data", e);
        }
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

            transaction.commit();

            return monthlyPayments;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new DataAccessException("Error retrieving monthly payments", e);
        }
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
}