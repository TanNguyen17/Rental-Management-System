package com.yourcompany.rentalmanagement.controller;

import java.util.Map;

import com.yourcompany.rentalmanagement.dao.impl.OwnerDaoImpl;
import com.yourcompany.rentalmanagement.model.User;

public class UserController {

    private OwnerDaoImpl ownerDao;
    User user = new User();

    public UserController() {
        ownerDao = new OwnerDaoImpl();
    }

    public User getUserProfile(long id) {
        user = ownerDao.getUserById(id);
        return user;
    }

    public void updateProfile(long userId, Map<String, Object> data) {
        ownerDao.updateProfile(userId, data);
    }

    public void updateAddress(long userId, Map<String, Object> data) {
        ownerDao.updateAddress(userId, data);
    }

    public void updateImageLink(long userId, String imageLink) {
        ownerDao.updateUserImage(userId, imageLink);
    }

    public void updatePassword(long userId, String password) {
        ownerDao.updatePassword(userId, password);
    }
}
