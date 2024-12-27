package com.yourcompany.rentalmanagement.dao;

import com.yourcompany.rentalmanagement.model.Owner;

import java.util.Map;
import java.util.Objects;

public interface UserDao {
    public void loadData();
    public <T extends Object> T getUserById(long id);
    public void updateProfile(long id, Map<String, Object> profile);
    public void updateUserImage(long id, String imageLink);
    public void updateAddress(long id, Map<String, Object> address);
    public void updatePassword(long id, String password);
}
