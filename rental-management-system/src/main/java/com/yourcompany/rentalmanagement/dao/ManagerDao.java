package com.yourcompany.rentalmanagement.dao;

/**
 * @author FTech
 */

import java.util.List;
import java.util.Map;

import com.yourcompany.rentalmanagement.model.Manager;
import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import com.yourcompany.rentalmanagement.model.User;

public interface ManagerDao {

    // Basic CRUD for Manager entity
    void create(Manager manager);

    Manager read(Long id);

    void update(Manager manager);

    void delete(Manager manager);

    Manager findByUsername(String username);

    // Administrative operations (previously in ManagerService)
    <T> void createEntity(T entity);

    <T> T readEntity(Class<T> entityClass, Long id);

    <T> void updateEntity(T entity);

    <T> void deleteEntity(T entity);

    // Entity listing operations
    List<User> getAllUsers();

    List<RentalAgreement> getAllAgreements();

    List<Payment> getAllPayments();

    List<Manager> getAllManagers();

    // Data management operations
    void softDeleteEntity(Object entity);

    void validateDataConsistency();

    Map<String, Object> updateProfile(long id, Map<String, Object> profile);

    Map<String, Object> updateUserImage(long id, String imageLink);

    Map<String, Object> updateAddress(long id, Map<String, Object> data);

    Map<String, Object> updatePassword(long id, String oldPassword, String newPassword);
}
