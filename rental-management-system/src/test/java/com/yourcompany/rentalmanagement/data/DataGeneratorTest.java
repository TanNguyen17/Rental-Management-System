package com.yourcompany.rentalmanagement.data;

import org.junit.jupiter.api.Test;
import org.hibernate.Session;
import com.yourcompany.rentalmanagement.util.HibernateUtil;

public class DataGeneratorTest {
    
    @Test
    public void testGeneratePropertyData() {
        System.out.println("property data..");
        try (Session testSession = HibernateUtil.getSessionFactory().openSession()) {
            PropertyDataGenerator.generateTestData();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* when use, take this comment out
    @Test
    public void testGeneratePaymentData() {
        System.out.println("payment data..");
        try (Session testSession = HibernateUtil.getSessionFactory().openSession()) {
            PaymentDataGenerator.generateTestData();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    */
} 