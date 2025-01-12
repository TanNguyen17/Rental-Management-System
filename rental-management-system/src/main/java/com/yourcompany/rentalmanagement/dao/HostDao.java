package com.yourcompany.rentalmanagement.dao;

import java.util.List;
import java.util.Map;

import com.yourcompany.rentalmanagement.model.Host;

public interface HostDao {

    List<Host> getAllHosts();

    Host getHostById(long id);

    Map<String, Object> updateProfile(long id, Map<String, Object> profile);

    Map<String, Object> updateUserImage(long id, String imageLink);

    Map<String, Object> updateAddress(long id, Map<String, Object> data);

    Map<String, Object> updatePassword(long id, String oldPassword, String newPassword);
}