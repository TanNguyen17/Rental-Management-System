package com.yourcompany.rentalmanagement.view;

import java.io.IOException;
import java.util.List;

import org.hibernate.Session;

import com.yourcompany.rentalmanagement.dao.PropertyDao;
import com.yourcompany.rentalmanagement.dao.impl.PropertyDaoImpl;
import com.yourcompany.rentalmanagement.model.CommercialProperty;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.ResidentialProperty;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import com.yourcompany.rentalmanagement.util.UserSession;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewRentalPropertiesController {

    @FXML
    private Button addPropertyButton;

    @FXML
    private VBox propertyListContainer;

    private final PropertyDao propertyDao;

    public ViewRentalPropertiesController() {
        propertyDao = new PropertyDaoImpl();
    }

    @FXML
    public void initialize() {
        System.out.println("ViewRentalPropertiesController initialized");
        loadProperties();
    }

    private void loadProperties() {
        try {
            // Get properties for current owner
            long ownerId = UserSession.getInstance().getCurrentUser().getId();
            List<Property> properties = propertyDao.getPropertiesByOwner(ownerId);

            propertyListContainer.getChildren().clear();

            for (Property property : properties) {
                createPropertyCard(property);
            }
        } catch (Exception e) {
            System.err.println("Error loading properties: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createPropertyCard(Property property) {
        HBox card = new HBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #ddd;");

        // Property image
        ImageView imageView = new ImageView(new Image(property.getImageLink()));
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);

        // Property info
        VBox info = new VBox(5);
        info.getChildren().addAll(
                new Label("Title: " + property.getTitle()),
                new Label("Price: $" + property.getPrice()),
                new Label("Status: " + property.getStatus())
        );

        // View details button
        Button viewButton = new Button("View Details");
        viewButton.setOnAction(e -> showPropertyDetails(property));

        card.getChildren().addAll(imageView, info, viewButton);
        propertyListContainer.getChildren().add(card);
    }

    private void showPropertyDetails(Property property) {
        try {
            // Reload the property with its associations in a new session
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Property refreshedProperty;
                if (property instanceof ResidentialProperty) {
                    refreshedProperty = session.createQuery(
                            "FROM ResidentialProperty p "
                            + "LEFT JOIN FETCH p.address "
                            + "WHERE p.id = :id",
                            ResidentialProperty.class)
                            .setParameter("id", property.getId())
                            .getSingleResult();
                } else {
                    refreshedProperty = session.createQuery(
                            "FROM CommercialProperty p "
                            + "LEFT JOIN FETCH p.address "
                            + "WHERE p.id = :id",
                            CommercialProperty.class)
                            .setParameter("id", property.getId())
                            .getSingleResult();
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PropertyDetails.fxml"));
                Scene scene = new Scene(loader.load());

                PropertyDetailsController controller = loader.getController();
                controller.setProperty(refreshedProperty);

                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("Property Details");
                stage.show();
            }
        } catch (IOException e) {
            System.err.println("Error showing property details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAddProperty() {
        try {
            System.out.println("Add Property button clicked");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PropertyForm.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Add New Property");

            // Add window close listener to refresh property list
            stage.setOnHidden(e -> loadProperties());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading PropertyForm: " + e.getMessage());
        }
    }
}
