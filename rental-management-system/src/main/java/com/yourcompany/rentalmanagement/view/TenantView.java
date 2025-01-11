package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.util.UserSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TenantView implements Initializable {

    private UserSession userSession = UserSession.getInstance();

    @FXML
    private BorderPane borderPane;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeSideMenu();
    }

    private void initializeSideMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SideMenu.fxml"));
            VBox sideMenu = loader.load();
            SideMenuView sideMenuView = loader.getController();
            sideMenuView.setBorderPane(borderPane);
            borderPane.setLeft(sideMenu);
            System.out.println("BorderPane set in SideMenuView.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
