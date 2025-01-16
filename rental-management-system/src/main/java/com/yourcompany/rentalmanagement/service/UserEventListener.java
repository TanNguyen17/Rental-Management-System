package com.yourcompany.rentalmanagement.service;

import com.yourcompany.rentalmanagement.model.User;
import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class UserEventListener {
    private static final Logger LOGGER = LogManager.getLogger(UserEventListener.class);

    @PrePersist
    @PreUpdate
    @PostRemove
    private void beforeAnyUpdate(User user) {
        if (user.getId() == 0) {
            LOGGER.info("[USER AUDIT] About to add a user");
        } else {
            LOGGER.info("[USER AUDIT] About to update/delete user: " + user.getId());
        }
    }

    @PostPersist
    @PostUpdate
    @PostRemove
    private void afterAnyUpdate(User user) {
        LOGGER.info("[USER AUDIT] add/update/delete complete for user: " + user.getId());
    }

    @PostLoad
    private void afterLoad(User user) {
        LOGGER.info("[USER AUDIT] user loaded from database: " + user.getId());
    }
}
