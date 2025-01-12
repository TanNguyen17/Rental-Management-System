package com.yourcompany.rentalmanagement;
/**
 * @author FTech
 */
import org.hibernate.SessionFactory;

import com.yourcompany.rentalmanagement.model.UserRole;
import com.yourcompany.rentalmanagement.service.PaymentScheduler;
import com.yourcompany.rentalmanagement.util.AddressData;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import com.yourcompany.rentalmanagement.util.UserSession;
import com.yourcompany.rentalmanagement.view.LoginViewController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private LoginViewController loginViewController = new LoginViewController();
    private PaymentScheduler paymentScheduler = new PaymentScheduler();
    private FXMLLoader loader = new FXMLLoader();

    @Override
    public void start(Stage primaryStage) {
        new Thread(() -> {
            paymentScheduler.startPaymentGeneration();
            System.out.println("Payment generation started");
        }).start();

        new Thread(() -> {
            AddressData.fetchProvinceData();
            System.out.println("Province Data fetched: ");
        }).start();

        try {
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            if (sessionFactory != null) {
                System.out.println("Hibernate initialized successfully!");
            }

            UserSession userSession = UserSession.getInstance();
            if (userSession.getCurrentUser() != null) {
                System.out.println("Found stored session for user: "
                        + userSession.getCurrentUser().getUsername());

                if (userSession.getCurrentUser().getRole() == UserRole.TENANT) {
                    loader = new FXMLLoader(getClass().getResource("/fxml/TenantView.fxml"));
                } else if (userSession.getCurrentUser().getRole() == UserRole.OWNER) {
                    loader = new FXMLLoader(getClass().getResource("/fxml/OwnerView.fxml"));
                } else if (userSession.getCurrentUser().getRole() == UserRole.HOST) {
                    loader = new FXMLLoader(getClass().getResource("/fxml/HostView.fxml"));
                } else if (userSession.getCurrentUser().getRole() == UserRole.MANAGER) {
                    loader = new FXMLLoader(getClass().getResource("/fxml/ManagerView.fxml"));
                }
            } else {
                loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            }

            Scene scene = new Scene(loader.load());
            scene.getStylesheets().addAll(
                    getClass().getResource("/css/property-list.css").toExternalForm(),
                    getClass().getResource("/css/side-menu.css").toExternalForm(),
                    getClass().getResource("/css/property-form.css").toExternalForm(),
                    getClass().getResource("/css/components/loading-spinner.css").toExternalForm(),
                    getClass().getResource("/css/components/toast.css").toExternalForm()
            );

            // Set default window size
            primaryStage.setWidth(1280);
            primaryStage.setHeight(720);

            // Set minimum dimensions
            primaryStage.setMinWidth(1024);
            primaryStage.setMinHeight(768);

            primaryStage.setScene(scene);
            String title = userSession.getCurrentUser() != null
                    ? "Rental Management System - " + userSession.getCurrentUser().getRole()
                    : "Rental Management System - Login";
            primaryStage.setTitle(title);

            // Center on screen before maximizing
            primaryStage.centerOnScreen();

            // Set to maximized
            primaryStage.setMaximized(true);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
