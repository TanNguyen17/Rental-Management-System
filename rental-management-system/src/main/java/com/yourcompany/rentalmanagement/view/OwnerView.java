package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.controller.SideMenuController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OwnerView implements Initializable {
    @FXML
    private BorderPane borderPane;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeSideMenu();
    }

    private void initializeSideMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SideMenu.fxml"));
            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/fxml/ViewRentalProperties.fxml"));

            VBox sideMenu = loader.load();
            AnchorPane main = mainLoader.load();
            SideMenuView sideMenuView = loader.getController();
            sideMenuView.setBorderPane(borderPane);

            borderPane.setLeft(sideMenu);
            borderPane.setCenter(main);

            System.out.println("BorderPane set in SideMenuView.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
