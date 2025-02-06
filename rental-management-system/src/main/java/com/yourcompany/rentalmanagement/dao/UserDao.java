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

    public long getNumberOfUser();

    public boolean updateProfile(long id, Map<String, Object> profile);

    public boolean updateUserImage(long id, String imageLink);

    public boolean updateAddress(long id, Map<String, Object> address);

    public boolean updatePassword(long id, String oldPassword, String newPassword);


}