package com.yourcompany.rentalmanagement;

import org.hibernate.SessionFactory;

import com.yourcompany.rentalmanagement.util.HibernateUtil;
import com.yourcompany.rentalmanagement.util.UserSession;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            if (sessionFactory != null) {
                System.out.println("Hibernate initialized successfully!");
            }

            // Check stored token
            UserSession userSession = UserSession.getInstance();
            if (userSession.getCurrentUser() != null) {
                System.out.println("Found stored session for user: "
                        + userSession.getCurrentUser().getUsername());
            }

            // If no valid stored session --> show login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            if (loader.getLocation() == null) {
                System.err.println("Could not find LoginView.fxml");
                return;
            }
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/css/property-list.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Rental Management System - Login");
            primaryStage.setWidth(1280);
            primaryStage.setHeight(720);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.setResizable(true);
            primaryStage.setMaximized(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
