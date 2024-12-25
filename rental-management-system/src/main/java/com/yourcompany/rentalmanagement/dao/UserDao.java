package com.yourcompany.rentalmanagement.dao;

import com.yourcompany.rentalmanagement.model.Owner;

import java.util.Objects;

public interface UserDao {
    public void loadData();
    public <T extends Object> T getUserById(long id);
}
