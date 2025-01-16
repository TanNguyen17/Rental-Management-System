package com.yourcompany.rentalmanagement.controller;

/**
 * @author FTech
 */

import java.util.*;

import com.yourcompany.rentalmanagement.dao.impl.HostDaoImpl;
import com.yourcompany.rentalmanagement.dao.impl.ManagerDaoImpl;
import com.yourcompany.rentalmanagement.dao.impl.OwnerDaoImpl;
import com.yourcompany.rentalmanagement.dao.impl.TenantDaoImpl;
import com.yourcompany.rentalmanagement.model.*;
import com.yourcompany.rentalmanagement.view.ProfileView;

public class UserController {

    private OwnerDaoImpl ownerDao;
    private TenantDaoImpl tenantDao;
    private HostDaoImpl hostDao;
    private ManagerDaoImpl managerDao;
    private ProfileView profileView;
    private Map<String, Object> result;
    private List<User> users;

    User user = new User();

    public List<Host> getAllHosts(){
        return hostDao.getAllHosts();
    }

    public List<Tenant> getAllTenant(){
        return tenantDao.getAllTenants();
    }

    public List<Owner> getAllOwners(){
        return ownerDao.getAllOwners();
    }

    public UserController() {
        this.ownerDao = new OwnerDaoImpl();
        this.tenantDao = new TenantDaoImpl();
        this.hostDao = new HostDaoImpl();
        this.managerDao = new ManagerDaoImpl();
        this.users = new ArrayList<>();
    }

    public User getUserProfile(long id, User.UserRole role) {
        if (role == User.UserRole.TENANT) {
            user = tenantDao.getUserById(id);
        } else if (role == User.UserRole.HOST) {
            user = ownerDao.getUserById(id);
        } else if (role == User.UserRole.OWNER) {
            user = ownerDao.getUserById(id);
        } else if (role == User.UserRole.MANAGER) {
            user = managerDao.read(id);
        }
        return user;
    }

    public List<Tenant> getTenants() {
        return tenantDao.loadAll();
    }

    public void updateProfile(long userId, Map<String, Object> data, User.UserRole role) {
        profileView = new ProfileView();

        if (role == User.UserRole.TENANT) {
            result = tenantDao.updateProfile(userId, data);
        } else if (role == User.UserRole.HOST) {
            result = hostDao.updateProfile(userId, data);
        } else if (role == User.UserRole.OWNER) {
            result = ownerDao.updateProfile(userId, data);
        } else if (role == User.UserRole.MANAGER) {
            result = hostDao.updateProfile(userId, data);
        }

        if (result.get("status").toString().equals("success")) {
            profileView.showSuccessAlert("Update Profile", result.get("message").toString());
        } else {
            profileView.showErrorAlert("Update Profile", result.get("message").toString());
        }
    }

    public void updateAddress(long userId, Map<String, Object> data, User.UserRole role) {
        profileView = new ProfileView();

        if (role == User.UserRole.TENANT) {
            result = tenantDao.updateAddress(userId, data);
        } else if (role == User.UserRole.HOST) {
            result = hostDao.updateAddress(userId, data);
        } else if (role == User.UserRole.OWNER) {
            result = ownerDao.updateAddress(userId, data);
        } else if (role == User.UserRole.MANAGER) {
            result = hostDao.updateAddress(userId, data);
        }

        if (result.get("status").toString().equals("success")) {
            profileView.showSuccessAlert("Update Address", result.get("message").toString());
        } else {
            profileView.showErrorAlert("Update Address", result.get("message").toString());
        }
    }

    public void updateImageLink(long userId, String imageLink, User.UserRole role) {
        profileView = new ProfileView();

        if (role == User.UserRole.TENANT) {
            result = tenantDao.updateUserImage(userId, imageLink);
        } else if (role == User.UserRole.HOST) {
            result = hostDao.updateUserImage(userId, imageLink);
        } else if (role == User.UserRole.OWNER) {
            result = ownerDao.updateUserImage(userId, imageLink);
        } else if (role == User.UserRole.MANAGER) {
            result = hostDao.updateUserImage(userId, imageLink);
        }

        if (result.get("status").toString().equals("success")) {
            profileView.showSuccessAlert("Update Profile Image", result.get("message").toString());
        } else {
            profileView.showErrorAlert("Update Profile Image", result.get("message").toString());
        }
    }

    public void updatePassword(long userId, String oldPassword, String newPassword, User.UserRole role) {
        profileView = new ProfileView();

        if (role == User.UserRole.TENANT) {
            result = tenantDao.updatePassword(userId, oldPassword, newPassword);
        }

        if (result.get("status").toString().equals("success")) {
            profileView.showSuccessAlert("Update Passsword", result.get("message").toString());
        } else {
            profileView.showErrorAlert("Update Passsword", result.get("message").toString());
        }
    }
}