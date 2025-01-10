package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.model.Address;
import com.yourcompany.rentalmanagement.model.CommercialProperty;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.ResidentialProperty;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class PropertyDetailsController {

    @FXML
    private ImageView propertyImage;
    @FXML
    private Label titleLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private FlowPane featuresContainer;
    @FXML
    private GridPane addressGrid;
    @FXML
    private VBox hostContainer;

    public void setProperty(Property property) {
        try {
            // Set basic info
            titleLabel.setText(property.getTitle());
            priceLabel.setText(String.format("$%.2f per month", property.getPrice()));
            descriptionLabel.setText(property.getDescription());
            statusLabel.setText(property.getStatus().toString());
            statusLabel.getStyleClass().add(property.getStatus().toString().toLowerCase());

            // Load image
            propertyImage.setImage(new Image(property.getImageLink()));

            // Setup features
            featuresContainer.getChildren().clear();
            if (property instanceof ResidentialProperty) {
                setupResidentialFeatures((ResidentialProperty) property);
            } else if (property instanceof CommercialProperty) {
                setupCommercialFeatures((CommercialProperty) property);
            }

            // Setup address
            setupAddress(property.getAddress());

            // Setup host info
            setupHostInfo(property);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupResidentialFeatures(ResidentialProperty property) {
        addFeature(String.format("%d Bedrooms", property.getNumberOfBedrooms()));
        if (property.isGardenAvailability()) {
            addFeature("Garden Available");
        }
        if (property.isPetFriendliness()) {
            addFeature("Pet Friendly");
        }
    }

    private void setupCommercialFeatures(CommercialProperty property) {
        addFeature("Business Type: " + property.getBusinessType());
        addFeature(String.format("%.0f m²", property.getSquareFootage()));
        if (property.isParkingSpace()) {
            addFeature("Parking Available");
        }
    }

    private void addFeature(String text) {
        Label feature = new Label(text);
        feature.getStyleClass().add("feature-label");
        featuresContainer.getChildren().add(feature);
    }

    private void setupAddress(Address address) {
        addressGrid.getChildren().clear();

        addAddressField(0, "Street", address.getStreet());
        addAddressField(1, "Number", address.getNumber());
        addAddressField(2, "City", address.getCity());
        addAddressField(3, "State", address.getState());
    }

    private void addAddressField(int row, String label, String value) {
        Label labelNode = new Label(label + ":");
        Label valueNode = new Label(value);

        labelNode.getStyleClass().add("address-info");
        valueNode.getStyleClass().add("address-info");

        addressGrid.add(labelNode, 0, row);
        addressGrid.add(valueNode, 1, row);
    }

    private void setupHostInfo(Property property) {
        hostContainer.getChildren().clear();

        if (property.getHosts().isEmpty()) {
            Label noHostLabel = new Label("No host assigned");
            noHostLabel.getStyleClass().add("host-info");
            hostContainer.getChildren().add(noHostLabel);
        } else {
            property.getHosts().forEach(host -> {
                Label hostLabel = new Label("Hosted by: " + host.getUsername());
                hostLabel.getStyleClass().add("host-info");
                hostContainer.getChildren().add(hostLabel);
            });
        }
    }
}
