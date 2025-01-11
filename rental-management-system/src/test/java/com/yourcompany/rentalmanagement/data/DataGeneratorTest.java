package com.yourcompany.rentalmanagement.data;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.yourcompany.rentalmanagement.service.PaymentEventListener;

public class DataGeneratorTest {

    @BeforeAll
    public static void setup() {
        PaymentEventListener.setTestEnvironment(true);
    }

    @Test
    public void testGenerateAllData() {
        System.out.println("Generating all test data...");

        try {
            PropertyDataGenerator.generateTestData();
            PaymentDataGenerator.generateTestData();
        } catch (Exception e) {
            System.err.println("Error generating test data: " + e.getMessage());
            throw e;
        }
    }
}
