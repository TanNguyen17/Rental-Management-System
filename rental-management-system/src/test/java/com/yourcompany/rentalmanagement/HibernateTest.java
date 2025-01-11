package com.yourcompany.rentalmanagement;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.yourcompany.rentalmanagement.dao.impl.HostDaoImpl;
import com.yourcompany.rentalmanagement.dao.impl.OwnerDaoImpl;
import com.yourcompany.rentalmanagement.dao.impl.TenantDaoImpl;
import com.yourcompany.rentalmanagement.model.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.yourcompany.rentalmanagement.util.HibernateUtil;

public class HibernateTest {
    private static final String[] STREET_NAMES = {"Main St", "Elm St", "Maple Ave", "Oak St", "Pine St"};
    private static final String[] CITY_NAMES = {"Springfield", "Riverdale", "Sunnyvale", "Mountain View", "Palo Alto"};
    private static final String[] STATES = {"CA", "TX", "NY", "FL", "WA"};
    private static final String[] BUSINESS_TYPES = {"Retail", "Office", "Restaurant", "Warehouse", "Industrial"};
    private static final String[] FIRST_NAMES = {"Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace", "Henry", "Ivy", "Jack", "Kelly", "Liam", "Mia", "Noah", "Olivia", "Peter", "Quinn", "Ryan", "Sophia", "Tom"};
    private static final String[] LAST_NAMES = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Wilson", "Martinez", "Anderson", "Taylor", "Thomas", "Hernandez", "Moore", "Martin", "Jackson", "Thompson", "White"};
    private static final String[] PAYMENT_METHODS = {"Credit Card", "Debit Card", "PayPal", "Bank Transfer"};
    private static final String[] PAYMENT_STATUSES = {"Pending", "Completed", "Failed"};

    public static void main(String[] args) {
        Transaction transaction = null;
        TenantDaoImpl tenantDaoImp = new TenantDaoImpl();
        HostDaoImpl hostDaoImp = new HostDaoImpl();
        OwnerDaoImpl ownerDaoImpl = new OwnerDaoImpl();

        Random random = new Random();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            //List<Payment> payments = session.createQuery("FROM Payment", Payment.class).list();
            //List<Owner> owners = session.createQuery("FROM Owner", Owner.class).list();
            //System.out.println(owners.size());
            List<Host> hosts = session.createQuery("FROM Host ", Host.class).list();
//            System.out.println(hosts.size());
//            List<Tenant> tenants = session.createQuery("FROM Tenant ", Tenant.class).list();
//            System.out.println(tenants.size());
//            List<RentalAgreement> rentalAgreements = session.createQuery("FROM RentalAgreement", RentalAgreement.class).list();
            List<CommercialProperty> commercialProperties = session.createQuery("FROM CommercialProperty ", CommercialProperty.class).list();
            List<ResidentialProperty> residentialProperties = session.createQuery("FROM ResidentialProperty ", ResidentialProperty.class).list();
            for (int i = 0; i < hosts.size(); i++) {
                try {
//                        Tenant tenant = new Tenant();
//                        tenant.setUsername(FIRST_NAMES[i] + " " + LAST_NAMES[i]);
//                        tenant.setPassword(FIRST_NAMES[i] + 123);
//                        tenant.setDob(generateRandomDate(random));
//                        tenant.setEmail(FIRST_NAMES[i] + "@gmail.com");
//                        tenant.setPhoneNumber(generateRandomPhoneNumber(random));
//                        tenant.setAddress(generateRandomAddress(random));
//                        tenant.setRole(UserRole.TENANT);

//                        Host host = new Host();
//                        host.setUsername(LAST_NAMES[i] + " " +FIRST_NAMES[random.nextInt(20)]);
//                        host.setPassword(FIRST_NAMES[random.nextInt(20)] + 321);
//                        host.setDob(generateRandomDate(random));
//                        host.setEmail(LAST_NAMES[i] + "@gmail.com");
//                        host.setPhoneNumber(generateRandomPhoneNumber(random));
//                        host.setAddress(generateRandomAddress(random));
//                        host.setRole(UserRole.HOST);
//
//                        Owner owner = new Owner();
//                        owner.setUsername(LAST_NAMES[19-i] + " " +FIRST_NAMES[i]);
//                        owner.setPassword(FIRST_NAMES[i] + 222);
//                        owner.setDob(generateRandomDate(random));
//                        owner.setEmail(LAST_NAMES[19-i] + "@gmail.com");
//                        owner.setPhoneNumber(generateRandomPhoneNumber(random));
//                        owner.setAddress(generateRandomAddress(random));
//                        owner.setRole(UserRole.OWNER);

//                        RentalAgreement rentalAgreement = new RentalAgreement();
//                        rentalAgreement.setStatus(generateRandomRentalAgreementStatus(random));
//                        rentalAgreement.setContractDate(generateRandomDate(random));
//                        rentalAgreement.setRentingFee(generateRandomPrice(random));
//                        rentalAgreement.setOwner(owners.get(i));
//                        rentalAgreement.setHost(hosts.get(i));
//                        tenants.get(i).addRentalAgreement(rentalAgreement);



//
//                        ResidentialProperty residentialProperty = new ResidentialProperty();
//                        residentialProperty.setAddress(generateRandomAddress(random));
//                        residentialProperty.setPrice(generateRandomPrice(random));
//                        residentialProperty.setStatus(generateRandomPropertyStatus(random));
//                        residentialProperty.setImageLink("https://res.cloudinary.com/dqydgahsj/image/upload/v1736302426/jasoag8sgnv4iravwrax.jpg");
//                        residentialProperty.setOwner(owners.get(i));
//                        residentialProperty.setTitle("Residential");
//                        residentialProperty.setRentalAgreement(rentalAgreements.get(i+20));
//                        residentialProperty.setCreatedAt(generateRandomCreatedDate(random));
//                        residentialProperty.setLastUpdated(generateRandomUpdatedDate(random));
//                        residentialProperty.setNumberOfBedrooms(generateRandomNumberOfBedrooms(random));
//                        residentialProperty.setPetFriendliness(generateRandomParkingSpace(random));
//                        residentialProperty.setGardenAvailability(generateRandomParkingSpace(random));

//                    Payment payment = new Payment();
//                    payment.setReceipt("Receipt" + i);
//                    payment.setMethod(generateRandomPaymentMethod(random));
//                    payment.setAmount(generateRandomPrice(random));
//                    payment.setStatus(generateRandomPaymentStatus(random));
//                    payment.setRentalAgreement(rentalAgreements.get(i));
//                    if (i == 20) {
//                        payment.setTenant(tenants.get(0));
//                    } else if (i>20) {
//                        payment.setTenant(tenants.get(40 - i));
//                    } else {
//                        payment.setTenant(tenants.get(i));
//                    }
//                    payment.setDueDate(generateRandomDueDate(random));

                    //payments.get(i).setDueDate(generateRandomDueDate(random));
                    // Persist rental agreement
                    hosts.get(i).addCommercialProperty(commercialProperties.get(i));
                    hosts.get(i).addResidentialProperty(residentialProperties.get(i));
                    session.persist(hosts.get(i));
                } catch (Exception e) {
                    System.err.println("Error while persisting payment at index " + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    private static String generateUsername(String firstName, String lastName, Random random) {
        String baseUsername = firstName.toLowerCase() + lastName.toLowerCase();
        int randomNumber = random.nextInt(100); // Add a random number to avoid duplicates.
        return baseUsername + randomNumber;
    }

    private static String generateRandomPaymentMethod(Random random) {
        String[] methods = {"CASH", "CARD", "ONLINE", "BANK_TRANSFER"};
        return methods[random.nextInt(methods.length)];
    }

    private static Address generateRandomAddress(Random random) {
        Address address = new Address();
        address.setNumber(String.valueOf(random.nextInt(1000) + 1)); // Random number between 1 and 1000
        address.setStreet(generateRandomStreetName(random));
        address.setCity(generateRandomCityName(random));
        return address;
    }

    private static String generateRandomStreetName(Random random) {
        return STREET_NAMES[random.nextInt(STREET_NAMES.length)];
    }

    private static String generateRandomCityName(Random random) {
        return CITY_NAMES[random.nextInt(CITY_NAMES.length)];
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
        long minDay = LocalDate.of(2020, 1, 1).toEpochDay();
        long maxDay = LocalDate.of(2024, 1, 1).toEpochDay();
        long randomDay = minDay + random.nextInt((int)(maxDay - minDay));
        return LocalDate.ofEpochDay(randomDay);
    }

    private static LocalDate generateRandomDueDate(Random random) {
        // Generate a random year between a reasonable range (e.g., 2020 to 2030)
        int year = random.nextInt(2024 - 2020 + 1) + 2020;

        // Generate a random month (1 to 12)
        int month = random.nextInt(12) + 1;

        // Get the last day of the month
        YearMonth yearMonth = YearMonth.of(year, month);
        int lastDayOfMonth = yearMonth.lengthOfMonth();

        // Create a LocalDate for the last day of the randomly generated month and year
        return LocalDate.of(year, month, lastDayOfMonth);
    }

    private static LocalDate generateRandomCreatedDate(Random random) {
        long minDay = LocalDate.of(1980, 1, 1).toEpochDay();
        long maxDay = LocalDate.of(2000, 1, 1).toEpochDay();
        long randomDay = minDay + random.nextInt((int)(maxDay - minDay));
        return LocalDate.ofEpochDay(randomDay);
    }

    private static LocalDate generateRandomUpdatedDate(Random random) {
        long minDay = LocalDate.of(2001, 1, 1).toEpochDay();
        long maxDay = LocalDate.of(2024, 1, 1).toEpochDay();
        long randomDay = minDay + random.nextInt((int)(maxDay - minDay));
        return LocalDate.ofEpochDay(randomDay);
    }

    private static double generateRandomPrice(Random random) {
        return 100000 + (2000000 - 100000) * random.nextDouble(); // Price between $100k and $2.1M
    }

    private static double generateRandomSquareFootage(Random random) {
        return 500 + (10000 - 500) * random.nextDouble(); // Square footage between 500 and 10,500
    }

    private static RentalAgreement.rentalAgreementStatus generateRandomRentalAgreementStatus(Random random) {
        RentalAgreement.rentalAgreementStatus[] statuses = RentalAgreement.rentalAgreementStatus.values();
        return statuses[random.nextInt(statuses.length)];
    }

    private static Property.propertyStatus generateRandomPropertyStatus(Random random) {
        Property.propertyStatus[] statuses = Property.propertyStatus.values();
        return statuses[random.nextInt(statuses.length)];
    }

    private static Payment.paymentStatus generateRandomPaymentStatus(Random random) {
        Payment.paymentStatus[]  statuses = Payment.paymentStatus.values();
        return statuses[random.nextInt(statuses.length)];
    }

    private static String generateRandomBusinessType(Random random) {
        return BUSINESS_TYPES[random.nextInt(BUSINESS_TYPES.length)];
    }

    private static boolean generateRandomParkingSpace(Random random) {
        return random.nextBoolean();
    }
    private static int generateRandomNumberOfBedrooms(Random random) {
        int min = 1;
        int max = 5;
        return random.nextInt((max - min) + 1) + min; // Generates a random number between min and max
    }
}

// The relationship between Owner and Host is Many to Many, so we need to create a new table to store the relationship between them. In this case, we create a new table called owner_host. The owner_host table has two columns: owner_id and host_id. When we add a new owner to a host, we need to add a new record to the owner_host table with the owner_id and host_id. The same thing happens when we add a new host to an owner. We need to add a new record to the owner_host table with the owner_id and host_id. This is how we create a Many to Many relationship in Hibernate.
