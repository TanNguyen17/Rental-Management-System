package com.yourcompany.rentalmanagement.view;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.yourcompany.rentalmanagement.dao.PropertyDao;
import com.yourcompany.rentalmanagement.dao.impl.PropertyDaoImpl;
import com.yourcompany.rentalmanagement.model.Address;
import com.yourcompany.rentalmanagement.model.CommercialProperty;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.ResidentialProperty;
import com.yourcompany.rentalmanagement.util.UserSession;
import com.yourcompany.rentalmanagement.view.components.LoadingSpinner;
import com.yourcompany.rentalmanagement.view.components.Toast;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ViewRentalPropertiesController {

    @FXML
    private ListView<Property> propertyList;
    @FXML
    private ComboBox<String> propertyTypeFilterCombo;
    @FXML
    private ComboBox<String> ownerFilterCombo;
    @FXML
    private ComboBox<String> secondaryFilter1;
    @FXML
    private ComboBox<String> secondaryFilter2;
    @FXML
    private ComboBox<String> secondaryFilter3;
    @FXML
    private Label messageLabel;
    @FXML
    private Label pageLabel;
    @FXML
    private MFXButton prevButton;
    @FXML
    private MFXButton nextButton;

    private final PropertyDao propertyDao;
    private static final int PAGE_SIZE = 10;
    private int currentPage = 0;
    private List<Property> allProperties;
    private LoadingSpinner loadingSpinner;

    public ViewRentalPropertiesController() {
        System.out.println("ViewRentalPropertiesController constructor called");
        this.propertyDao = new PropertyDaoImpl();
    }

    @FXML
    public void initialize() {
        System.out.println("ViewRentalPropertiesController initialize called");
        try {
            setupLoadingSpinner();
            setupFilters();
            setupPropertyList();
            loadProperties();
            updatePaginationControls();
        } catch (Exception e) {
            System.err.println("Error initializing ViewRentalPropertiesController: " + e.getMessage());
            e.printStackTrace();
        }
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

    private void setupFilters() {
        ownerFilterCombo.getItems().addAll("My Properties", "All Properties");
        ownerFilterCombo.setValue("My Properties");

        propertyTypeFilterCombo.getItems().addAll("All Types", "Commercial", "Residential");
        propertyTypeFilterCombo.setValue("All Types");

        // Initialize secondary filters (hidden by default)
        setupSecondaryFilters();

        ownerFilterCombo.setOnAction(e -> refreshProperties());
        propertyTypeFilterCombo.setOnAction(e -> {
            updateSecondaryFilters(propertyTypeFilterCombo.getValue());
            refreshProperties();
        });

        // Add listeners for secondary filters
        secondaryFilter1.setOnAction(e -> refreshProperties());
        secondaryFilter2.setOnAction(e -> refreshProperties());
        secondaryFilter3.setOnAction(e -> refreshProperties());
    }

    private void setupSecondaryFilters() {
        secondaryFilter1.setVisible(false);
        secondaryFilter2.setVisible(false);
        secondaryFilter3.setVisible(false);

        secondaryFilter1.setPromptText("Select Filter");
        secondaryFilter2.setPromptText("Select Filter");
        secondaryFilter3.setPromptText("Select Filter");
    }

    private void updateSecondaryFilters(String propertyType) {
        switch (propertyType) {
            case "Commercial":
                showCommercialFilters();
                break;
            case "Residential":
                showResidentialFilters();
                break;
            default:
                hideSecondaryFilters();
                break;
        }
    }

    private void showCommercialFilters() {
        secondaryFilter1.setVisible(true);
        secondaryFilter1.getItems().clear();
        secondaryFilter1.getItems().addAll("All Business Types", "Retail", "Office", "Industrial");
        secondaryFilter1.setValue("All Business Types");

        secondaryFilter2.setVisible(true);
        secondaryFilter2.getItems().clear();
        secondaryFilter2.getItems().addAll("All Parking", "With Parking", "Without Parking");
        secondaryFilter2.setValue("All Parking");

        secondaryFilter3.setVisible(true);
        secondaryFilter3.getItems().clear();
        secondaryFilter3.getItems().addAll("All Sizes", "< 100m²", "100-500m²", "> 500m²");
        secondaryFilter3.setValue("All Sizes");
    }

    private void showResidentialFilters() {

        secondaryFilter1.setVisible(true);
        secondaryFilter1.getItems().clear();
        secondaryFilter1.getItems().addAll("All Bedrooms", "1 Bedroom", "2 Bedrooms", "3+ Bedrooms");
        secondaryFilter1.setValue("All Bedrooms");

        secondaryFilter2.setVisible(true);
        secondaryFilter2.getItems().clear();
        secondaryFilter2.getItems().addAll("All Properties", "With Garden", "Without Garden");
        secondaryFilter2.setValue("All Properties");

        secondaryFilter3.setVisible(true);
        secondaryFilter3.getItems().clear();
        secondaryFilter3.getItems().addAll("All Properties", "Pet Friendly", "No Pets");
        secondaryFilter3.setValue("All Properties");
    }

    private void hideSecondaryFilters() {
        secondaryFilter1.setVisible(false);
        secondaryFilter2.setVisible(false);
        secondaryFilter3.setVisible(false);
    }

    private void setupPropertyList() {
        propertyList.setFocusTraversable(false);
        propertyList.getStyleClass().add("property-list");

        propertyList.setMinHeight(500);  // Adjust this value as needed
        propertyList.setPrefHeight(Region.USE_COMPUTED_SIZE);
        propertyList.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(propertyList, Priority.ALWAYS);

        propertyList.setCellFactory(lv -> new PropertyListCell());
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
                        createActionButton("Edit", "edit-button", () -> handleEdit(property)),
                        createActionButton("Delete", "delete-button", () -> handleDelete(property))
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

    private String formatAddress(Address address) {
        return String.format("%s %s, %s, %s",
                address.getNumber(),
                address.getStreet(),
                address.getWard(),
                address.getDistrict(),
                address.getCity());
    }

    private MFXButton createActionButton(String text, String styleClass, Runnable action) {
        MFXButton button = new MFXButton(text);
        button.getStyleClass().add(styleClass);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> action.run());
        return button;
    }

    private void loadProperties() {
        loadingSpinner.show();
        try {
            if ("All Properties".equals(ownerFilterCombo.getValue())) {
                allProperties = propertyDao.getAllProperties();
            } else {
                long ownerId = UserSession.getInstance().getCurrentUser().getId();
                allProperties = propertyDao.getPropertiesByOwner(ownerId);
            }
            applyFiltersAndUpdateList();
        } catch (Exception e) {
            showError("Error loading properties: " + e.getMessage());
        } finally {
            loadingSpinner.hide();
        }
    }

    private void applyFiltersAndUpdateList() {
        List<Property> filteredProperties = allProperties.stream()
                .filter(p -> filterByType(p, propertyTypeFilterCombo.getValue()))
                .toList();

        int fromIndex = currentPage * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, filteredProperties.size());

        if (fromIndex < filteredProperties.size()) {
            List<Property> pageProperties = filteredProperties.subList(fromIndex, toIndex);
            propertyList.setItems(FXCollections.observableArrayList(pageProperties));
            // Scroll to top when page changes
            propertyList.scrollTo(0);
        }

        updatePaginationControls();
    }

    private boolean filterByType(Property p, String type) {
        if ("All Types".equals(type)) {
            return true;
        }

        if ("Commercial".equals(type) && p instanceof CommercialProperty) {
            return filterCommercialProperty((CommercialProperty) p);
        } else if ("Residential".equals(type) && p instanceof ResidentialProperty) {
            return filterResidentialProperty((ResidentialProperty) p);
        }
        return false;
    }

    private boolean filterCommercialProperty(CommercialProperty cp) {
        // Business Type Filter
        if (!"All Business Types".equals(secondaryFilter1.getValue())) {
            String selectedType = secondaryFilter1.getValue();
            if (!cp.getBusinessType().equals(selectedType)) {
                return false;
            }
        }

        // Parking Filter
        if (!"All Parking".equals(secondaryFilter2.getValue())) {
            boolean wantsParking = "With Parking".equals(secondaryFilter2.getValue());
            if (cp.isParkingSpace() != wantsParking) {
                return false;
            }
        }

        // Size Filter
        if (!"All Sizes".equals(secondaryFilter3.getValue())) {
            double size = cp.getSquareFootage();
            String sizeFilter = secondaryFilter3.getValue();
            if (sizeFilter.equals("< 100m²") && size >= 100) {
                return false;
            }
            if (sizeFilter.equals("100-500m²") && (size < 100 || size > 500)) {
                return false;
            }
            if (sizeFilter.equals("> 500m²") && size <= 500) {
                return false;
            }
        }

        return true;
    }

    private boolean filterResidentialProperty(ResidentialProperty rp) {
        // Bedrooms Filter
        if (!"All Bedrooms".equals(secondaryFilter1.getValue())) {
            int bedrooms = rp.getNumberOfBedrooms();
            String bedroomFilter = secondaryFilter1.getValue();
            if (bedroomFilter.equals("1 Bedroom") && bedrooms != 1) {
                return false;
            }
            if (bedroomFilter.equals("2 Bedrooms") && bedrooms != 2) {
                return false;
            }
            if (bedroomFilter.equals("3+ Bedrooms") && bedrooms < 3) {
                return false;
            }
        }

        // Garden Filter
        if (!"All Properties".equals(secondaryFilter2.getValue())) {
            boolean wantsGarden = "With Garden".equals(secondaryFilter2.getValue());
            if (rp.isGardenAvailability() != wantsGarden) {
                return false;
            }
        }

        // Pet Friendly Filter
        if (!"All Properties".equals(secondaryFilter3.getValue())) {
            boolean wantsPetFriendly = "Pet Friendly".equals(secondaryFilter3.getValue());
            if (rp.isPetFriendliness() != wantsPetFriendly) {
                return false;
            }
        }

        return true;
    }

    private void updatePaginationControls() {
        int totalPages = (int) Math.ceil((double) allProperties.size() / PAGE_SIZE);
        pageLabel.setText(String.format("Page %d of %d", currentPage + 1, totalPages));
        prevButton.setDisable(currentPage == 0);
        nextButton.setDisable((currentPage + 1) * PAGE_SIZE >= allProperties.size());
    }

    @FXML
    private void handlePrevPage() {
        if (currentPage > 0) {
            currentPage--;
            applyFiltersAndUpdateList();
        }
    }

    @FXML
    private void handleNextPage() {
        if ((currentPage + 1) * PAGE_SIZE < allProperties.size()) {
            currentPage++;
            applyFiltersAndUpdateList();
        }
    }

    private void refreshProperties() {
        loadingSpinner.show();
        currentPage = 0;

        // Use Platform.runLater to allow the UI to update before loading
        Platform.runLater(() -> {
            try {
                loadProperties();
            } finally {
                loadingSpinner.hide();
            }
        });
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

    private void handleEdit(Property property) {
        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        confirmation.setTitle("Edit Property");
        confirmation.setHeaderText("Are you sure you want to edit this property?");
        confirmation.setContentText("This will update all related information.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PropertyForm.fxml"));
                Scene scene = new Scene(loader.load());

                PropertyFormController controller = loader.getController();
                controller.setEditMode(property);

                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("Edit Property");
                stage.showAndWait();

                loadProperties();
            } catch (IOException e) {
                showError("Error opening edit form: " + e.getMessage());
            }
        }
    }

    private void handleDelete(Property property) {
        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Property");
        confirmation.setHeaderText("Are you sure you want to delete this property?");
        confirmation.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                propertyDao.deleteProperty(property);
                loadProperties();
                showSuccess("Property deleted successfully");
            } catch (Exception e) {
                showError("Error deleting property: " + e.getMessage());
            }
        }
    }

    @FXML
    public void handleAddProperty() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PropertyForm.fxml"));
            Scene scene = new Scene(loader.load());

            scene.getStylesheets().addAll(
                    getClass().getResource("/css/property-form.css").toExternalForm(),
                    getClass().getResource("/css/components/loading-spinner.css").toExternalForm(),
                    getClass().getResource("/css/components/toast.css").toExternalForm()
            );

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Add New Property");
            stage.setWidth(1280);
            stage.setHeight(720);
            stage.setMinWidth(800);
            stage.setMinHeight(600);

            stage.setOnHidden(e -> {
                loadingSpinner.show();
                Platform.runLater(() -> {
                    try {
                        loadProperties();
                    } finally {
                        loadingSpinner.hide();
                    }
                });
            });

            stage.show();
        } catch (IOException e) {
            showError("Error loading PropertyForm: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showSuccess(String message) {
        Toast.showSuccess((Stage) propertyList.getScene().getWindow(), message);
    }

    private void showError(String message) {
        Toast.showError((Stage) propertyList.getScene().getWindow(), message);
    }
}
