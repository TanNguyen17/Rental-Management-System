package com.yourcompany.rentalmanagement.controller;

import java.util.*;

import com.yourcompany.rentalmanagement.dao.impl.OwnerDaoImpl;
import com.yourcompany.rentalmanagement.dao.impl.TenantDaoImpl;
import com.yourcompany.rentalmanagement.model.Tenant;
import com.yourcompany.rentalmanagement.model.User;
import com.yourcompany.rentalmanagement.model.UserRole;
import com.yourcompany.rentalmanagement.view.ProfileView;

public class UserController {

    private OwnerDaoImpl ownerDao;
    private TenantDaoImpl tenantDao;
    private ProfileView profileView;
    private Map<String, Object> result;
    private List<User> users;

    User user = new User();

    public UserController() {
        this.ownerDao = new OwnerDaoImpl();
        this.tenantDao = new TenantDaoImpl();
        this.users = new ArrayList<>();
    }

    public User getUserProfile(long id, UserRole role) {
        if (role == UserRole.TENANT) {
            user = tenantDao.getUserById(id);
        }
        return user;
    }

    public List<Tenant> getTenants() {
        return tenantDao.loadAll();
    }

    public void updateProfile(long userId, Map<String, Object> data, UserRole role) {
        profileView = new ProfileView();

        if (role == UserRole.TENANT) {
            result = tenantDao.updateProfile(userId, data);
        }

        if (result.get("status").toString().equals("success")) {
            profileView.showSuccessAlert("Update Address", result.get("message").toString());
        } else {
            profileView.showErrorAlert("Update Address", result.get("message").toString());
        }
    }

    public void updateAddress(long userId, Map<String, Object> data, UserRole role) {
        profileView = new ProfileView();

        if (role == UserRole.TENANT) {
            result = tenantDao.updateAddress(userId, data);
        }

        if (result.get("status").toString().equals("success")) {
            profileView.showSuccessAlert("Update Address", result.get("message").toString());
        } else {
            profileView.showErrorAlert("Update Address", result.get("message").toString());
        }
    }

    public void updateImageLink(long userId, String imageLink, UserRole role) {
        profileView = new ProfileView();

        if (role == UserRole.TENANT) {
            result = tenantDao.updateUserImage(userId, imageLink);
        }

        if (result.get("status").toString().equals("success")) {
            profileView.showSuccessAlert("Update Profile Image", result.get("message").toString());
        } else {
            profileView.showErrorAlert("Update Profile Image", result.get("message").toString());
        }
    }

    public void updatePassword(long userId, String oldPassword, String newPassword, UserRole role) {
        profileView = new ProfileView();

        if (role == UserRole.TENANT) {
            result = tenantDao.updatePassword(userId, oldPassword, newPassword);
        }

        if (result.get("status").toString().equals("success")) {
            profileView.showSuccessAlert("Update Passsword", result.get("message").toString());
        } else {
            profileView.showErrorAlert("Update Passsword", result.get("message").toString());
        }
    }
}
