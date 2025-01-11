package com.yourcompany.rentalmanagement;

import com.yourcompany.rentalmanagement.model.UserRole;
import com.yourcompany.rentalmanagement.service.PaymentScheduler;
import com.yourcompany.rentalmanagement.util.AddressData;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import com.yourcompany.rentalmanagement.util.UserSession;
import com.yourcompany.rentalmanagement.view.LoginViewController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.hibernate.SessionFactory;

public class MainApp extends Application {
    private LoginViewController loginViewController = new LoginViewController();
    private FXMLLoader loader;
    private PaymentScheduler scheduler = new PaymentScheduler();
    @Override
    public void start(Stage primaryStage) {
        new Thread(() -> {
            // Load address data
            AddressData.fetchProvinceData();
            System.out.println("Province Data fetched: ");

        }).start();

        new Thread(() -> {
            scheduler.startPaymentGeneration();
        }).start();

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

                // Ae co j implement cai nay when ae tao main view nhe
                if (userSession.getCurrentUser().getRole() == UserRole.TENANT) {
                    loader = new FXMLLoader(getClass().getResource("/fxml/TenantView.fxml"));
                } else if (userSession.getCurrentUser().getRole() == UserRole.OWNER) {
                    loader = new FXMLLoader(getClass().getResource("/fxml/ViewRentalProperties.fxml"));
                }
            } else {
                loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            }
            // If no valid stored session --> show login view
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
