package com.yourcompany.rentalmanagement.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.yourcompany.rentalmanagement.model.Address;
import com.yourcompany.rentalmanagement.model.CommercialProperty;
import com.yourcompany.rentalmanagement.model.Host;
import com.yourcompany.rentalmanagement.model.Owner;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.ResidentialProperty;
import com.yourcompany.rentalmanagement.model.UserRole;
import com.yourcompany.rentalmanagement.util.HibernateUtil;

public class PropertyDataGenerator {

    // Test data arrays
    private static final String[] OWNER_USERNAMES = {
        "owner_john", "owner_sarah", "owner_mike"
    };

    private static final String[] HOST_USERNAMES = {
        "host_alice", "host_bob", "host_charlie", "host_david"
    };

    private static final String[] RESIDENTIAL_TITLES = {
        "Luxury Apartment", "Family Home", "Beach House", "Mountain Cabin",
        "City Condo", "Suburban House", "Studio Apartment", "Penthouse Suite",
        "Garden Villa", "Townhouse"
    };

    private static final String[] COMMERCIAL_TITLES = {
        "Downtown Office", "Retail Space", "Restaurant Location", "Shopping Mall Unit",
        "Business Center", "Industrial Space", "Warehouse", "Medical Office",
        "Tech Hub", "Co-working Space"
    };

    private static final String[] CITIES = {
        "New York", "Los Angeles", "Chicago", "Houston", "Phoenix",
        "Philadelphia", "San Antonio", "San Diego", "Dallas", "San Jose"
    };

    private static final String[] BUSINESS_TYPES = {
        "Office", "Retail", "Restaurant", "Warehouse", "Medical"
    };

    public static void generateTestData() {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Check if data already exists
            Long ownerCount = session.createQuery("SELECT COUNT(o) FROM Owner o", Long.class)
                    .getSingleResult();
            if (ownerCount > 0) {
                System.out.println("Data already exists. Skipping generation.");
                transaction.commit();
                return;
            }

            // 1. Create Owners
            List<Owner> owners = createOwners(session);
            System.out.println("Created " + owners.size() + " owners with IDs: "
                    + owners.stream().map(o -> String.valueOf(o.getId())).collect(Collectors.joining(", ")));

            // 2. Create Hosts  
            List<Host> hosts = createHosts(session);
            System.out.println("Created " + hosts.size() + " hosts with IDs: "
                    + hosts.stream().map(h -> String.valueOf(h.getId())).collect(Collectors.joining(", ")));

            // 3. Create Properties
            System.out.println("\nCreating Residential Properties...");
            createResidentialProperties(session, owners, hosts);

            System.out.println("\nCreating Commercial Properties...");
            createCommercialProperties(session, owners, hosts);

            // Verify commercial properties
            Long commercialCount = session.createQuery("SELECT COUNT(c) FROM CommercialProperty c", Long.class)
                    .getSingleResult();
            System.out.println("Total commercial properties created: " + commercialCount);

            transaction.commit();
            System.out.println("Transaction committed successfully");
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                System.err.println("Transaction rolled back due to error");
            }
            throw e;
        }
    }

    private static List<Owner> createOwners(Session session) {
        List<Owner> owners = new ArrayList<>();
        for (String username : OWNER_USERNAMES) {
            Owner owner = new Owner();
            owner.setUsername(username);
            owner.setEmail(username + "@test.com");
            owner.setHashedPassword("password123");
            owner.setRole(UserRole.OWNER);
            session.persist(owner);
            owners.add(owner);
            System.out.println("Created owner: " + username);
        }
        return owners;
    }

    private static List<Host> createHosts(Session session) {
        List<Host> hosts = new ArrayList<>();
        for (String username : HOST_USERNAMES) {
            Host host = new Host();
            host.setUsername(username);
            host.setEmail(username + "@test.com");
            host.setHashedPassword("password123");
            host.setRole(UserRole.HOST);
            session.persist(host);
            hosts.add(host);
            System.out.println("Created host: " + username);
        }
        return hosts;
    }

    private static void createResidentialProperties(Session session, List<Owner> owners, List<Host> hosts) {
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            ResidentialProperty property = new ResidentialProperty();

            // Set basic property details
            property.setTitle(RESIDENTIAL_TITLES[i]);
            property.setDescription("A beautiful residential property for rent");
            property.setPrice(1500 + random.nextInt(5000)); // Price between $1500-$6500
            property.setStatus(Property.propertyStatus.AVAILABLE);
            property.setImageLink("https://picsum.photos/400/300"); // Random image placeholder
            property.setCreatedAt(LocalDateTime.now());
            property.setLastUpdated(LocalDateTime.now());

            // Set residential-specific details
            property.setNumberOfBedrooms(1 + random.nextInt(5)); // 1-5 bedrooms
            property.setGardenAvailability(random.nextBoolean());
            property.setPetFriendliness(random.nextBoolean());

            // Set address
            property.setAddress(createRandomAddress(random));

            // Assign random owner
            Owner owner = owners.get(random.nextInt(owners.size()));
            property.setOwner(owner);

            // Assign random host (70% chance to have a host)
            if (random.nextDouble() < 0.7) {
                Host host = hosts.get(random.nextInt(hosts.size()));
                property.getHosts().add(host);
                host.addResidentialProperty(property);
            }

            session.persist(property);
            session.flush(); // Force the persist operation
            System.out.println("Created residential property: " + property.getTitle() + " with ID: " + property.getId());
        }
    }

    private static void createCommercialProperties(Session session, List<Owner> owners, List<Host> hosts) {
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            CommercialProperty property = new CommercialProperty();

            // Set basic property details
            property.setTitle(COMMERCIAL_TITLES[i]);
            property.setDescription("A prime commercial property for rent");
            property.setPrice(3000 + random.nextInt(7000));
            property.setStatus(Property.propertyStatus.AVAILABLE);
            property.setImageLink("https://picsum.photos/400/300");
            property.setCreatedAt(LocalDateTime.now());
            property.setLastUpdated(LocalDateTime.now());

            // Set commercial-specific details
            property.setBusinessType(BUSINESS_TYPES[random.nextInt(BUSINESS_TYPES.length)]);
            property.setParkingSpace(random.nextBoolean());
            property.setSquareFootage(1000 + random.nextInt(9000));

            // Set address
            Address address = createRandomAddress(random);
            session.persist(address); // Persist address first
            property.setAddress(address);

            // Assign random owner
            Owner owner = owners.get(random.nextInt(owners.size()));
            property.setOwner(owner);

            // Persist the property first
            session.persist(property);

            // Assign random host (70% chance to have a host)
            if (random.nextDouble() < 0.7) {
                Host host = hosts.get(random.nextInt(hosts.size()));
                property.addHost(host);
                host.addCommercialProperty(property);
                session.merge(host); // Update the host
            }

            session.flush();
            System.out.println("Created commercial property: " + property.getTitle() + " with ID: " + property.getId());
        }
    }

    private static Address createRandomAddress(Random random) {
        Address address = new Address();
        address.setNumber(String.valueOf(1 + random.nextInt(999))); // Street number 1-999
        address.setStreet("Main Street");
        address.setCity(CITIES[random.nextInt(CITIES.length)]);
        address.setState("State " + (random.nextInt(50) + 1)); // State 1-50, change later, just put state for now for testing
        return address;
    }
}
