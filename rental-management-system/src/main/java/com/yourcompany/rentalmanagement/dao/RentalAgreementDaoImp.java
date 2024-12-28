package com.yourcompany.rentalmanagement.dao;

import com.yourcompany.rentalmanagement.model.Owner;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class RentalAgreementDaoImp implements RentalAgreementDao {
    private List<RentalAgreement> rentalAgreements;
    private Transaction transaction;

    public RentalAgreementDaoImp() {
        transaction = null;
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
}
