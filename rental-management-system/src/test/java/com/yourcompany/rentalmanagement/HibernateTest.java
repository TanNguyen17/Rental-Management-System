package com.yourcompany.rentalmanagement;

import com.yourcompany.rentalmanagement.model.Owner;
import org.hibernate.Session;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class HibernateTest {

    public static void main(String[] args) {
        Owner owner = new Owner();
        owner.setUsername("ManhTan");
        owner.setPassword("ManhTan");
        owner.setEmail("ManhTan@gmail.com");
        owner.setDob(LocalDate.now());
        owner.setPhoneNumber("0859493676");
        owner.setProfileImage("sfsfsfsf");

        Transaction transaction = null;

        //Get Session
        Session session = HibernateUtil.getSessionFactory().openSession();
        try (session) {
            transaction = session.beginTransaction();

            //Save the Model object
            session.persist(owner);
            //Commit transaction
            transaction.commit();
            System.out.println("Owner ID=" + owner.getId());

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
//
//        try (session) {
//            List<Owner> books = session.createQuery("from Owner", Owner.class).list();
//            books.forEach(o -> {
//                System.out.println("Print book name : " + o.getUsername());
//            });
//            session.close();
//        } catch (Exception e) {
//            if (transaction != null) {
//                transaction.rollback();
//            }
//            e.printStackTrace();
//        } finally {
//            session.close();
//        }
    }

}