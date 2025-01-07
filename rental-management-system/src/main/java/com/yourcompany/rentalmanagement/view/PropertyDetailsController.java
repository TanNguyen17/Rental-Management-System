package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class PropertyDetailsController {
    @FXML private ImageView propertyImage;
    @FXML private Label titleLabel;
    @FXML private Label priceLabel;
    @FXML private Label statusLabel;
    @FXML private TextArea descriptionArea;
    @FXML private VBox addressBox;
    @FXML private VBox specificDetailsBox;

    public void setProperty(Property property) {
        // Set basic property details
        propertyImage.setImage(new Image(property.getImageLink()));
        titleLabel.setText(property.getTitle());
        priceLabel.setText(String.format("$%.2f", property.getPrice()));
        statusLabel.setText(property.getStatus().toString());
        descriptionArea.setText(property.getDescription());
        
        // Set address
        Address address = property.getAddress();
        addressBox.getChildren().addAll(
            new Label(address.getNumber() + " " + address.getStreet()),
            new Label(address.getCity() + ", " + address.getState())
        );
        
        // Set specific details based on property type
        if (property instanceof CommercialProperty) {
            setCommercialDetails((CommercialProperty) property);
        } else if (property instanceof ResidentialProperty) {
            setResidentialDetails((ResidentialProperty) property);
        }
    }

    private void setCommercialDetails(CommercialProperty property) {
        specificDetailsBox.getChildren().addAll(
            new Label("Business Type: " + property.getBusinessType()),
            new Label("Square Footage: " + property.getSquareFootage()),
            new Label("Parking Space: " + (property.isParkingSpace() ? "Yes" : "No"))
        );
    }

    private void setResidentialDetails(ResidentialProperty property) {
        specificDetailsBox.getChildren().addAll(
            new Label("Bedrooms: " + property.getNumberOfBedrooms()),
            new Label("Garden: " + (property.isGardenAvailability() ? "Yes" : "No")),
            new Label("Pet Friendly: " + (property.isPetFriendliness() ? "Yes" : "No"))
        );
    }
}