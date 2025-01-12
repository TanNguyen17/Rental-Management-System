package com.yourcompany.rentalmanagement.dao;

import java.util.List;

import com.yourcompany.rentalmanagement.model.Host;

public interface HostDao {

    List<String> getAllUserName();

    List<Host> getAllHosts();

    Host getHostById(long id);

    List<String> getAllUserName();
}