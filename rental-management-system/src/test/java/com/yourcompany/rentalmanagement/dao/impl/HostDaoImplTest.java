//package com.yourcompany.rentalmanagement.dao.impl;
///**
// * @author FTech
// */
//import com.yourcompany.rentalmanagement.model.Address;
//import com.yourcompany.rentalmanagement.model.Host;
//import com.yourcompany.rentalmanagement.util.HibernateUtil;
//import org.junit.jupiter.api.*;
//
//import java.time.LocalDate;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//class HostDaoImplTest {
//
//    private static HostDaoImpl hostDao;
//    private static long testHostId;
//
//    @BeforeAll
//    static void setup() {
//        hostDao = new HostDaoImpl();
//    }
//
//    @Test
//    void testGetHostById_Success() {
//        // Act
//        Host host = hostDao.getHostById(testHostId);
//
//        // Assert
//        assertNotNull(host, "Host should be retrieved successfully");
//        assertEquals("testUser", host.getUsername(), "Username should match");
//        assertEquals("testUser@example.com", host.getEmail(), "Email should match");
//    }
//
//    @Test
//    void testGetHostById_NotFound() {
//        // Act
//        Host host = hostDao.getHostById(9999L);
//
//        // Assert
//        assertNull(host, "Host should not be found");
//    }
//
//    @Test
//    void testUpdateProfile_Success() {
//        // Arrange
//        Map<String, Object> profile = new HashMap<>();
//        profile.put("username", "updatedUser");
//        profile.put("dob", "1992-05-15");
//        profile.put("email", "updatedUser@example.com");
//        profile.put("phoneNumber", "0987654321");
//
//        // Act
//        Map<String, Object> result = hostDao.updateProfile(testHostId, profile);
//
//        // Assert
//        assertEquals("success", result.get("status"), "Profile update should be successful");
//
//        Host updatedHost = hostDao.getHostById(testHostId);
//        assertEquals("updatedUser", updatedHost.getUsername(), "Username should be updated");
//        assertEquals(LocalDate.of(1992, 5, 15), updatedHost.getDob(), "DOB should be updated");
//        assertEquals("updatedUser@example.com", updatedHost.getEmail(), "Email should be updated");
//    }
//
//    @Test
//
//    void testUpdateProfile_Failure() {
//        // Arrange
//        Map<String, Object> profile = new HashMap<>();
//        profile.put("username", "failUser");
//
//        // Act
//        Map<String, Object> result = hostDao.updateProfile(9999L, profile);
//
//        // Assert
//        assertEquals("failed", result.get("status"), "Profile update should fail for non-existing host");
//    }
//
//    @Test
//
//    void testUpdateUserImage_Success() {
//        // Act
//        Map<String, Object> result = hostDao.updateUserImage(testHostId, "http://example.com/newImage.png");
//
//        // Assert
//        assertEquals("success", result.get("status"), "Image update should be successful");
//
//        Host updatedHost = hostDao.getHostById(testHostId);
//        assertEquals("http://example.com/newImage.png", updatedHost.getProfileImage(), "Image link should be updated");
//    }
//
//    @Test
//
//    void testUpdateUserImage_Failure() {
//        // Act
//        Map<String, Object> result = hostDao.updateUserImage(9999L, "http://example.com/failImage.png");
//
//        // Assert
//        assertEquals("failed", result.get("status"), "Image update should fail for non-existing host");
//    }
//
//    @Test
//
//    void testUpdateAddress_Success() {
//        // Arrange
//        Map<String, Object> addressData = new HashMap<>();
//        addressData.put("province", "Updated Province");
//        addressData.put("district", "Updated District");
//        addressData.put("ward", "Updated Ward");
//        addressData.put("streetNumber", "99");
//        addressData.put("streetName", "Updated Street");
//
//        // Act
//        Map<String, Object> result = hostDao.updateAddress(testHostId, addressData);
//
//        // Assert
//        assertEquals("success", result.get("status"), "Address update should be successful");
//
//        Host updatedHost = hostDao.getHostById(testHostId);
//        Address updatedAddress = updatedHost.getAddress();
//        assertEquals("Updated Province", updatedAddress.getCity(), "Province should be updated");
//        assertEquals("Updated District", updatedAddress.getDistrict(), "District should be updated");
//    }
//
//    @Test
//
//    void testUpdateAddress_Failure() {
//        // Arrange
//        Map<String, Object> addressData = new HashMap<>();
//        addressData.put("province", "Fail Province");
//
//        // Act
//        Map<String, Object> result = hostDao.updateAddress(9999L, addressData);
//
//        // Assert
//        assertEquals("failed", result.get("status"), "Address update should fail for non-existing host");
//    }
//
//    @Test
//    void testUpdatePassword_Success() {
//        // Act
//        Map<String, Object> result = hostDao.updatePassword(testHostId, "password", "newPassword");
//
//        // Assert
//        assertEquals("success", result.get("status"), "Password update should be successful");
//
//        Host updatedHost = hostDao.getHostById(testHostId);
//        assertTrue(updatedHost.checkPassword("newPassword"), "Password should be updated");
//    }
//
//    @Test
//    void setPassword(){
//        Map<String, Object> result = hostDao.setPassword(46, "password");
//        System.out.println(hostDao.getHostById(46).getPassword());
//        assertEquals("success", result.get("status"), "Password update should be successful");
//    }
//
//    @Test
//    void testUpdatePassword_Failure() {
//        // Act
//        Map<String, Object> result = hostDao.updatePassword(testHostId, "wrongPassword", "newPassword");
//
//        // Assert
//        assertEquals("failed", result.get("status"), "Password update should fail for incorrect old password");
//    }
//
//    @Test
//    void testGetTotalUsers() {
//        // Act
//        long totalUsers = hostDao.getTotalUsers();
//
//        // Assert
//        assertTrue(totalUsers > 0, "Total users should be greater than 0");
//    }
//}
