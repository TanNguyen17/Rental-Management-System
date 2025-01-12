package com.yourcompany.rentalmanagement.view;
/**
 * @author FTech
 */
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class OwnerView implements Initializable {

    @FXML
    private BorderPane borderPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeSideMenu();
    }

    private void initializeSideMenu() {
        try {
            FXMLLoader sideMenuLoader = new FXMLLoader(getClass().getResource("/fxml/SideMenu.fxml"));
            VBox sideMenu = sideMenuLoader.load();
            SideMenuView sideMenuController = sideMenuLoader.getController();

            FXMLLoader propertiesLoader = new FXMLLoader(getClass().getResource("/fxml/ViewRentalProperties.fxml"));
            Node mainContent = propertiesLoader.load();

            borderPane.setLeft(sideMenu);
            borderPane.setCenter(mainContent);

            sideMenuController.setBorderPane(borderPane);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading views: " + e.getMessage());
        }
    }
}
