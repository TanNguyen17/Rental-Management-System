//package com.yourcompany.rentalmanagement.data;
//
//import java.time.LocalDate;
//import java.util.*;
//
//import org.hibernate.Session;
//import org.hibernate.Transaction;
//
//import com.yourcompany.rentalmanagement.model.Address;
//import com.yourcompany.rentalmanagement.model.CommercialProperty;
//import com.yourcompany.rentalmanagement.model.Host;
//import com.yourcompany.rentalmanagement.model.Owner;
//import com.yourcompany.rentalmanagement.model.Property;
//import com.yourcompany.rentalmanagement.model.RentalAgreement;
//import com.yourcompany.rentalmanagement.model.Tenant;
//import com.yourcompany.rentalmanagement.util.HibernateUtil;
//
//public class DataGenerator {
//    private static final String[] FIRST_NAMES = {"James", "John", "Robert", "Michael", "William", "David", "Richard", "Charles", "Joseph", "Thomas", "Mary", "Patricia", "Linda", "Barbara", "Elizabeth", "Jennifer", "Maria", "Susan", "Margaret", "Dorothy"};
//    private static final String[] LAST_NAMES = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin"};
//    private static final String[] BUSINESS_TYPES = {"Office", "Retail", "Restaurant", "Warehouse", "Industrial"};
//    public static void main(String[] args) {
//            Transaction transaction = null;
//            Random random = new Random();
//            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//                transaction = session.beginTransaction();
//                List<Owner> owners = session.createQuery("from Owner", Owner.class).list();
//                List<CommercialProperty> properties = session.createQuery("from CommercialProperty", CommercialProperty.class).list();
//                List<Tenant> tenants = session.createQuery("from Tenant", Tenant.class).list();
//                List<Host> hosts = session.createQuery("from Host", Host.class).list();
//
//                for (int i = 0; i < 20; i++) {
//                    RentalAgreement rentalAgreement = new RentalAgreement();
//                    rentalAgreement.setHost(hosts.get(i));
//                    rentalAgreement.setOwner(owners.get(i));
//                    rentalAgreement.setStatus(RentalAgreement.rentalAgreementStatus.NEW);
//                    rentalAgreement.setContractDate(generateRandomDate(random));
//                    rentalAgreement.setRentingFee(generateRandomPrice(random));
//                    properties.get(i).setRentalAgreement(rentalAgreement);
//                    tenants.get(i).addRentalAgreement(rentalAgreement);
//                    session.persist(rentalAgreement);
//                }
//
//                //Commit transaction
//                transaction.commit();
//            } catch (Exception e) {
//                if (transaction != null) {
//                    transaction.rollback();
//                }
//                e.printStackTrace();
//            }
//    }
//
//    private static String generateUsername(String firstName, String lastName, Random random) {
//        String baseUsername = firstName.toLowerCase() + lastName.toLowerCase();
//        int randomNumber = random.nextInt(100); // Add a random number to avoid duplicates.
//        return baseUsername + randomNumber;
//    }
//
//    private static Address generateRandomAddress(Random random) {
//        Address address = new Address();
//        address.setNumber(String.valueOf(random.nextInt(1000) + 1)); // Random number between 1 and 1000
//        address.setStreet(generateRandomStreetName(random));
//        address.setCity(generateRandomCityName(random));
//        address.setState(generateRandomState(random));
//        return address;
//    }
//
//    private static String generateRandomStreetName(Random random) {
//        String[] streetNames = {"Main St", "Oak Ave", "Pine Ln", "Maple Dr", "Cedar Rd"};
//        return streetNames[random.nextInt(streetNames.length)];
//    }
//
//    private static String generateRandomCityName(Random random) {
//        String[] cityNames = {"New York", "Los Angeles", "Chicago", "Houston", "Phoenix"};
//        return cityNames[random.nextInt(cityNames.length)];
//    }
//
//    private static String generateRandomState(Random random) {
//        String[] states = {"AL", "AK", "AZ", /* ... all states */ "WV", "WI", "WY"};
//        return states[random.nextInt(states.length)];
//    }
//
//    private static String generateRandomPhoneNumber(Random random) {
//        return String.format("%03d-%03d-%04d",
//                random.nextInt(1000), random.nextInt(1000), random.nextInt(10000));
//    }
//
//    private static LocalDate generateRandomDate(Random random) {
//        long minDay = LocalDate.of(1950, 1, 1).toEpochDay();
//        long maxDay = LocalDate.of(2005, 1, 1).toEpochDay();
//        long randomDay = minDay + random.nextInt((int)(maxDay - minDay));
//        return LocalDate.ofEpochDay(randomDay);
//    }
//
//    private static double generateRandomPrice(Random random) {
//        return 100000 + (2000000 - 100000) * random.nextDouble(); // Price between $100k and $2.1M
//    }
//
//    private static double generateRandomSquareFootage(Random random) {
//        return 500 + (10000 - 500) * random.nextDouble(); // Square footage between 500 and 10,500
//    }
//
//    private static Property.propertyStatus generateRandomPropertyStatus(Random random) {
//        Property.propertyStatus[] statuses = Property.propertyStatus.values();
//        return statuses[random.nextInt(statuses.length)];
//    }
//}