package com.yourcompany.rentalmanagement.view;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.yourcompany.rentalmanagement.dao.PropertyDao;
import com.yourcompany.rentalmanagement.dao.impl.PropertyDaoImpl;
import com.yourcompany.rentalmanagement.model.CommercialProperty;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.ResidentialProperty;
import com.yourcompany.rentalmanagement.util.UserSession;
import com.yourcompany.rentalmanagement.view.components.LoadingSpinner;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
    private Button addPropertyButton;

    @FXML
    private VBox propertyListContainer;

    @FXML
    private Label messageLabel;

    @FXML
    private ComboBox<String> ownerFilterCombo;
    @FXML
    private ComboBox<String> propertyTypeFilterCombo;

    private List<Property> allProperties;
    private final PropertyDao propertyDao;
    private LoadingSpinner loadingSpinner;
    private static final int PAGE_SIZE = 10;
    private int currentPage = 0;

    public ViewRentalPropertiesController() {
        propertyDao = new PropertyDaoImpl();
    }

    @FXML
    public void initialize() {
        try {
            System.out.println("ViewRentalPropertiesController initialized");

            // Initialize filters first
            initializeFilters();

            // Initialize loading spinner after scene is available
            propertyListContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    Platform.runLater(() -> {
                        try {
                            initializeLoadingSpinner(newScene);
                            loadMyProperties(); // Load properties after spinner is ready
                        } catch (Exception e) {
                            System.err.println("Error initializing UI: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                }
            });

        } catch (Exception e) {
            System.err.println("Error in initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeFilters() {
        ownerFilterCombo.getItems().addAll("My Properties", "All Properties");
        ownerFilterCombo.setValue("My Properties");

        propertyTypeFilterCombo.getItems().addAll("All Types", "Commercial", "Residential");
        propertyTypeFilterCombo.setValue("All Types");
        propertyTypeFilterCombo.setDisable(true);

        ownerFilterCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("All Properties".equals(newVal)) {
                propertyTypeFilterCombo.setDisable(false);
                loadAllProperties();
            } else {
                propertyTypeFilterCombo.setValue("All Types");
                propertyTypeFilterCombo.setDisable(true);
                loadMyProperties();
            }
        });

        propertyTypeFilterCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });
    }

    private void initializeLoadingSpinner(Scene scene) {
        scene.getStylesheets().addAll(
                getClass().getResource("/css/common.css").toExternalForm(),
                getClass().getResource("/css/property-list.css").toExternalForm(),
                getClass().getResource("/css/components/loading-spinner.css").toExternalForm()
        );

        loadingSpinner = new LoadingSpinner();
        VBox root = (VBox) scene.getRoot();
        if (!root.getChildren().contains(loadingSpinner)) {
            root.getChildren().add(loadingSpinner);
            loadingSpinner.setManaged(false);
            loadingSpinner.setVisible(false);
            loadingSpinner.setMouseTransparent(true);
        }
    }

    private void showLoadingSpinner() {
        if (loadingSpinner != null && propertyListContainer.getScene() != null) {
            Platform.runLater(() -> {
                VBox root = (VBox) propertyListContainer.getScene().getRoot();
                loadingSpinner.show(root);
                root.setDisable(true);
            });
        }
    }

    private void hideLoadingSpinner() {
        if (loadingSpinner != null && propertyListContainer.getScene() != null) {
            Platform.runLater(() -> {
                VBox root = (VBox) propertyListContainer.getScene().getRoot();
                loadingSpinner.hide(root);
                root.setDisable(false);
            });
        }
    }

    private void loadAllProperties() {
        showLoadingSpinner();
        try {
            // Load properties in batches
            Platform.runLater(() -> {
                try {
                    // First load just the first page
                    allProperties = propertyDao.getAllPropertiesPaginated(currentPage, PAGE_SIZE);
                    applyFilters();
                    
                    // Then load the rest in background
                    Thread backgroundLoader = new Thread(() -> {
                        try {
                            List<Property> remainingProperties = propertyDao.getAllPropertiesAfterPage(currentPage, PAGE_SIZE);
                            Platform.runLater(() -> {
                                allProperties.addAll(remainingProperties);
                                applyFilters();
                            });
                        } catch (Exception e) {
                            Platform.runLater(() -> 
                                showError("Error loading additional properties: " + e.getMessage())
                            );
                        }
                    });
                    backgroundLoader.setDaemon(true);
                    backgroundLoader.start();
                } catch (Exception e) {
                    showError("Error loading properties: " + e.getMessage());
                } finally {
                    hideLoadingSpinner();
                }
            });
        } catch (Exception e) {
            System.err.println("Error in loadAllProperties: " + e.getMessage());
            e.printStackTrace();
            Platform.runLater(() -> showError("Error loading properties: " + e.getMessage()));
            hideLoadingSpinner();
        }
    }

    private void loadMyProperties() {
        showLoadingSpinner();
        try {
            long ownerId = UserSession.getInstance().getCurrentUser().getId();
            allProperties = propertyDao.getPropertiesByOwner(ownerId);
            applyFilters();
        } catch (Exception e) {
            System.err.println("Error loading my properties: " + e.getMessage());
            e.printStackTrace();
            showError("Error loading properties: " + e.getMessage());
        } finally {
            hideLoadingSpinner();
        }
    }

    private void applyFilters() {
        propertyListContainer.getChildren().clear();

        if (allProperties == null) {
            return;
        }

        List<Property> filteredProperties = allProperties.stream()
                .filter(property -> {
                    String typeFilter = propertyTypeFilterCombo.getValue();
                    if ("Commercial".equals(typeFilter)) {
                        return property instanceof CommercialProperty;
                    } else if ("Residential".equals(typeFilter)) {
                        return property instanceof ResidentialProperty;
                    }
                    return true; // "All Types" selected
                })
                .toList();

        for (Property property : filteredProperties) {
            createPropertyCard(property);
        }

        if (filteredProperties.isEmpty()) {
            Label noPropertiesLabel = new Label("No properties found");
            noPropertiesLabel.getStyleClass().add("no-properties-label");
            propertyListContainer.getChildren().add(noPropertiesLabel);
        }
    }

    private void loadProperties() {
        if ("All Properties".equals(ownerFilterCombo.getValue())) {
            loadAllProperties();
        } else {
            loadMyProperties();
        }
    }

    private void createPropertyCard(Property property) {
        HBox card = new HBox(10);
        card.getStyleClass().add("property-card");

        // Add click handler for the entire card
        card.setOnMouseClicked(e -> {
            Node source = (Node) e.getTarget();
            while (source != null && !(source instanceof MFXButton) && !(source instanceof HBox)) {
                source = source.getParent();
            }
            if (source instanceof MFXButton) {
                return;
            }
            showPropertyDetails(property);
        });

        ImageView imageView = new ImageView(new Image(property.getImageLink()));
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.getStyleClass().add("property-image");

        VBox info = new VBox(5);
        info.getStyleClass().add("property-info");

        Label titleLabel = new Label(property.getTitle());
        titleLabel.getStyleClass().add("property-title");

        Label descriptionLabel = new Label(property.getDescription());
        descriptionLabel.getStyleClass().add("property-description");

        Label priceLabel = new Label(String.format("$%.2f", property.getPrice()));
        priceLabel.getStyleClass().add("property-price");

        FlowPane features = new FlowPane(5, 5);
        features.getStyleClass().add("features-container");

        if (property.getHosts() != null && !property.getHosts().isEmpty()) {
            Label hostLabel = new Label("Hosted by " + property.getHosts().get(0).getUsername());
            hostLabel.getStyleClass().add("feature-label");
            features.getChildren().add(hostLabel);
        }

        Label addressLabel = new Label(property.getAddress().getCity() + ", " + property.getAddress().getState());
        addressLabel.getStyleClass().add("feature-label");
        features.getChildren().add(addressLabel);

        if (property instanceof ResidentialProperty) {
            ResidentialProperty rp = (ResidentialProperty) property;
            Label bedsLabel = new Label(rp.getNumberOfBedrooms() + " bed");
            bedsLabel.getStyleClass().add("feature-label");
            features.getChildren().add(bedsLabel);

            if (rp.isGardenAvailability()) {
                Label gardenLabel = new Label("Has Garden");
                gardenLabel.getStyleClass().add("feature-label");
                features.getChildren().add(gardenLabel);
            }

            if (rp.isPetFriendliness()) {
                Label petLabel = new Label("Pet Friendly");
                petLabel.getStyleClass().add("feature-label");
                features.getChildren().add(petLabel);
            }
        }

        info.getChildren().addAll(titleLabel, descriptionLabel, priceLabel, features);

        Label statusLabel = new Label(property.getStatus().toString());
        statusLabel.getStyleClass().addAll("status-label", property.getStatus().toString().toLowerCase());

        VBox buttons = new VBox(5);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.getStyleClass().add("buttons-container");

        MFXButton editButton = new MFXButton("Edit");
        editButton.getStyleClass().add("edit-button");
        editButton.setOnAction(e -> handleEdit(property));

        MFXButton deleteButton = new MFXButton("Delete");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(e -> handleDelete(property));

        buttons.getChildren().addAll(editButton, deleteButton);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(imageView, info, spacer, statusLabel, buttons);
        propertyListContainer.getChildren().add(card);
    }

    private void showPropertyDetails(Property property) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PropertyDetails.fxml"));
            Scene scene = new Scene(loader.load());

            // Add CSS
            scene.getStylesheets().add(getClass().getResource("/css/property-list.css").toExternalForm());

            PropertyDetailsController controller = loader.getController();
            controller.setProperty(property);

            Stage stage = new Stage();
            stage.setTitle("Property Details - " + property.getTitle());
            stage.setScene(scene);

            // Make it modal
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
            System.out.println("Add Property button clicked");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PropertyForm.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Add New Property");

            stage.setOnHidden(e -> loadProperties());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading PropertyForm: " + e.getMessage());
        }
    }

    private void showSuccess(String message) {
        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText(message);
    }

    private void showError(String message) {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText(message);
    }
}
