package com.yourcompany.rentalmanagement.dao.impl;

import com.yourcompany.rentalmanagement.dao.RentalManagementDao;
import com.yourcompany.rentalmanagement.model.Host;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import com.yourcompany.rentalmanagement.model.ResidentialProperty;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class RentalAgreementDaoImpl implements RentalManagementDao {
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

    public static void main(String[] args) {
        RentalManagementDao test = new RentalAgreementDaoImpl();
        List<RentalAgreement> db = test.getAllRentalAgreements();
        System.out.println("======================");
        for (RentalAgreement rentalAgreement : db){
            System.out.println(rentalAgreement.getTenants());
        }
    }
}