package com.yourcompany.rentalmanagement;

import com.yourcompany.rentalmanagement.model.Host;
import com.yourcompany.rentalmanagement.model.Owner;
import org.hibernate.Session;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class HibernateTest {

    public static void main(String[] args) {
        Host host = new Host();
        host.setUsername("hahah");
        host.setPassword("hahah");
        host.setEmail("hahah@gmail.com");
        host.setDob(LocalDate.now());
        host.setPhoneNumber("0859433676");
        host.setProfileImage("hahah");

        Transaction transaction = null;

        //Get Session
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            transaction = session.beginTransaction();
//
//            //Save the Model object
//            session.persist(host);
//            //Commit transaction
//            transaction.commit();
//            System.out.println("Owner ID=" + host.getId());
//
//        } catch (Exception e) {
//            if (transaction != null) {
//                transaction.rollback();
//            }
//            e.printStackTrace();
//        }
//
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            List<Owner> owners = session.createQuery("from Owner", Owner.class).list();
            List<Host> hosts = session.createQuery("from Host", Host.class).list();
            owners.forEach(o -> {
                o.addHost(hosts.get(0));
                hosts.get(0).addOwner(o);
                System.out.println("Print owner name : " + o.getUsername());
            });

//            session.persist(owners);
//            session.persist(hosts);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

}