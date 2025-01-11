package com.yourcompany.rentalmanagement.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.yourcompany.rentalmanagement.model.Address;
import com.yourcompany.rentalmanagement.model.Host;
import com.yourcompany.rentalmanagement.model.Owner;
import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import com.yourcompany.rentalmanagement.model.Tenant;
import com.yourcompany.rentalmanagement.model.UserRole;
import com.yourcompany.rentalmanagement.util.HibernateUtil;

public class PaymentDataGenerator {

    private static final String[] FIRST_NAMES = {"James", "John", "Robert", "Michael", "William", "David", "Richard", "Charles", "Joseph", "Thomas", "Mary", "Patricia", "Linda", "Barbara", "Elizabeth", "Jennifer", "Maria", "Susan", "Margaret", "Dorothy"};
    private static final String[] LAST_NAMES = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin"};
    private static final String[] BUSINESS_TYPES = {"Office", "Retail", "Restaurant", "Warehouse", "Industrial"};
    private static final String[] PAYMENT_METHODS = {"Credit Card", "Debit Card", "PayPal", "Bank Transfer"};
    private static final Payment.paymentStatus[] PAYMENT_STATUSES = Payment.paymentStatus.values();

    public static void generateTestData() {
        Transaction transaction = null;
        Session session = null;
        Random random = new Random();

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            // Get existing data
            List<Owner> owners = session.createQuery("from Owner", Owner.class).list();
            List<Host> hosts = session.createQuery("from Host", Host.class).list();

            // Get both types of properties
            List<Property> properties = new ArrayList<>();
            properties.addAll(session.createQuery("from ResidentialProperty", Property.class).list());
            properties.addAll(session.createQuery("from CommercialProperty", Property.class).list());

            if (owners.isEmpty() || hosts.isEmpty() || properties.isEmpty()) {
                System.out.println("Required data missing. Please run PropertyDataGenerator first.");
                return;
            }

            // Check if we already have payment data
            Long paymentCount = session.createQuery("SELECT COUNT(p) FROM Payment p", Long.class)
                    .uniqueResult();
            if (paymentCount > 0) {
                System.out.println("Payment data already exists. Skipping generation.");
                transaction.commit();
                return;
            }

            // Create tenants if they don't exist
            List<Tenant> tenants = createTenants(session, 20);

            // Create agreements based on available data
            int maxAgreements = Math.min(Math.min(
                    Math.min(owners.size(), hosts.size()),
                    Math.min(properties.size(), tenants.size())
            ), 20);

            // Create rental agreements and payments
            for (int i = 0; i < maxAgreements; i++) {
                // Create rental agreement
                RentalAgreement rentalAgreement = new RentalAgreement();
                rentalAgreement.setHost(hosts.get(i % hosts.size()));
                rentalAgreement.setOwner(owners.get(i % owners.size()));
                rentalAgreement.setStatus(RentalAgreement.rentalAgreementStatus.NEW);
                rentalAgreement.setStartContractDate(generateRandomDate(random));
                rentalAgreement.setRentingFee(generateRandomPrice(random));

                Property property = properties.get(i % properties.size());
                property.setRentalAgreement(rentalAgreement);

                Tenant tenant = tenants.get(i % tenants.size());
                tenant.addRentalAgreement(rentalAgreement);

                session.persist(rentalAgreement);

                // Create one payment per agreement
                Payment payment = new Payment();
                payment.setReceipt(generateReceipt());
                payment.setMethod(PAYMENT_METHODS[random.nextInt(PAYMENT_METHODS.length)]);
                payment.setAmount(rentalAgreement.getRentingFee());
                payment.setStatus(PAYMENT_STATUSES[random.nextInt(PAYMENT_STATUSES.length)]);
                payment.setDueDate(generateRandomDate(random));
                payment.setTenant(tenant);
                payment.setRentalAgreement(rentalAgreement);

                session.persist(payment);
            }

            transaction.commit();
        } catch (Exception e) {
            System.err.println("Error generating payment data: " + e.getMessage());
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    private static String generateUsername(String firstName, String lastName, Random random) {
        String baseUsername = firstName.toLowerCase() + lastName.toLowerCase();
        int randomNumber = random.nextInt(100); // Add a random number to avoid duplicates.
        return baseUsername + randomNumber;
    }

    private static Address generateRandomAddress(Random random) {
        Address address = new Address();
        address.setNumber(String.valueOf(random.nextInt(1000) + 1));
        address.setStreet(generateRandomStreetName(random));
        address.setCity(generateRandomCityName(random));
        address.setDistrict("District " + (random.nextInt(20) + 1));
        address.setWard("Ward " + (random.nextInt(10) + 1));
        return address;
    }

    private static String generateRandomStreetName(Random random) {
        String[] streetNames = {
            "Main Street", "Oak Avenue", "Pine Lane", "Maple Drive",
            "Cedar Road", "Elm Street", "Park Avenue", "Lake View",
            "Forest Drive", "River Road"
        };
        return streetNames[random.nextInt(streetNames.length)];
    }

    private static String generateRandomCityName(Random random) {
        String[] cityNames = {
            "Ho Chi Minh City", "Ha Noi", "Da Nang",
            "Can Tho", "Hai Phong", "Nha Trang",
            "Vung Tau", "Da Lat", "Hue", "Quy Nhon"
        };
        return cityNames[random.nextInt(cityNames.length)];
    }

    private static String generateRandomState(Random random) {
        String[] states = {"AL", "AK", "AZ", /* ... all states */ "WV", "WI", "WY"};
        return states[random.nextInt(states.length)];
    }

    private static String generateRandomPhoneNumber(Random random) {
        return String.format("%03d-%03d-%04d",
                random.nextInt(1000), random.nextInt(1000), random.nextInt(10000));
    }

    private static LocalDate generateRandomDate(Random random) {
        long minDay = LocalDate.of(1950, 1, 1).toEpochDay();
        long maxDay = LocalDate.of(2005, 1, 1).toEpochDay();
        long randomDay = minDay + random.nextInt((int) (maxDay - minDay));
        return LocalDate.ofEpochDay(randomDay);
    }

    private static double generateRandomPrice(Random random) {
        return 100000 + (2000000 - 100000) * random.nextDouble(); // Price between $100k and $2.1M
    }

    private static double generateRandomSquareFootage(Random random) {
        return 500 + (10000 - 500) * random.nextDouble(); // Square footage between 500 and 10,500
    }

    private static Property.propertyStatus generateRandomPropertyStatus(Random random) {
        Property.propertyStatus[] statuses = Property.propertyStatus.values();
        return statuses[random.nextInt(statuses.length)];
    }

    private static List<Tenant> createTenants(Session session, int count) {
        List<Tenant> tenants = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            Tenant tenant = new Tenant();
            String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];

            tenant.setUsername(generateUsername(firstName, lastName, random));
            tenant.setEmail(tenant.getUsername() + "@test.com");
            tenant.setHashedPassword("password123");
            tenant.setRole(UserRole.TENANT);

            // Set address
            Address address = generateRandomAddress(random);
            session.persist(address);
            tenant.setAddress(address);

            session.persist(tenant);
            tenants.add(tenant);
        }

        return tenants;
    }

    private static String generateReceipt() {
        return UUID.randomUUID().toString().substring(0, 10);
    }
}