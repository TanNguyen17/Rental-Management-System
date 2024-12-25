package com.yourcompany.rentalmanagement.dao;

import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class PaymentDaoImp implements PaymentDao {

    private Transaction transaction = null;
    private List<Payment> payments = new ArrayList<Payment>();

    public PaymentDaoImp() {
        loadData();
    }

    @Override
    public void loadData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            payments = session.createQuery("from Payment").list();

        }
    }
}
