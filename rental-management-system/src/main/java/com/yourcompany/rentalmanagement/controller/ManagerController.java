package com.yourcompany.rentalmanagement.controller;

/**
 * @author FTech
 */

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ManagerController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }


}