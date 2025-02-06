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
import com.yourcompany.rentalmanagement.util.DataAccessException;
import com.yourcompany.rentalmanagement.view.ProfileView;

public class UserController {

    private OwnerDaoImpl ownerDao;
    private TenantDaoImpl tenantDao;
    private HostDaoImpl hostDao;
    private ManagerDaoImpl managerDao;
    private Map<String, Object> result = new HashMap<>();
    private List<User> userList;

    User user = new User();

    public List<Host> getAllHosts() {
           return hostDao.loadAll();
    }

    public List<Tenant> getAllTenant(){
        return tenantDao.loadAll();
    }

    public List<Owner> getAllOwners(){
        return ownerDao.loadAll();
    }

    public UserController() {
        this.ownerDao = new OwnerDaoImpl();
        this.tenantDao = new TenantDaoImpl();
        this.hostDao = new HostDaoImpl();
        this.managerDao = new ManagerDaoImpl();
        this.userList = new ArrayList<>();
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
        if (role == User.UserRole.TENANT) {
            tenantDao.updateProfile(userId, data);
        } else if (role == User.UserRole.HOST) {
            hostDao.updateProfile(userId, data);
        } else if (role == User.UserRole.OWNER) {
            ownerDao.updateProfile(userId, data);
        } else if (role == User.UserRole.MANAGER) {
            hostDao.updateProfile(userId, data);
        }
    }

    public void updateAddress(long userId, Map<String, Object> data, User.UserRole role) {

        if (role == User.UserRole.TENANT) {
            tenantDao.updateAddress(userId, data);
        } else if (role == User.UserRole.HOST) {
            hostDao.updateAddress(userId, data);
        } else if (role == User.UserRole.OWNER) {
            ownerDao.updateAddress(userId, data);
        } else if (role == User.UserRole.MANAGER) {
            hostDao.updateAddress(userId, data);
        }
    }

    public void updateImageLink(long userId, String imageLink, User.UserRole role) {

        if (role == User.UserRole.TENANT) {
            tenantDao.updateUserImage(userId, imageLink);
        } else if (role == User.UserRole.HOST) {
            hostDao.updateUserImage(userId, imageLink);
        } else if (role == User.UserRole.OWNER) {
            ownerDao.updateUserImage(userId, imageLink);
        } else if (role == User.UserRole.MANAGER) {
            hostDao.updateUserImage(userId, imageLink);
        }
    }

    public void updatePassword(long userId, String oldPassword, String newPassword, User.UserRole role) {
        if (role == User.UserRole.TENANT) {
            tenantDao.updatePassword(userId, oldPassword, newPassword);
        }
    }
}