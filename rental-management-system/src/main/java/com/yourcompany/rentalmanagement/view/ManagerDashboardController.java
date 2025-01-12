package com.yourcompany.rentalmanagement.view;
/**
 * @author FTech
 */
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;

public class ManagerDashboardController implements Initializable {

    @FXML
    private Label dashboardTitle;

    @FXML
    private TabPane dashboardPanel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize dashboard components
    }
}
