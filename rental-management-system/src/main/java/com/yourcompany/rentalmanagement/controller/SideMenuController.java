package com.yourcompany.rentalmanagement.controller;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;

public class SideMenuController {
    ObservableList<String> roles = FXCollections.observableArrayList("Tenant", "Owner", "Host", "Manager");

    @FXML
    private ComboBox roleItems;

    public void initiateComboBox(){
        roleItems = new ComboBox<>(roles);
        System.out.println("You clicked!");
    }

}