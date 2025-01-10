package com.yourcompany.rentalmanagement.dao.impl;

import com.yourcompany.rentalmanagement.dao.RentalAgreementDao;
import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RentalAgreementDaoImpl implements RentalAgreementDao {
    private List<RentalAgreement> rentalAgreements;
    private Query<RentalAgreement> query;
    private RentalAgreement rentalAgreement;
    private Transaction transaction;

    public RentalAgreementDaoImpl() {
        transaction = null;
        rentalAgreements = new ArrayList<>();
    }

    @Override
    public List<RentalAgreement> getAllRentalAgreements() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            query = session.createQuery("from RentalAgreement", RentalAgreement.class);
            return query.list();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return rentalAgreements;
    }

    @Override
    public RentalAgreement getRentalAgreementById(long id){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            rentalAgreement = session.get(RentalAgreement.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rentalAgreement;
    }

    @Override
    public void loadData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            rentalAgreements = session.createQuery("from RentalAgreement ", RentalAgreement.class).list();
            //rentalAgreements.forEach(System.out::println);
            System.out.println(rentalAgreements.size());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public List<RentalAgreement> getRentalAgreementsById(long id){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            rentalAgreements = session.createQuery("FROM RentalAgreement r WHERE r.host.id = :hostId", RentalAgreement.class).setParameter("hostId", id).getResultList();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return rentalAgreements;
    };

    public List<Payment> getRelatedPayments(List<RentalAgreement> rentalAgreements) {
        List<Payment> relatedPayments = new ArrayList<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Collect IDs of all rental agreements
            List<Long> rentalAgreementIds = rentalAgreements.stream()
                    .map(RentalAgreement::getId)
                    .collect(Collectors.toList());

            // Query to fetch payments related to all rental agreements
            relatedPayments = session.createQuery("FROM Payment p WHERE p.rentalAgreement.id IN :rentalAgreementIds", Payment.class)
                    .setParameter("rentalAgreementIds", rentalAgreementIds)
                    .getResultList();

            // Commit the transaction
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return relatedPayments;
    }

    @Override
    public void createRentalAgreement(RentalAgreement rentalAgreement) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(rentalAgreement);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

    }
    @Override
    public void updateRentalAgreement(RentalAgreement rentalAgreement) {

    }
    @Override
    public void deleteRentalAgreement(RentalAgreement rentalAgreement) {}



    public static void main(String[] args) {
        RentalAgreementDao test = new RentalAgreementDaoImpl();
        List<RentalAgreement> db = test.getAllRentalAgreements();
        System.out.println("======================");
        for (RentalAgreement rentalAgreement : db){
            System.out.println(rentalAgreement.getTenants());
        }
    }
}