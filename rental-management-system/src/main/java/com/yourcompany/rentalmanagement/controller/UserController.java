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
    public void updateUserProfile(Map<String, Object> data) {

    }

    public void updateUserImageLink(long userId, String imageLink) {
        ownerDaoImp.updateUserImage(userId, imageLink);
    }
}
