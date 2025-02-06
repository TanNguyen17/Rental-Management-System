//package com.yourcompany.rentalmanagement.dao.impl;
///**
// * @author FTech
// */
//import com.yourcompany.rentalmanagement.model.Address;
//import com.yourcompany.rentalmanagement.model.Owner;
//import com.yourcompany.rentalmanagement.util.HibernateUtil;
//import org.hibernate.Session;
//import org.hibernate.Transaction;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDate;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class OwnerDaoImplTest {
//
//    private OwnerDaoImpl ownerDao;
//    private Address address = new Address();
//
//    @BeforeEach
//    void setUp() {
//        ownerDao = new OwnerDaoImpl();
//    }
//
//    @Test
//    void testLoadAllOwners_Success() {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Transaction transaction = session.beginTransaction();
//
//            // Assuming some Owners are already present in the database
//            List<Owner> owners = ownerDao.loadAll();
//
//            assertNotNull(owners);
//            assertFalse(owners.isEmpty(), "Owners list should not be empty");
//
//            transaction.rollback(); // Rollback to avoid persisting changes during testing
//        } catch (Exception e) {
//            fail("Exception should not occur during successful test");
//        }
//    }
//
//    @Test
//    void testLoadAllOwners_NoOwnersFound() {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Transaction transaction = session.beginTransaction();
//
//            // Assuming no Owners are present in the database
//            ownerDao.getOwners().clear(); // Simulating an empty database
//            List<Owner> owners = ownerDao.loadAll();
//
//            assertNotNull(owners);
//            assertTrue(owners.isEmpty(), "Owners list should be empty");
//
//            transaction.rollback();
//        } catch (Exception e) {
//            fail("Exception should not occur during no-owners test");
//        }
//    }
//
//    @Test
//    void testGetAllOwners_WithMultipleOwners() {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Transaction transaction = session.beginTransaction();
//
//            // Add sample owners to the database
//            Owner owner1 = new Owner();
//            owner1.setUsername("owner1");
//            owner1.setEmail("owner1@example.com");
//            owner1.setPassword("password1");
//            owner1.setSalt("salt1");
//            owner1.setRole(UserRole.OWNER);
//            session.persist(owner1);
//
//            Owner owner2 = new Owner();
//            owner2.setUsername("owner2");
//            owner2.setEmail("owner2@example.com");
//            owner2.setPassword("password2");
//            owner2.setSalt("salt2");
//            owner2.setRole(UserRole.OWNER);
//            session.persist(owner2);
//
//            transaction.commit();
//
//            // Call the getAllOwners method
//            List<Owner> owners = ownerDao.getAllOwners();
//
//            assertNotNull(owners);
//            assertTrue(owners.size() >= 2, "There should be at least 2 owners in the list");
//
//        } catch (Exception e) {
//            fail("Exception should not occur when retrieving multiple owners");
//        }
//    }
//
//    @Test
//    void testGetAllOwners_WhenNoOwnersExist() {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Transaction transaction = session.beginTransaction();
//
//            // Ensure no owners exist in the database
//            session.createQuery("DELETE FROM Owner").executeUpdate();
//            transaction.commit();
//
//            // Call the getAllOwners method
//            List<Owner> owners = ownerDao.getAllOwners();
//
//            assertNotNull(owners);
//            assertTrue(owners.isEmpty(), "Owners list should be empty when no owners exist");
//
//        } catch (Exception e) {
//            fail("Exception should not occur when no owners exist");
//        }
//    }
//
//    @Test
//    void testUpdateProfile_Success() {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Transaction transaction = session.beginTransaction();
//
//            // Add an owner to update
//            Owner owner = new Owner();
//            owner.setUsername("old_username");
//            owner.setEmail("old_email@example.com");
//            owner.setPassword("password");
//            owner.setSalt("salt");
//            owner.setRole(UserRole.OWNER);
//            owner.setPhoneNumber("1234567890");
//            session.persist(owner);
//            transaction.commit();
//
//            // Prepare update data
//            Map<String, Object> profile = new HashMap<>();
//            profile.put("username", "new_username");
//            profile.put("dob", "1990-01-01");
//            profile.put("email", "new_email@example.com");
//            profile.put("phoneNumber", "0987654321");
//
//            // Call updateProfile
//            Map<String, Object> result = ownerDao.updateProfile(owner.getId(), profile);
//
//            // Reload owner and assert changes
//            session.beginTransaction();
//            Owner updatedOwner = session.get(Owner.class, owner.getId());
//
//            assertNotNull(updatedOwner);
//            assertEquals("new_username", updatedOwner.getUsername());
//            assertEquals(LocalDate.parse("1990-01-01"), updatedOwner.getDob());
//            assertEquals("new_email@example.com", updatedOwner.getEmail());
//            assertEquals("0987654321", updatedOwner.getPhoneNumber());
//
//            session.getTransaction().rollback(); // Rollback changes to maintain test isolation
//        } catch (Exception e) {
//            fail("Exception should not occur during profile update");
//        }
//    }
//
//    @Test
//    void testUpdateProfile_Failure_OwnerNotFound() {
//        // Prepare update data for a non-existent owner
//        Map<String, Object> profile = new HashMap<>();
//        profile.put("username", "new_username");
//        profile.put("dob", "1990-01-01");
//        profile.put("email", "new_email@example.com");
//        profile.put("phoneNumber", "0987654321");
//
//        // Call updateProfile with an invalid ID
//        Map<String, Object> result = ownerDao.updateProfile(-100, profile);
//
//        // Assert result is not successful
//        assertNotNull(result);
//        assertEquals("failed", result.get("status"));
//    }
//
//    @Test
//    void testGetUserById_Success() {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Transaction transaction = session.beginTransaction();
//
//            // Add a sample owner to the database
//            Owner owner = new Owner();
//            owner.setUsername("sampleUser");
//            owner.setEmail("sampleUser@example.com");
//            owner.setPassword("securePassword");
//            owner.setSalt("randomSalt");
//            owner.setRole(UserRole.OWNER);
//            session.persist(owner);
//
//            transaction.commit();
//
//            // Retrieve the owner by ID
//            Owner retrievedOwner = ownerDao.getUserById(owner.getId());
//
//            assertNotNull(retrievedOwner, "Owner should be retrieved successfully");
//            assertEquals(owner.getUsername(), retrievedOwner.getUsername());
//            assertEquals(owner.getEmail(), retrievedOwner.getEmail());
//            assertNotNull(retrievedOwner.getId(), "Owner ID should not be null");
//
//        } catch (Exception e) {
//            fail("Exception should not occur during successful retrieval of user by ID");
//        }
//    }
//
//    @Test
//    void testGetUserById_NotFound() {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            // Attempt to retrieve a non-existent owner
//            Owner retrievedOwner = ownerDao.getUserById(-1L);
//
//            assertNull(retrievedOwner, "Owner should not be found for invalid ID");
//        } catch (Exception e) {
//            fail("Exception should not occur during retrieval of non-existent user");
//        }
//    }
//
//    @Test
//    void testUpdateUserImage_Success() {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Transaction transaction = session.beginTransaction();
//
//            // Add a sample owner to the database
//            Owner owner = new Owner();
//            owner.setUsername("test_user");
//            owner.setEmail("test_user@example.com");
//            owner.setPassword("securePassword");
//            owner.setSalt("randomSalt");
//            owner.setRole(UserRole.OWNER);
//            session.persist(owner);
//            transaction.commit();
//
//            // Call the updateUserImage method
//            String newImageLink = "http://example.com/profile.jpg";
//            Long id = owner.getId();
//            Map<String, Object> result = ownerDao.updateUserImage(owner.getId(), newImageLink);
//
//            // Reload owner and assert changes
//            session.beginTransaction();
//            Owner updatedOwner = session.get(Owner.class, owner.getId());
//
//            assertNotNull(updatedOwner);
//            assertEquals(newImageLink, updatedOwner.getProfileImage());
//
//            session.getTransaction().rollback();
//        } catch (Exception e) {
//            fail("Exception should not occur during image update");
//        }
//    }
//
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
//        Map<String, Object> result = ownerDao.updateAddress(41, addressData); // Replace 1L with an actual ID
//        assertEquals("success", result.get("status"), "Address update should be successful");
//    }
//
//    @Test
//    void testUpdatePassword_Success() {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Transaction transaction = session.beginTransaction();
//
//            // Retrieve an existing owner
//            Owner existingOwner = session.createQuery("FROM Owner", Owner.class).setMaxResults(1).uniqueResult();
//
//            assertNotNull(existingOwner, "An existing owner must be present in the database for this test");
//
//            // Save current password for comparison
//            String oldPassword = existingOwner.getPassword();
//            String newPassword = "newSecurePassword";
//
//            // Call the updatePassword method
//            Map<String, Object> result = ownerDao.updatePassword(existingOwner.getId(), oldPassword, newPassword);
//
//            // Reload owner and assert changes
//            session.beginTransaction();
//            Owner updatedOwner = session.get(Owner.class, existingOwner.getId());
//
//            assertNotNull(updatedOwner);
//            assertEquals(newPassword, updatedOwner.getPassword(), "Password should be updated successfully");
//
//            // Revert changes for test isolation
//            updatedOwner.setPassword(oldPassword);
//            session.update(updatedOwner);
//            transaction.commit();
//        } catch (Exception e) {
//            fail("Exception should not occur during password update");
//        }
//    }
//
//    @Test
//    void testUpdatePassword_Failure_WrongOldPassword() {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Transaction transaction = session.beginTransaction();
//
//            // Retrieve an existing owner
//            Owner existingOwner = session.createQuery("FROM Owner", Owner.class).setMaxResults(1).uniqueResult();
//
//            assertNotNull(existingOwner, "An existing owner must be present in the database for this test");
//
//            // Attempt to update password with the wrong old password
//            String wrongOldPassword = "incorrectPassword";
//            String newPassword = "newSecurePassword";
//            Map<String, Object> result = ownerDao.updatePassword(existingOwner.getId(), wrongOldPassword, newPassword);
//
//            // Reload owner and assert no changes
//            session.beginTransaction();
//            Owner updatedOwner = session.get(Owner.class, existingOwner.getId());
//
//            assertNotNull(updatedOwner);
//            assertNotEquals(newPassword, updatedOwner.getPassword(), "Password should not be updated with the wrong old password");
//
//            transaction.commit();
//        } catch (Exception e) {
//            fail("Exception should not occur during password update with incorrect old password");
//        }
//    }
//}