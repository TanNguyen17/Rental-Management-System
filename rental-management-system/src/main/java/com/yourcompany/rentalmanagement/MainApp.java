package com.yourcompany.rentalmanagement;

import com.yourcompany.rentalmanagement.dao.HostDaoImp;
import com.yourcompany.rentalmanagement.dao.OwnerDaoImp;
import com.yourcompany.rentalmanagement.dao.RentalAgreementDao;
import com.yourcompany.rentalmanagement.dao.RentalAgreementDaoImp;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HostDashboardView.fxml"));
        Scene scene = new Scene(loader.load(), 1280, 720);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Rental Management System");
        primaryStage.show();
    }

    public static void main(String[] args) {
        //launch(args);
        RentalAgreementDaoImp rentalAgreementDao = new RentalAgreementDaoImp();
        rentalAgreementDao.loadData();
    }
}
