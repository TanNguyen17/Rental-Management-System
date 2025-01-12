package com.yourcompany.rentalmanagement.dao.impl;
/**
 * @author FTech
 */
import com.yourcompany.rentalmanagement.model.Manager;
import com.yourcompany.rentalmanagement.model.UserRole;
import org.hibernate.JDBCException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagerDaoImplTest {
    ManagerDaoImpl managerDAO = new ManagerDaoImpl();
    @Test
    void testCreateManager_Success() {
        Manager manager = new Manager();
        manager.setUsername("testUser");
        manager.setEmail("test@example.com");
        manager.setPassword("testPassword");
        manager.setRole(UserRole.MANAGER);
        manager.setSalt("testSalt");

        managerDAO.create(manager);

        Manager retrievedManager = managerDAO.read(manager.getId());

        assertNotNull(retrievedManager, "Manager should be created successfully.");
        assertEquals("testUser", retrievedManager.getUsername(), "Username should match.");
        assertEquals("test@example.com", retrievedManager.getEmail(), "Email should match.");
        assertEquals(UserRole.MANAGER, retrievedManager.getRole(), "Role should match.");
    }
    @Test
    void testCreateManager_MissingFields() {
        Manager manager = new Manager();
        manager.setUsername("incompleteUser");
        // Missing email, password, role, and salt

        assertThrows(IllegalArgumentException.class, () -> managerDAO.create(manager),
                "Creating a manager without all required fields should throw an exception.");
    }
    @Test
    void testCreateManager_DuplicateUsername() {
        Manager manager1 = new Manager();
        manager1.setUsername("duplicateUser");
        manager1.setEmail("test1@example.com");
        manager1.setPassword("password1");
        manager1.setRole(UserRole.MANAGER);
        manager1.setSalt("salt1");
        managerDAO.create(manager1);

        Manager manager2 = new Manager();
        manager2.setUsername("duplicateUser");
        manager2.setEmail("test2@example.com");
        manager2.setPassword("password2");
        manager2.setRole(UserRole.MANAGER);
        manager2.setSalt("salt2");

        assertThrows(Exception.class, () -> managerDAO.create(manager2),
                "Creating a manager with a duplicate username should throw an exception.");
    }
    @Test
    void testUpdateManager_Success() {
        Manager manager = new Manager();
        manager.setUsername("originalUser");
        manager.setEmail("original@example.com");
        manager.setPassword("originalPassword");
        manager.setRole(UserRole.MANAGER);
        manager.setSalt("originalSalt");
        managerDAO.create(manager);

        manager.setUsername("updatedUser");
        manager.setEmail("updated@example.com");
        managerDAO.update(manager);

        Manager updatedManager = managerDAO.read(manager.getId());

        assertNotNull(updatedManager, "Updated manager should exist.");
        assertEquals("updatedUser", updatedManager.getUsername(), "Username should be updated.");
        assertEquals("updated@example.com", updatedManager.getEmail(), "Email should be updated.");
    }
    @Test
    void testUpdateManager_NonExistent() {
        Manager manager = new Manager();
        manager.setId(999L);
        manager.setUsername("nonExistentUser");
        manager.setEmail("nonexistent@example.com");
        manager.setPassword("password");
        manager.setRole(UserRole.MANAGER);
        manager.setSalt("salt");

        assertThrows(Exception.class, () -> managerDAO.update(manager),
                "Updating a non-existent manager should throw an exception.");
    }
    @Test
    void testUpdateManager_MissingFields() {
        Manager manager = new Manager();
        manager.setUsername("validUser");
        manager.setEmail("valid@example.com");
        manager.setPassword("password");
        manager.setRole(UserRole.MANAGER);
        manager.setSalt("salt");
        managerDAO.create(manager);

        manager.setUsername(null); // Invalid update
        assertThrows(IllegalArgumentException.class, () -> managerDAO.update(manager),
                "Updating a manager with missing required fields should throw an exception.");
    }
    @Test
    void testDeleteManager_Success() {
        Manager manager = new Manager();
        manager.setUsername("deletableUser");
        manager.setEmail("delete@example.com");
        manager.setPassword("deletePassword");
        manager.setRole(UserRole.MANAGER);
        manager.setSalt("deleteSalt");
        managerDAO.create(manager);

        managerDAO.delete(manager);

        Manager deletedManager = managerDAO.read(manager.getId());
        assertNull(deletedManager, "Manager should be deleted.");
    }
    @Test
    void testDeleteManager_NonExistent() {
        Manager manager = new Manager();
        manager.setId(999L);
        manager.setUsername("nonExistentUser");

        assertThrows(Exception.class, () -> managerDAO.delete(manager),
                "Deleting a non-existent manager should throw an exception.");
    }
    @Test
    void testFindByUsername_Success() {
        Manager manager = new Manager();
        manager.setUsername("findUser");
        manager.setEmail("find@example.com");
        manager.setPassword("findPassword");
        manager.setRole(UserRole.MANAGER);
        manager.setSalt("findSalt");
        managerDAO.create(manager);

        Manager retrievedManager = managerDAO.findByUsername("findUser");

        assertNotNull(retrievedManager, "Manager should be found.");
        assertEquals("findUser", retrievedManager.getUsername(), "Username should match.");
    }
    @Test
    void testFindByUsername_NotFound() {
        Manager retrievedManager = managerDAO.findByUsername("nonExistentUser");
        assertNull(retrievedManager, "Manager should not be found.");
    }
    @Test
    void testFindByUsername_NullUsername() {
        assertThrows(IllegalArgumentException.class, () -> managerDAO.findByUsername(null),
                "Finding a manager with null username should throw an exception.");
    }
}