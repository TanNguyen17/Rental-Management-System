package com.yourcompany.rentalmanagement.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtil {

    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Create registry
                registry = new StandardServiceRegistryBuilder()
                        .configure("/hibernate.cfg.xml") // Changed path
                        .build();

                // Create MetadataSources
                MetadataSources sources = new MetadataSources(registry);

                // I think that we should add all entities explicitly, not in hibernate.cfg.xml
                sources.addAnnotatedClass(com.yourcompany.rentalmanagement.model.User.class);
                sources.addAnnotatedClass(com.yourcompany.rentalmanagement.model.Tenant.class);
                sources.addAnnotatedClass(com.yourcompany.rentalmanagement.model.Host.class);
                sources.addAnnotatedClass(com.yourcompany.rentalmanagement.model.Owner.class);
                sources.addAnnotatedClass(com.yourcompany.rentalmanagement.model.Manager.class);
                sources.addAnnotatedClass(com.yourcompany.rentalmanagement.model.Address.class);
                sources.addAnnotatedClass(com.yourcompany.rentalmanagement.model.Property.class);
                sources.addAnnotatedClass(com.yourcompany.rentalmanagement.model.ResidentialProperty.class);
                sources.addAnnotatedClass(com.yourcompany.rentalmanagement.model.CommercialProperty.class);
                sources.addAnnotatedClass(com.yourcompany.rentalmanagement.model.RentalAgreement.class);
                sources.addAnnotatedClass(com.yourcompany.rentalmanagement.model.Payment.class);

                // Create Metadata
                Metadata metadata = sources.getMetadataBuilder().build();

                // Create SessionFactory
                sessionFactory = metadata.getSessionFactoryBuilder().build();

                System.out.println("Hibernate SessionFactory initialized successfully");

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("SessionFactory creation failed: " + e.getMessage());
                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}