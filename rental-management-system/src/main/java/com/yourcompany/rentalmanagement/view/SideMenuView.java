package com.yourcompany.rentalmanagement.view;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;

public class SideMenuView implements Initializable {

    @FXML
    private ComboBox<String> roleComboBox = new ComboBox<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){;
        roleComboBox.setItems(FXCollections.observableArrayList("Owner", "Host", "Tenant", "Manager"));
    }

}