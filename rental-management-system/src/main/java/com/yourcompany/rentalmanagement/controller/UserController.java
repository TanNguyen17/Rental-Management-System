package com.yourcompany.rentalmanagement.controller;

import com.yourcompany.rentalmanagement.dao.OwnerDaoImp;
import com.yourcompany.rentalmanagement.model.User;

import java.util.Map;

public class UserController {
    OwnerDaoImp ownerDaoImp = new OwnerDaoImp();
    User user = new User();
    public User getUserProfile(long id) {
        user = ownerDaoImp.getUserById(id);
        return user;
    }
    public void updateProfile(long userId, Map<String, Object> data) {
        ownerDaoImp.updateProfile(userId, data);
    }

    public void updateAddress(long userId, Map<String, Object> data) {
        ownerDaoImp.updateAddress(userId, data);
    }

    public void updateImageLink(long userId, String imageLink) {
        ownerDaoImp.updateUserImage(userId, imageLink);
    }

    public void updatePassword(long userId, String password) {
        ownerDaoImp.updatePassword(userId, password);
    }
}
