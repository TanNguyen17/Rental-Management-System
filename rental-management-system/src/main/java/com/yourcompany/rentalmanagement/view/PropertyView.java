package com.yourcompany.rentalmanagement.view;
/**
 * @author FTech
 */
import com.yourcompany.rentalmanagement.controller.PropertyController;
import com.yourcompany.rentalmanagement.model.Address;
import com.yourcompany.rentalmanagement.model.CommercialProperty;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.ResidentialProperty;
import com.yourcompany.rentalmanagement.util.AddressData;
import com.yourcompany.rentalmanagement.util.HibernateUtil;

import com.yourcompany.rentalmanagement.view.components.LoadingSpinner;
import com.yourcompany.rentalmanagement.view.components.Toast;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.hibernate.Session;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class PropertyView implements Initializable {

    private final PropertyController propertyController = new PropertyController();
    private Map<String, Object> data = new HashMap<>();
    private Map<String, List<String>> provinceCities = new HashMap<>();
    private Map<String, List<String>> cityWards = new HashMap<>();
    private LoadingSpinner loadingSpinner;
    private List<Property> properties = new ArrayList<>();

    @FXML
    private ListView<Property> propertyList;

    @FXML
    private ChoiceBox<String> provinceChoice;

    @FXML
    private ChoiceBox<String> districtChoice;

    @FXML
    private ChoiceBox<String> wardChoice;

    @FXML
    private Button findButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadAddress();
        setupLoadingSpinner();

        new Thread(() -> {
            loadAvailableProperty();
            Platform.runLater(() -> {
                setupPropertyList();
            });
        }).start();
    }

    private void setupLoadingSpinner() {
        try {
            loadingSpinner = new LoadingSpinner();
            VBox mainContainer = (VBox) propertyList.getParent();
            loadingSpinner.prefWidthProperty().bind(mainContainer.widthProperty());
            loadingSpinner.prefHeightProperty().bind(mainContainer.heightProperty());
            mainContainer.getChildren().add(loadingSpinner);
            loadingSpinner.setViewOrder(-1000);
            loadingSpinner.toFront();
        } catch (Exception e) {
            System.err.println("Error setting up loading spinner: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAddress() {
        Platform.runLater(() -> {
            provinceChoice.getItems().addAll(AddressData.provinceCities.keySet());

            provinceChoice.setOnAction(event -> {
                String selectedProvince = provinceChoice.getValue();
                updateDistrictCombobox(selectedProvince);
            });

            districtChoice.setOnAction(event -> {
                String selectedDistrict = districtChoice.getValue();
                updateWardCombobox(selectedDistrict);
            });
        });
    }

    @FXML
    void filterProperty(ActionEvent event) {
        data.put("province", provinceChoice.getValue());
        data.put("district", districtChoice.getValue());
        data.put("ward", wardChoice.getValue());

        loadAvailableProperty();
        setupPropertyList();
    }

    private void loadAvailableProperty() {
        loadingSpinner.show();
        //Handle exceptions from the controller
        try {
            properties = propertyController.getPropertyByStatus(Property.propertyStatus.AVAILABLE, data);
            propertyList.setItems(FXCollections.observableList(properties));
        } catch (Exception e) {
            System.err.println("Error loading properties: " + e.getMessage());
            //Show error in UI
            showError("Error loading properties: " + e.getMessage());
        } finally {
            loadingSpinner.hide();
        }
    }

    private void setupPropertyList() {
        propertyList.setFocusTraversable(false);
        propertyList.getStyleClass().add("property-list");

        propertyList.setMinHeight(500);  // Adjust this value as needed
        propertyList.setMinWidth(900);
        propertyList.setPrefHeight(Region.USE_COMPUTED_SIZE);
        propertyList.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(propertyList, Priority.ALWAYS);

        propertyList.setCellFactory(lv -> new PropertyView.PropertyListCell());
    }

    // Custom ListCell for better performance
    private class PropertyListCell extends ListCell<Property> {

        private final HBox card;
        private final ImageView imageView;
        private final Label titleLabel, priceLabel, descriptionLabel, hostLabel, addressLabel;
        private final FlowPane attributes;
        private final VBox contentBox, buttonsBox;

        public PropertyListCell() {

            card = new HBox(20);
            card.getStyleClass().addAll("property-card", "hoverable-card");
            card.setPadding(new Insets(15));
            card.setAlignment(Pos.CENTER_LEFT);

            VBox imageContainer = new VBox();
            imageContainer.getStyleClass().add("image-container");
            imageView = new ImageView();
            imageView.setFitHeight(140);
            imageView.setFitWidth(180);
            imageView.getStyleClass().add("property-image");
            imageContainer.getChildren().add(imageView);

            contentBox = new VBox(10);
            contentBox.getStyleClass().add("content-box");
            contentBox.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(contentBox, Priority.ALWAYS);

            titleLabel = new Label();
            titleLabel.getStyleClass().add("property-title");

            priceLabel = new Label();
            priceLabel.getStyleClass().add("property-price");

            HBox headerBox = new HBox(15);
            headerBox.setAlignment(Pos.CENTER_LEFT);
            headerBox.getChildren().addAll(titleLabel, priceLabel);

            descriptionLabel = new Label();
            descriptionLabel.getStyleClass().add("property-description");
            descriptionLabel.setWrapText(true);

            hostLabel = new Label();
            hostLabel.getStyleClass().add("host-label");

            addressLabel = new Label();
            addressLabel.getStyleClass().add("address-label");

            attributes = new FlowPane(10, 10);
            attributes.getStyleClass().add("property-attributes");

            contentBox.getChildren().addAll(headerBox, descriptionLabel, hostLabel,
                    addressLabel, attributes);

            buttonsBox = new VBox(8);
            buttonsBox.setAlignment(Pos.CENTER_RIGHT);
            buttonsBox.getStyleClass().add("buttons-container");

            card.getChildren().addAll(imageContainer, contentBox, buttonsBox);
        }

        @Override
        protected void updateItem(Property property, boolean empty) {
            super.updateItem(property, empty);

            if (empty || property == null) {
                setGraphic(null);
            } else {

                titleLabel.setText(property.getTitle());
                priceLabel.setText(String.format("$%.2f", property.getPrice()));
                descriptionLabel.setText(property.getDescription());
                hostLabel.setText("Hosted by "
                        + (property.getHosts().isEmpty() ? "No Host" : property.getHosts().get(0).getUsername()));
                addressLabel.setText(formatAddress(property.getAddress()));

                loadPropertyImage(property.getImageLink(), imageView);

                attributes.getChildren().clear();
                if (property instanceof CommercialProperty) {
                    setupCommercialAttributes((CommercialProperty) property, attributes);
                } else if (property instanceof ResidentialProperty) {
                    setupResidentialAttributes((ResidentialProperty) property, attributes);
                }

                buttonsBox.getChildren().clear();
                buttonsBox.getChildren().addAll(
                        createActionButton("View Details", "view-button", () -> showPropertyDetails(property)),
                        createActionButton("Rent", "edit-button", () -> rentProperty(property))
                );

                setGraphic(card);
            }
        }
    }

    private void loadPropertyImage(String imageUrl, ImageView imageView) {
        new Thread(() -> {
            try {
                Image image = new Image(imageUrl, true); // true enables background loading
                Platform.runLater(() -> imageView.setImage(image));
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
            }
        }).start();
    }

    private void setupCommercialAttributes(CommercialProperty cp, FlowPane attributes) {
        addAttribute(attributes, "Business Type: " + cp.getBusinessType());
        addAttribute(attributes, cp.isParkingSpace() ? "Parking Available" : "No Parking");
        addAttribute(attributes, String.format("%.0f m²", cp.getSquareFootage()));
    }

    private void setupResidentialAttributes(ResidentialProperty rp, FlowPane attributes) {
        addAttribute(attributes, rp.getNumberOfBedrooms() + " Bedrooms");
        if (rp.isGardenAvailability()) {
            addAttribute(attributes, "Garden");
        }
        if (rp.isPetFriendliness()) {
            addAttribute(attributes, "Pet Friendly");
        }
    }

    private void addAttribute(FlowPane container, String text) {
        Label label = new Label(text);
        label.getStyleClass().add("attribute-label");
        label.setStyle("-fx-background-color: #1E3058; -fx-text-fill: white; "
                + "-fx-padding: 5 10; -fx-background-radius: 3;");
        container.getChildren().add(label);
    }

    private MFXButton createActionButton(String text, String styleClass, Runnable action) {
        MFXButton button = new MFXButton(text);
        button.getStyleClass().add(styleClass);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> action.run());
        return button;
    }

    private String formatAddress(Address address) {
        return String.format("%s %s, %s, %s",
                address.getNumber(),
                address.getStreet(),
                address.getWard(),
                address.getDistrict(),
                address.getCity());
    }

    private void showPropertyDetails(Property property) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PropertyDetails.fxml"));
            Scene scene = new Scene(loader.load());

            scene.getStylesheets().add(getClass().getResource("/css/property-list.css").toExternalForm());

            PropertyDetailsController controller = loader.getController();
            controller.setProperty(property);

            Stage stage = new Stage();
            stage.setTitle("Property Details - " + property.getTitle());
            stage.setScene(scene);

            // make it modal, this is to prevent the user from interacting with the main window while the property details are open
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            showError("Error opening property details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void rentProperty(Property property) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Implement rent property logic here
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RentalAgreementCreationView.fxml"));
            Scene scene = new Scene(loader.load());
            System.out.println(property.getId());

            if (property instanceof ResidentialProperty) {
                ResidentialProperty residentialProperty = (ResidentialProperty) property;
                data = propertyController.getResidentialPropertyData(residentialProperty.getId());

            } else if (property instanceof CommercialProperty) {
                CommercialProperty commercialProperty = (CommercialProperty) property;
                data = propertyController.getCommercialPropertyData(commercialProperty.getId());
            }

            RentalAgreementCreationView controller = loader.getController();
            controller.setInformation(data);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Rental Agreement Details");
            stage.show();
        } catch (IOException e) {
            System.err.println("Error showing property details: " + e.getMessage());
        }
    }

    // Update cities data into combobox when province is selected
    private void updateDistrictCombobox(String selectedProvince) {
        if (selectedProvince != null) {
            List<String> cities = AddressData.provinceCities.getOrDefault(selectedProvince, new ArrayList<>());
            ObservableList<String> cityList = FXCollections.observableArrayList(cities);
            districtChoice.setItems(cityList);
        } else {
            districtChoice.getItems().clear();
        }
    }

    // Update wards data into combobox when city is selected
    private void updateWardCombobox(String selectedCity) {
        if (selectedCity != null) {
            List<String> wards = AddressData.cityWards.getOrDefault(selectedCity, new ArrayList<>());
            ObservableList<String> wardList = FXCollections.observableArrayList(wards);
            wardChoice.setItems(wardList);
        } else {
            wardChoice.getItems().clear();
        }
    }

    private void showSuccess(String message) {
        Toast.showSuccess((Stage) propertyList.getScene().getWindow(), message);
    }

    private void showError(String message) {
        Toast.showError((Stage) propertyList.getScene().getWindow(), message);
    }
}