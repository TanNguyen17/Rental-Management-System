//package com.yourcompany.rentalmanagement.dao.impl;
//
//import com.yourcompany.rentalmanagement.model.*;
//import com.yourcompany.rentalmanagement.util.HibernateUtil;
//import org.hibernate.Session;
//import org.hibernate.Transaction;
//import org.hibernate.query.Query;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class TenantDaoImplTest {
//
//    private TenantDaoImpl tenantDao;
//    private Address address = new Address();
//
//    @BeforeEach
//    void setUp() {
//        tenantDao = new TenantDaoImpl();
//    }
//
//    // Test for createTenant
//    @Test
//    void testCreateOwner_Success() {
//        Tenant tenant = new Tenant();
//        tenant.setUsername("quihuynh1315");
//        tenant.setPassword("securePassword133");
//        tenant.setSalt("randomSalt10");
//        tenant.setEmail("quihuynh1315@example.com");
//        tenant.setRole(UserRole.TENANT);
//        tenant.setPhoneNumber("0919967677");
//
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Transaction transaction = session.beginTransaction();
//
//
//            Map<String, Object> result = tenantDao.createTenant(tenant);
//
//            assertEquals("success", result.get("status"));
//
//            transaction.rollback(); // Rollback to avoid persisting data during testing
//        } catch (Exception e) {
//            fail("Exception should not occur during successful test");
//        }
//    }
//
//    @Test
//    void testCreateOwner_Failure_MissingUsername() {
//        Tenant tenant = new Tenant();
//        tenant.setPassword("securePassword133");
//        tenant.setEmail("quihuynh1315@example.com");
//        tenant.setRole(UserRole.TENANT);
//        tenant.setPhoneNumber("0919967677");
//
//        Exception exception = assertThrows(Exception.class, () -> {
//            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//                Transaction transaction = session.beginTransaction();
//
//                tenantDao.createTenant(tenant);
//
//                transaction.rollback();
//            }
//        });
//        assertNotNull(exception);
//    }
//
//    @Test
//    void testCreateOwner_Failure_MissingEmail() {
//        Tenant tenant = new Tenant();
//        tenant.setUsername("quihuynh1315");
//        tenant.setPassword("securePassword133");
//        tenant.setSalt("randomSalt10");
//        tenant.setRole(UserRole.TENANT);
//        tenant.setPhoneNumber("0919967677");
//
//        Exception exception = assertThrows(Exception.class, () -> {
//            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//                Transaction transaction = session.beginTransaction();
//
//                tenantDao.createTenant(tenant);
//
//                transaction.rollback();
//            }
//        });
//
//        assertNotNull(exception);
//    }
//
//    @Test
//    void testGetAllTenants_WithData() {
//        // Assume there are tenants in the database
//        List<Tenant> tenants = tenantDao.getAllTenants();
//        assertNotNull(tenants, "Tenants list should not be null");
//        assertTrue(tenants.size() > 0, "Tenants list should not be empty");
//    }
//
//    @Test
//    void testGetAllTenants_NoData() {
//        // Simulate an empty database or clear it before running this test
//        List<Tenant> tenants = tenantDao.getAllTenants();
//        assertNotNull(tenants, "Tenants list should not be null");
//        assertEquals(0, tenants.size(), "Tenants list should be empty");
//    }
//
//    // Test for loadAll
//    @Test
//    void testLoadAll_WithData() {
//        // Assume there are tenants in the database
//        List<Tenant> tenants = tenantDao.loadAll();
//        assertNotNull(tenants, "Tenants list should not be null");
//        assertTrue(tenants.size() > 0, "Tenants list should not be empty");
//    }
//
//    @Test
//    void testLoadAll_NoData() {
//        // Simulate an empty database or clear it before running this test
//        List<Tenant> tenants = tenantDao.loadAll();
//        assertNotNull(tenants, "Tenants list should not be null");
//        assertEquals(0, tenants.size(), "Tenants list should be empty");
//    }
//
//
//    // Test for getUserById
//    @Test
//    void testGetUserById_ValidId() {
//        // Assume a tenant with ID 41 exists in the database
//        Tenant tenant = tenantDao.getUserById(41);
//        assertNotNull(tenant, "Tenant should not be null for a valid ID");
//        assertEquals("tanne20", tenant.getUsername(), "Tenant ID should match the requested ID");
//    }
//
//    @Test
//    void testGetUserById_InvalidIdFormat() {
//        // Assume an invalid ID format, such as "1a"
//        Exception exception = assertThrows(NumberFormatException.class, () -> {
//            tenantDao.getUserById(Long.parseLong("1a"));
//        });
//        assertNotNull(exception, "Exception should be thrown for an invalid ID format");
//        assertEquals("For input string: \"1a\"", exception.getMessage(), "Exception message should indicate the invalid input");
//    }
//
//
//    // Test for getTotalUsers
//    @Test
//    void testGetTotalUsers_WithUsers() {
//        long totalUsers = tenantDao.getTotalUsers();
//        assertTrue(totalUsers > 0, "Total users should be greater than 0");
//    }
//
//    @Test
//    void testGetTotalUsers_NoUsers() {
//        // Assuming the database has been cleared for this test
//        long totalUsers = tenantDao.getTotalUsers();
//        assertEquals(0, totalUsers, "Total users should be 0");
//    }
//
//
//    // Test for updateProfile
//    @Test
//    void testUpdateProfile_Success() {
//        Map<String, Object> profile = Map.of(
//                "username", "UpdatedName",
//                "dob", "1990-01-01",
//                "email", "updated@example.com",
//                "phoneNumber", "1234567890",
//                "paymentMethod", Payment.paymentMethod.CASH
//        );
//
//        Map<String, Object> result = tenantDao.updateProfile(105, profile);
//        assertEquals("success", result.get("status"), "Update status should be success");
//    }
//
//
//    @Test
//    void testUpdateProfile_TenantNotFound() {
//        Map<String, Object> profile = Map.of("username", "NonExistent");
//        Map<String, Object> result = tenantDao.updateProfile(1080999, profile);
//        System.out.println(result);
//        assertEquals("failed", result.get("status"), "Update status should be failed");
//    }
//
//
//    // Test for updateAddress
//    @Test
//    void testUpdateAddress_Success() {
//        Map<String, Object> addressData = Map.of(
//                "province", "NewProvince",
//                "district", "NewDistrict",
//                "ward", "NewWard",
//                "streetNumber", "123",
//                "streetName", "NewStreet"
//        );
//
//        Map<String, Object> result = tenantDao.updateAddress(41, addressData); // Replace 1L with an actual ID
//        assertEquals("success", result.get("status"), "Address update should be successful");
//    }
//
//    @Test
//    void testUpdateAddress_TenantNotFound() {
//        Map<String, Object> addressData = Map.of("province", "NonExistent");
//        Map<String, Object> result = tenantDao.updateAddress(999L, addressData);
//        assertEquals("failed", result.get("status"), "Address update should fail for non-existent tenant");
//    }
//
//    // Test for updatePassword
//    @Test
//    void testUpdatePassword_Success() {
//        Map<String, Object> result = tenantDao.updatePassword(102, "JackyJacky", "newPassword"); // Replace with actual data
//        assertEquals("success", result.get("status"), "Password update should be successful");
//    }
//
//    @Test
//    void testUpdatePassword_OldPasswordMismatch() {
//        Map<String, Object> result = tenantDao.updatePassword(102, "Hello", "newPassword");
//        assertEquals("failed", result.get("status"), "Password update should fail for incorrect old password");
//        assertEquals("Old password does not match", result.get("message"));
//    }
//
//    // Test for updateUserImage
//    @Test
//    void testUpdateUserImage_Success() {
//        Map<String, Object> result = tenantDao.updateUserImage(106, "https://res.cloudinary.com/dqydgahsj/image/upload/v1736670115/zguznvwm7ib3exi1g1ko.png");
//        assertEquals("success", result.get("status"));
//        assertEquals("Image updated successfully", result.get("message"));
//    }
//
//    @Test
//    void testUpdateUserImage_TenantNotFound() {
//        Map<String, Object> result = tenantDao.updateUserImage(999L, "https://res.cloudinary.com/dqydgahsj/image/upload/v1736670115/zguznvwm7ib3exi1g1ko.png");
//        assertEquals("failed", result.get("status"));
//        assertEquals("Tenant not found", result.get("message"));
//    }
//}
