package com.yourcompany.rentalmanagement.view;
/**
 * @author FTech
 */
import java.util.HashMap;
import java.util.Map;

import com.yourcompany.rentalmanagement.controller.UserController;
import com.yourcompany.rentalmanagement.model.Property;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PropertyListView {

    private UserController userController = new UserController();

    private void openRentalAgreementCreation(Property property) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RentalAgreementCreationView.fxml"));
            VBox newRoot = loader.load();

            RentalAgreementCreationView controller = loader.getController();

            // Pass the necessary data
            Map<String, Object> data = new HashMap<>();
            data.put("property", property);
            data.put("owner", property.getOwner());
            data.put("hosts", userController.getAllHosts());
            controller.setInformation(data);

            // Create new stage with proper size
            Stage newStage = new Stage();
            newStage.setTitle("Create Rental Agreement");
            Scene scene = new Scene(newRoot);
            newStage.setScene(scene);
            newStage.setMinWidth(1000);
            newStage.setMinHeight(700);
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
