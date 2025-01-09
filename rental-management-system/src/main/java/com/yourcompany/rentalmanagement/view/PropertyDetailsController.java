package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.model.Address;
import com.yourcompany.rentalmanagement.model.CommercialProperty;
import com.yourcompany.rentalmanagement.model.Host;
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
    private FlowPane featuresContainer;
    @FXML
    private GridPane addressGrid;
    @FXML
    private VBox hostContainer;
    @FXML
    private VBox contentContainer;

    public void setProperty(Property property) {
        propertyImage.setImage(new Image(property.getImageLink()));
        titleLabel.setText(property.getTitle());

        String propertyType = property instanceof ResidentialProperty ? "Residential Property" : "Commercial Property";
        Label typeLabel = new Label(propertyType);
        typeLabel.getStyleClass().add("property-type-label");

        priceLabel.setText(String.format("$%.2f", property.getPrice()));
        descriptionLabel.setText(property.getDescription());

        contentContainer.getChildren().add(1, typeLabel); // Add after title

        if (property instanceof ResidentialProperty) {
            setupResidentialFeatures((ResidentialProperty) property);
        } else if (property instanceof CommercialProperty) {
            setupCommercialFeatures((CommercialProperty) property);
        }

        setupAddress(property.getAddress());

        setupHostInfo(property);
    }

    private void setupResidentialFeatures(ResidentialProperty property) {
        addFeature(featuresContainer, property.getNumberOfBedrooms() + " Bedrooms");
        if (property.isGardenAvailability()) {
            addFeature(featuresContainer, "Garden Available");
        }
        if (property.isPetFriendliness()) {
            addFeature(featuresContainer, "Pet Friendly");
        }
    }

    private void setupCommercialFeatures(CommercialProperty property) {
        addFeature(featuresContainer, "Business Type: " + property.getBusinessType());
        addFeature(featuresContainer, String.format("%.0f sq ft", property.getSquareFootage()));
        if (property.isParkingSpace()) {
            addFeature(featuresContainer, "Parking Available");
        }
    }

    private void setupAddress(Address address) {
        addAddressField("Street", address.getStreet());
        addAddressField("City", address.getCity());
        addAddressField("State", address.getState());
    }

    private void setupHostInfo(Property property) {
        if (property.getHosts() != null && !property.getHosts().isEmpty()) {
            for (Host host : property.getHosts()) {
                Label hostLabel = new Label("Hosted by: " + host.getUsername());
                hostLabel.getStyleClass().add("host-info");
                hostContainer.getChildren().add(hostLabel);
            }
        }
    }

    private void addFeature(FlowPane container, String text) {
        Label label = new Label(text);
        label.getStyleClass().add("feature-label");
        container.getChildren().add(label);
    }

    private void addAddressField(String label, String value) {
        int row = addressGrid.getRowCount();
        addressGrid.add(new Label(label + ":"), 0, row);
        addressGrid.add(new Label(value), 1, row);
    }
}
