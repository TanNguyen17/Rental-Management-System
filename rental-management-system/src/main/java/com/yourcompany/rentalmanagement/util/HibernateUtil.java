package com.yourcompany.rentalmanagement.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;

import com.yourcompany.rentalmanagement.service.PaymentEventListener;
import com.yourcompany.rentalmanagement.view.PaymentsView;


public class HibernateUtil {

    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;
    private static PaymentsView paymentsView;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Create registry
                registry = new StandardServiceRegistryBuilder()
                        .configure("/hibernate/hibernate.cfg.xml") // Changed path
                        .build();

                // Create MetadataSources
                MetadataSources sources = new MetadataSources(registry);

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

    private static void registerEventListeners(SessionFactory sessionFactory) {
        EventListenerRegistry eventListenerRegistry = ((SessionFactoryImpl) sessionFactory).getServiceRegistry().getService(EventListenerRegistry.class);
        PaymentEventListener paymentEventListener = new PaymentEventListener(paymentsView);
        assert eventListenerRegistry != null;
        eventListenerRegistry.appendListeners(EventType.POST_INSERT, paymentEventListener);
        eventListenerRegistry.appendListeners(EventType.POST_UPDATE, paymentEventListener);
        eventListenerRegistry.appendListeners(EventType.POST_DELETE, paymentEventListener);
    }

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
