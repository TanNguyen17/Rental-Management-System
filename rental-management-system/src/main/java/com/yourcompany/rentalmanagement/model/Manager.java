package com.yourcompany.rentalmanagement.model;
/**
 * @author FTech
 */

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "Manager", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id"})})
public class Manager extends User {
    // Manager has access to all functionalities through services

    // add extra fields if needed nhe
    /*
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(name = "last_action")
    private String lastAction;
    
    @Column(name = "action_timestamp")
    private LocalDateTime actionTimestamp;
     */
}
