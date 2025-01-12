package com.yourcompany.rentalmanagement.dao.impl;

import com.yourcompany.rentalmanagement.dao.impl.ManagerDaoImpl;
import com.yourcompany.rentalmanagement.model.Manager;
import com.yourcompany.rentalmanagement.model.Address;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ManagerDaoImplTest {

    private ManagerDaoImpl managerDao;

    @BeforeEach
    void setUp() {
        managerDao = new ManagerDaoImpl();
    }

    @Test
    void testCreate() {
        Manager newManager = new Manager();
        newManager.setUsername("NewManager");
        newManager.setPassword("newpassword");
        newManager.setEmail("newmanager@example.com");

        assertDoesNotThrow(() -> managerDao.create(newManager), "Manager creation should not throw exceptions");
    }

    @Test
    void testRead_ValidId() {
        Manager manager = managerDao.read(11L);
        assertNotNull(manager, "Manager should not be null for a valid ID");
        assertEquals("UpdatedManager", manager.getUsername(), "Username should match");
    }

    @Test
    void testRead_InvalidId() {
        Manager manager = managerDao.read(999L);
        assertNull(manager, "Manager should be null for an invalid ID");
    }

    @Test
    void testUpdate() {
        Manager manager = managerDao.read(11L);
        assertNotNull(manager, "Manager should not be null");

        manager.setUsername("UpdatedManager");
        managerDao.update(manager);

        Manager updatedManager = managerDao.read(11L);
        assertNotNull(updatedManager);
        assertEquals("UpdatedManager", updatedManager.getUsername(), "Username should be updated");
    }

    @Test
    void testDelete() {
        Manager manager = managerDao.read(14L);
        assertNotNull(manager, "Manager should exist before deletion");

        managerDao.delete(manager);

        Manager deletedManager = managerDao.read(14L);
        assertNull(deletedManager, "Manager should be null after deletion");
    }

    @Test
    void testFindByUsername_ValidUsername() {
        Manager manager = managerDao.findByUsername("diemqui1101");
        assertNotNull(manager, "Manager should not be null for a valid username");
        assertEquals(8, manager.getId(), "ID should match the expected value");
    }

    @Test
    void testFindByUsername_InvalidUsername() {
        Manager manager = managerDao.findByUsername("NonExistent");
        assertNull(manager, "Manager should be null for an invalid username");
    }

    @Test
    void testUpdateProfile_Success() {
        Map<String, Object> profile = new HashMap<>();
        profile.put("username", "ProfileUpdated");
        profile.put("dob", "1990-01-01");
        profile.put("email", "profileupdated@example.com");
        profile.put("phoneNumber", "123456789");

        Map<String, Object> result = managerDao.updateProfile(10, profile);
        assertEquals("success", result.get("status"), "Profile update should be successful");

        Manager updatedManager = managerDao.read(10L);
        assertEquals("ProfileUpdated", updatedManager.getUsername(), "Username should be updated");
    }

    @Test
    void testUpdateProfile_ManagerNotFound() {
        Map<String, Object> profile = new HashMap<>();
        profile.put("username", "NonExistent");

        Map<String, Object> result = managerDao.updateProfile(999L, profile);
        assertEquals("failed", result.get("status"), "Profile update should fail for a non-existent manager");
    }

    @Test
    void testUpdateUserImage_Success() {
        Map<String, Object> result = managerDao.updateUserImage(10, "https://th.bing.com/th/id/OIP.gYV1VqfK1YVNYL1XSoT1VwHaFh?w=670&h=500&rs=1&pid=ImgDetMain");
        assertEquals("success", result.get("status"), "Image update should be successful");


    }

    @Test
    void testUpdateUserImage_ManagerNotFound() {
        Map<String, Object> result = managerDao.updateUserImage(999L, "nonExistentImage.jpg");
        assertEquals("failed", result.get("status"), "Image update should fail for a non-existent manager");
    }

    @Test
    void testUpdatePassword_Success() {
        Map<String, Object> result = managerDao.updatePassword(10, "password1", "newPassword");
        assertEquals("success", result.get("status"), "Password update should be successful");

        Manager updatedManager = managerDao.read(1L);
        assertTrue(updatedManager.checkPassword("newPassword"), "Password should be updated successfully");
    }

    @Test
    void testUpdatePassword_OldPasswordMismatch() {
        Map<String, Object> result = managerDao.updatePassword(9, "wrongPassword", "newPassword");
        assertEquals("failed", result.get("status"), "Password update should fail for a mismatched old password");
        assertEquals("Password does not match", result.get("message"), "Error message should indicate mismatch");
    }

    @Test
    void testGetAllManagers() {
        List<Manager> managers = managerDao.getAllManagers();
        System.out.println(managers.size());
        assertNotNull(managers, "Manager list should not be null");
        assertEquals(12, managers.size(), "Manager list size should match the expected count");
    }
}
