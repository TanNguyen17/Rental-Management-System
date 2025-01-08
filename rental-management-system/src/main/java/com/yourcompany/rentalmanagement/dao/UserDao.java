package com.yourcompany.rentalmanagement.dao;

import java.util.List;
import java.util.Map;

public interface UserDao {

    public void loadData();

    public <T extends Object> T getUserById(long id);

    public List<String> getAllUserName();

    public void updateProfile(long id, Map<String, Object> profile);

    public void updateUserImage(long id, String imageLink);

    public void updateAddress(long id, Map<String, Object> address);

    public void updatePassword(long id, String password);
}
