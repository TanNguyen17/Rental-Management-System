package com.yourcompany.rentalmanagement;
/**
 * @author FTech
 */
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import com.yourcompany.rentalmanagement.dao.impl.HostDaoImpl;
import com.yourcompany.rentalmanagement.dao.impl.OwnerDaoImpl;
import com.yourcompany.rentalmanagement.dao.impl.TenantDaoImpl;
import com.yourcompany.rentalmanagement.model.*;
import com.yourcompany.rentalmanagement.util.AddressData;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

//import com.yourcompany.rentalmanagement.util.HibernateUtil;
import org.hibernate.cfg.Configuration;

public class HibernateTest {
    private static final String[] STREET_NAMES = {"Main St", "Elm St", "Maple Ave", "Oak St", "Pine St"};
    private static final String[] CITY_NAMES = {"Springfield", "Riverdale", "Sunnyvale", "Mountain View", "Palo Alto"};
    private static final String[] STATES = {"CA", "TX", "NY", "FL", "WA"};
    private static final String[] BUSINESS_TYPES = {"Retail", "Office", "Restaurant", "Warehouse", "Industrial"};
//    private static final String[] FIRST_NAMES = {
//            "Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace",
//            "Henry", "Ivy", "Jack", "Kelly", "Liam", "Mia", "Noah",
//            "Olivia", "Peter", "Quinn", "Ryan", "Sophia", "Tom"
//    };
    private static final String[] LAST_NAMES = {
            "An", "Binh", "Chi", "Dung", "Em", "Phuc", "Hanh",
            "Hoang", "Khanh", "Lan", "Linh", "Minh", "Ngoc", "Nam",
            "Quynh", "Son", "Thu", "Trang", "Van", "Tuan"
    };
    private static final String[] FIRST_NAMES = {
            "Nguyen", "Tran", "Le", "Pham", "Hoang", "Vu",
            "Vo", "Dang", "Bui", "Do", "Ho", "Ngo",
            "Duong", "Ly", "Truong", "Dinh", "Huynh", "Phan",
            "Cao", "Trinh"
    };



//    private static final String[] LAST_NAMES = {
//            "Anderson", "Brown", "Davis", "Garcia", "Hernandez", "Jackson",
//            "Johnson", "Jones", "Martin", "Martinez", "Miller", "Moore",
//            "Rodriguez", "Smith", "Taylor", "Thomas", "Thompson", "White",
//            "Williams", "Wilson"
//    };
    static HostDaoImpl hostDaoImp = new HostDaoImpl();
    static Host host = hostDaoImp.getHostById(58);

    public static void main(String[] args) {
        Transaction transaction = null;
        LocalDate[] contractDates;
        LocalDateTime[] createdAndUpdatedDates;
        TenantDaoImpl tenantDaoImp = new TenantDaoImpl();
        HostDaoImpl hostDaoImp = new HostDaoImpl();
        OwnerDaoImpl ownerDaoImpl = new OwnerDaoImpl();
        List<Address> addresses = new ArrayList<>();
        addresses.add(new Address("15", "Le Van Sy", "Ward 14", "District 3", "Ho Chi Minh City"));
        addresses.add(new Address("37", "Nguyen Hue", "Ward Ben Nghe", "District 1", "Ho Chi Minh City"));
        addresses.add(new Address("56", "Pham Van Dong", "Ward Hoa Khanh Bac", "Lien Chieu District", "Da Nang"));
        addresses.add(new Address("44", "Nguyen Van Cu", "Ward An Hai Dong", "Son Tra District", "Da Nang"));
        addresses.add(new Address("25", "Hoang Van Thu", "Ward Quang Trung", "Ha Dong District", "Hanoi"));
        addresses.add(new Address("78", "Nguyen Du", "Ward Hang Bac", "Hoan Kiem District", "Hanoi"));
        addresses.add(new Address("50", "Nguyen Van Linh", "Ward Hoa Phu", "Thu Dau Mot City", "Binh Duong"));
        addresses.add(new Address("91", "Ly Thuong Kiet", "Ward Van Mieu", "Nam Dinh City", "Nam Dinh"));
        addresses.add(new Address("62", "Cach Mang Thang 8", "Ward Hoa Cuong Nam", "Hai Chau District", "Da Nang"));
        addresses.add(new Address("18", "Hung Vuong", "Ward Phu Hoi", "Hue City", "Thua Thien Hue"));
        addresses.add(new Address("29", "Nguyen Trai", "Ward Van Quyet", "Son La City", "Son La"));
        addresses.add(new Address("64", "Hai Ba Trung", "Ward Trung Trac", "Vinh Yen City", "Vinh Phuc"));
        addresses.add(new Address("83", "Dien Bien Phu", "Ward Dakao", "District 1", "Ho Chi Minh City"));
        addresses.add(new Address("48", "Nguyen Thi Minh Khai", "Ward Ben Thanh", "District 1", "Ho Chi Minh City"));
        addresses.add(new Address("39", "Tran Hung Dao", "Ward Chau Van Liem", "O Mon District", "Can Tho"));
        addresses.add(new Address("72", "Vo Nguyen Giap", "Ward My An", "Ngu Hanh Son District", "Da Nang"));
        addresses.add(new Address("85", "Nguyen Thi Dinh", "Ward Binh Trung Tay", "Thu Duc City", "Ho Chi Minh City"));
        addresses.add(new Address("31", "Tran Phu", "Ward Lam Son", "Thanh Hoa City", "Thanh Hoa"));
        addresses.add(new Address("43", "To Hieu", "Ward Le Chan", "Le Chan District", "Hai Phong"));
        addresses.add(new Address("99", "Tran Phu", "Ward Cam Chau", "Cam Ranh City", "Khanh Hoa"));



        Random random = new Random();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            for (int i = 5; i < 6; i++) {
                try {
                    Tenant tenant = new Tenant();
                    tenant.setUsername(FIRST_NAMES[i] + " " + LAST_NAMES[i]);
                    tenant.setPassword(FIRST_NAMES[i] + 123);
                    tenant.setHashedPassword(FIRST_NAMES[i] + 123);
                    tenant.setDob(generateRandomDate(random));
                    tenant.setEmail(FIRST_NAMES[i] + "@gmail.com");
                    tenant.setProfileImage("https://res.cloudinary.com/dqydgahsj/image/upload/v1736670115/zguznvwm7ib3exi1g1ko.png");
                    tenant.setPhoneNumber(generateRandomPhoneNumber(random));
                    tenant.setAddress(generateRandomAddress(random, addresses));
                    tenant.setRole(UserRole.TENANT);


//                    Host host = new Host();
//                    host.setUsername("host");
//                    host.setPassword("bread1102");
//                    host.setHashedPassword("bread1102" + 321);
//                    host.setDob(generateRandomDate(random));
//                    host.setEmail(LAST_NAMES[i] + "@gmail.com");
//                    host.setProfileImage("https://res.cloudinary.com/dqydgahsj/image/upload/v1736670115/zguznvwm7ib3exi1g1ko.png");
//                    host.setPhoneNumber(generateRandomPhoneNumber(random));
//                    host.setAddress(generateRandomAddress(random, addresses));
//                    host.setRole(UserRole.HOST);


//
                    Owner owner = new Owner();
                    owner.setUsername(LAST_NAMES[i] + " " + FIRST_NAMES[i]);
                    owner.setPassword(FIRST_NAMES[i] + 222);
                    owner.setHashedPassword(FIRST_NAMES[i] + 222);
                    owner.setDob(generateRandomDate(random));
                    owner.setEmail(LAST_NAMES[i] + "@gmail.com");
                    owner.setProfileImage("https://res.cloudinary.com/dqydgahsj/image/upload/v1736670115/zguznvwm7ib3exi1g1ko.png");
                    owner.setPhoneNumber(generateRandomPhoneNumber(random));
                    owner.setAddress(generateRandomAddress(random, addresses));
                    owner.setRole(UserRole.OWNER);
                    owner.addHost(host);


                    RentalAgreement rentalAgreement = new RentalAgreement();
                    rentalAgreement.setStatus(generateRandomRentalAgreementStatus(random));
                    contractDates = generateRandomContractDates(random);
                    rentalAgreement.setStartContractDate(contractDates[0]);
                    rentalAgreement.setEndContractDate(contractDates[1]);
                    rentalAgreement.setOwner(owner);
                    rentalAgreement.setHost(host);
                    tenant.addRentalAgreement(rentalAgreement);


//
//                    ResidentialProperty residentialProperty = new ResidentialProperty();
//                    residentialProperty.setAddress(generateRandomAddress(random, addresses));
//                    residentialProperty.setPrice(generateRandomPropertyPrice(random));
//                    residentialProperty.setStatus(generateRandomPropertyStatus(random));
//                    residentialProperty.setImageLink("https://th.bing.com/th/id/OIP.5tEfHpBeD1karbOl6Vd5bgAAAA?w=201&h=201&rs=1&pid=ImgDetMain");
//                    residentialProperty.setOwner(owner);
//                    residentialProperty.setRentalAgreement(rentalAgreement);
//                    residentialProperty.setTitle(generateRandomPropertyName(random));
//                    residentialProperty.setDescription("This is a beautiful residential property located in a quiet neighborhood. It has a spacious living room, a modern kitchen, and a lovely garden. The property is pet-friendly and has a garage for parking.");
//                    createdAndUpdatedDates = generateRandomCreatedAndUpdatedDates(random);
//                    residentialProperty.setCreatedAt(createdAndUpdatedDates[0]);
//                    residentialProperty.setLastUpdated(createdAndUpdatedDates[1]);
//                    residentialProperty.setNumberOfBedrooms(generateRandomNumberOfBedrooms(random));
//                    residentialProperty.setPetFriendliness(generateRandomParkingSpace(random));
//                    residentialProperty.setGardenAvailability(generateRandomParkingSpace(random));
//                    rentalAgreement.setRentingFee(residentialProperty.getPrice());
//                    host.addResidentialProperty(residentialProperty);

                    CommercialProperty commercialProperty = new CommercialProperty();
                    commercialProperty.setAddress(generateRandomAddress(random, addresses));
                    commercialProperty.setPrice(generateRandomPropertyPrice(random));
                    commercialProperty.setStatus(generateRandomPropertyStatus(random));
                    commercialProperty.setImageLink("https://th.bing.com/th/id/OIP.1TqrYWCFU0QUYAN7_MF5NgHaE8?rs=1&pid=ImgDetMain");
                    commercialProperty.setOwner(owner);
                    commercialProperty.setRentalAgreement(rentalAgreement);
                    commercialProperty.setTitle(generateRandomPropertyName(random));
                    commercialProperty.setDescription("This is a spacious commercial property located in a prime location. It has a large storefront, high ceilings, and plenty of natural light. The property is suitable for a variety of businesses, such as a retail store, restaurant, or office space.");
                    createdAndUpdatedDates = generateRandomCreatedAndUpdatedDates(random);
                    commercialProperty.setCreatedAt(createdAndUpdatedDates[0]);
                    commercialProperty.setLastUpdated(createdAndUpdatedDates[1]);
                    commercialProperty.setBusinessType(generateRandomBusinessType(random));
                    commercialProperty.setParkingSpace(generateRandomParkingSpace(random));
                    commercialProperty.setSquareFootage(generateRandomSquareFootage(random));
                    rentalAgreement.setRentingFee(commercialProperty.getPrice());
                    host.addCommercialProperty(commercialProperty);



                    Payment payment = new Payment();
                    payment.setReceipt(generateRandomReceiptId(random));
                    payment.setMethod(generateRandomPaymentMethod(random));
                    payment.setAmount(rentalAgreement.getRentingFee());
                    payment.setStatus(generateRandomPaymentStatus(random));
                    payment.setDueDate(generateRandomDueDate(random, rentalAgreement.getStartContractDate(), rentalAgreement.getEndContractDate()));
                    payment.setRentalAgreement(rentalAgreement);
                    tenant.setPaymentMethod(payment.getMethod());
                    payment.setTenant(tenant);

                    session.persist(tenant);
                    session.persist(host);
                    session.persist(owner);
                    session.persist(rentalAgreement);
                    session.persist(commercialProperty);
                    session.persist(payment);

                } catch (Exception e) {
                    System.err.println("Error while persisting entities: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Commit transaction
            transaction.commit();
            System.out.println("All entities saved successfully.");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public class HibernateUtil {
        private static final SessionFactory sessionFactory;
        static {
            try {
                sessionFactory = new Configuration().configure("/hibernate/hibernate.cfg.xml").buildSessionFactory();
            } catch (Throwable ex) {
                System.err.println("Initial SessionFactory creation failed." + ex);
                throw new ExceptionInInitializerError(ex);
            }
        }

        public static SessionFactory getSessionFactory() {
            return sessionFactory;
        }

        public static void shutdown() {
            getSessionFactory().close();
        }

        private <T> void saveEntity(Session session, T entity) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                session.persist(entity); // Use persist to save the entity to the database
                transaction.commit();
                System.out.println("Created entity: " + entity.getClass().getSimpleName());
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                System.err.println("Error while creating entity: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    private static String generateUsername(String firstName, String lastName, Random random) {
        String baseUsername = firstName.toLowerCase() + lastName.toLowerCase();
        int randomNumber = random.nextInt(100); // Add a random number to avoid duplicates.
        return baseUsername + randomNumber;
    }

    public static String generateRandomReceiptId(Random random) {
        // Prefix for the receipt ID
        String prefix = "RCPT";

        // Generate a random number for the ID
        int randomNumber = random.nextInt(900000) + 100000; // Ensures a 6-digit number

        // Generate a random alphanumeric suffix
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder suffix = new StringBuilder();
        for (int i = 0; i < 4; i++) { // Generate 4-character suffix
            suffix.append(characters.charAt(random.nextInt(characters.length())));
        }

        // Combine prefix, random number, and suffix
        return prefix + "-" + randomNumber + "-" + suffix;
    }
    public static String generateRandomPropertyName(Random random) {
        // Define arrays of words for property names
        String[] locations = {"Sunny", "Cozy", "Spacious", "Modern", "Elegant", "Luxurious", "Charming", "Serene", "Peaceful", "Grand"};
        String[] adjectives = {"Apartment", "Villa", "Cottage", "House", "Mansion", "Condo", "Bungalow", "Cabin", "Loft", "Estate"};
        String[] propertyTypes = {"Hillview", "Parkside", "Seaview", "Riverside", "Downtown", "Woodland", "Lakefront", "Sunset", "Meadow", "Garden"};

        // Randomly pick one word from each array
        String adjective = adjectives[random.nextInt(adjectives.length)];
        String propertyType = propertyTypes[random.nextInt(propertyTypes.length)];
        String location = locations[random.nextInt(locations.length)];

        // Combine the words into a property name
        return adjective + " " + propertyType + " at " + location;
    }

    private static Payment.paymentMethod generateRandomPaymentMethod(Random random) {
        Payment.paymentMethod[] methods = Payment.paymentMethod.values();
        return methods[random.nextInt(methods.length)];
    }

    private static Address generateRandomAddress(Random random, List<Address> addresses) {
        return addresses.get(random.nextInt(addresses.size()));
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
        long minDay = LocalDate.of(1970, 1, 1).toEpochDay();
        long maxDay = LocalDate.of(2000, 1, 1).toEpochDay();
        long randomDay = minDay + random.nextInt((int)(maxDay - minDay));
        return LocalDate.ofEpochDay(randomDay);
    }

    private static LocalDate[] generateRandomContractDates(Random random) {
        // Define the date range
        long minDay = LocalDate.of(2019, 1, 1).toEpochDay();
        long maxDay = LocalDate.of(2024, 1, 1).toEpochDay();

        // Generate random start date
        long startDay = minDay + random.nextInt((int) (maxDay - minDay));
        LocalDate startContractDate = LocalDate.ofEpochDay(startDay);

        // Generate random end date, ensuring it is after start date
        long endDay = startDay + random.nextInt((int) (maxDay - startDay));
        LocalDate endContractDate = LocalDate.ofEpochDay(endDay);

        // Return the dates as an array
        return new LocalDate[]{startContractDate, endContractDate};
    }

    public static LocalDate generateRandomDueDate(Random random, LocalDate startContractDate, LocalDate endContractDate) {
        // Get the epoch days for the start and end contract dates
        long startDay = startContractDate.toEpochDay();
        long endDay = endContractDate.toEpochDay();

        // Generate a random day between start and end contract dates
        long randomDay = startDay + random.nextInt((int) (endDay - startDay));
        LocalDate randomDate = LocalDate.ofEpochDay(randomDay);

        // Adjust the random date to the last day of its month
        return randomDate.withDayOfMonth(randomDate.lengthOfMonth());
    }


    private static LocalDateTime[] generateRandomCreatedAndUpdatedDates(Random random) {
        // Define the range for createdDate
        long minCreatedDay = LocalDate.of(1980, 1, 1).toEpochDay();
        long maxCreatedDay = LocalDate.of(2000, 1, 1).toEpochDay();

        // Generate random createdDate
        long createdDay = minCreatedDay + random.nextInt((int) (maxCreatedDay - minCreatedDay));
        LocalDateTime createdDate = LocalDate.ofEpochDay(createdDay)
                .atTime(random.nextInt(24), random.nextInt(60), random.nextInt(60));

        // Define the range for updatedDate (starts from createdDate)
        long minUpdatedDay = createdDate.toLocalDate().toEpochDay();
        long maxUpdatedDay = LocalDate.of(2024, 1, 1).toEpochDay();

        // Generate random updatedDate
        long updatedDay = minUpdatedDay + random.nextInt((int) (maxUpdatedDay - minUpdatedDay));
        LocalDateTime updatedDate = LocalDate.ofEpochDay(updatedDay)
                .atTime(random.nextInt(24), random.nextInt(60), random.nextInt(60));

        return new LocalDateTime[]{createdDate, updatedDate};
    }

    private static double generateRandomPropertyPrice(Random random) {
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
