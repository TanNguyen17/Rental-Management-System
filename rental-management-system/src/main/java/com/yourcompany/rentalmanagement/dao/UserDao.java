package com.yourcompany.rentalmanagement.dao;

/**
 * @author FTech
 */

import com.yourcompany.rentalmanagement.model.Owner;

import java.util.List;

import java.util.Map;

public interface UserDao {

    public <T extends Object> List<T> loadAll();

    public <T extends Object> T getUserById(long id);

    void loadData();

    public long getTotalUsers();

    public Map<String, Object> updateProfile(long id, Map<String, Object> profile);

    public Map<String, Object> updateUserImage(long id, String imageLink);

    public Map<String, Object> updateAddress(long id, Map<String, Object> address);

    public Map<String, Object> updatePassword(long id, String oldPassword, String newPassword);


}