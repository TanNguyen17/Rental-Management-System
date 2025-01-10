package com.yourcompany.rentalmanagement;

import com.yourcompany.rentalmanagement.dao.impl.RentalAgreementDaoImp;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import org.hibernate.SessionFactory;

import com.yourcompany.rentalmanagement.util.HibernateUtil;
import com.yourcompany.rentalmanagement.util.UserSession;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

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
                
                // Ae co j implement cai nay when ae tao main view nhe
            }

            // If no valid stored session --> show login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HostDashboardView.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Rental Management System - Login");
            primaryStage.setWidth(1280);
            primaryStage.setHeight(720);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        launch(args);
//        RentalAgreementDaoImp dao = new RentalAgreementDaoImp();
//        List<RentalAgreement> rentalAgreements = new ArrayList<>();
//        rentalAgreements = dao.getRentalAgreementsById(2);
//        System.out.println(rentalAgreements.toString());
//        System.out.println(dao.getRelatedPayments(rentalAgreements).toString());
    }
}
