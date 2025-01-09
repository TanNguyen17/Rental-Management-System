package com.yourcompany.rentalmanagement;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.yourcompany.rentalmanagement.model.Host;
import com.yourcompany.rentalmanagement.model.Owner;
import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import com.yourcompany.rentalmanagement.model.Tenant;
import com.yourcompany.rentalmanagement.util.HibernateUtil;

public class HibernateTest {
//    private static final String[] STREET_NAMES = {"Main St", "Elm St", "Maple Ave", "Oak St", "Pine St"};
//    private static final String[] CITY_NAMES = {"Springfield", "Riverdale", "Sunnyvale", "Mountain View", "Palo Alto"};
//    private static final String[] STATES = {"CA", "TX", "NY", "FL", "WA"};
//    private static final String[] BUSINESS_TYPES = {"Retail", "Office", "Restaurant", "Warehouse", "Industrial"};
//    private static final String[] FIRST_NAMES = {"Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace", "Henry", "Ivy", "Jack", "Kelly", "Liam", "Mia", "Noah", "Olivia", "Peter", "Quinn", "Ryan", "Sophia", "Tom"};
//    private static final String[] LAST_NAMES = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Wilson", "Martinez", "Anderson", "Taylor", "Thomas", "Hernandez", "Moore", "Martin", "Jackson", "Thompson", "White"};
    private static final String[] PAYMENT_METHODS = {"Credit Card", "Debit Card", "PayPal", "Bank Transfer"};
    private static final String[] PAYMENT_STATUSES = {"Pending", "Completed", "Failed"};

    public static void main(String[] args) {
        Transaction transaction = null;
        Random random = new Random();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            transaction = session.beginTransaction();
//            List<Owner> owners = session.createQuery("from Owner", Owner.class).list();
//            List<Host> hosts = session.createQuery("from Host", Host.class).list();
//            List<Tenant> tenants = session.createQuery("from Tenant", Tenant.class).list();
            List<RentalAgreement> rentalAgreements = session.createQuery("from RentalAgreement", RentalAgreement.class).list();
            System.out.println(rentalAgreements.size());

//            for (int i = 0; i < 20; i++) {
//                Payment payment = new Payment();
//
//                String receipt = UUID.randomUUID().toString().substring(0, 10); // Generate unique receipt
//                String method = PAYMENT_METHODS[random.nextInt(PAYMENT_METHODS.length)];
//                double amount = 500 + random.nextInt(2000); // Random amount between 500 and 2500
//                String status = PAYMENT_STATUSES[random.nextInt(PAYMENT_STATUSES.length)];
//
//                payment.setReceipt(receipt);
//                payment.setMethod(method);
//                payment.setAmount(amount);
//                payment.setStatus(status);
//
//                payment.setTenant(tenants.get(i));
//                payment.setRentalAgreement(rentalAgreements.get(i));
//
//                session.persist(payment);
//            }


//            //Commit transaction
//            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

//        List<Tenant> tenants = new ArrayList<>();
//        Random random = new Random();
//        Transaction transaction = null;
//        for (int i = 0; i < 20; i++) {
//            Tenant tenant = new Tenant();
//
//            String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
//            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
//            String username = (firstName + lastName + i).toLowerCase(); // Ensure uniqueness
//            String email = username + "@example.com";
//            String phoneNumber = String.format("123-456-%04d", random.nextInt(10000)); // Format phone number
//            LocalDate dob = LocalDate.of(1970 + random.nextInt(50), random.nextInt(12) + 1, random.nextInt(28) + 1); // Random date of birth
//            String password = "password" + i; // In real application, HASH the password
//
//            tenant.setUsername(username);
//            tenant.setEmail(email);
//            tenant.setPhoneNumber(phoneNumber);
//            tenant.setDob(dob);
//            tenant.setPassword(password); // Remember to hash this in a real app
//
//            //Get Session
//            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//                transaction = session.beginTransaction();
//
//                //Save the Model object
//                session.persist(tenant);
//                //Commit transaction
//                transaction.commit();
//
//            } catch (Exception e) {
//                if (transaction != null) {
//                    transaction.rollback();
//                }
//                e.printStackTrace();
//            }
//
//            tenants.add(tenant);
//        }



//
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            transaction = session.beginTransaction();
//            List<Owner> owners = session.createQuery("from Owner", Owner.class).list();
//            List<Host> hosts = session.createQuery("from Host", Host.class).list();
//            owners.forEach(o -> {
//                o.addHost(hosts.get(0));
//                hosts.get(0).addOwner(o);
//                System.out.println("Print owner name : " + o.getUsername());
//            });
//            transaction.commit();
//
//        } catch (Exception e) {
//            if (transaction != null) {
//                transaction.rollback();
//            }
//            e.printStackTrace();
//        }
//    }

// The relationship between Owner and Host is Many to Many, so we need to create a new table to store the relationship between them. In this case, we create a new table called owner_host. The owner_host table has two columns: owner_id and host_id. When we add a new owner to a host, we need to add a new record to the owner_host table with the owner_id and host_id. The same thing happens when we add a new host to an owner. We need to add a new record to the owner_host table with the owner_id and host_id. This is how we create a Many to Many relationship in Hibernate.
    }
}