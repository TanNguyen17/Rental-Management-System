package com.yourcompany.rentalmanagement.dao;

/**
 * @author FTech
 */

import java.util.List;
import java.util.Map;

import com.yourcompany.rentalmanagement.model.Host;

public interface HostDao {

    public List<Host> getAllHosts();

    public Host getHostById(long id);

    public boolean updateProfile(long id, Map<String, Object> profile);

    public boolean updateUserImage(long id, String imageLink);

    public boolean updateAddress(long id, Map<String, Object> data);

    public boolean updatePassword(long id, String oldPassword, String newPassword);

    public long getNumberOfUser();
}