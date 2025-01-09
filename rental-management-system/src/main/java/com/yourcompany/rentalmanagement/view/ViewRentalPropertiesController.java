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

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.scene.layout.HBox;
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

    public ViewRentalPropertiesController() {
        this.propertyDao = new PropertyDaoImpl();
    }

    @FXML
    public void initialize() {
        setupFilters();
        setupPropertyList();
        loadProperties();
        updatePaginationControls();
    }

    private void setupFilters() {
        ownerFilterCombo.getItems().addAll("My Properties", "All Properties");
        ownerFilterCombo.setValue("My Properties");

        propertyTypeFilterCombo.getItems().addAll("All Types", "Commercial", "Residential");
        propertyTypeFilterCombo.setValue("All Types");

        ownerFilterCombo.setOnAction(e -> refreshProperties());
        propertyTypeFilterCombo.setOnAction(e -> refreshProperties());
    }

    private void setupPropertyList() {
        propertyList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Property property, boolean empty) {
                super.updateItem(property, empty);
                if (empty || property == null) {
                    setGraphic(null);
                } else {
                    HBox card = createPropertyCard(property);
                    setGraphic(card);
                }
            }
        });
    }

    private HBox createPropertyCard(Property property) {
        HBox card = new HBox(10);
        card.getStyleClass().add("property-card");

        // Image loading
        ImageView imageView = new ImageView();
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.getStyleClass().add("property-image");

        new Thread(() -> {
            try {
                Image image = new Image(property.getImageLink(), true);
                Platform.runLater(() -> imageView.setImage(image));
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
            }
        }).start();

        VBox info = new VBox(5);
        info.getStyleClass().add("property-info");

        Label titleLabel = new Label(property.getTitle());
        titleLabel.getStyleClass().add("property-title");

        Label priceLabel = new Label(String.format("$%.2f per month", property.getPrice()));
        priceLabel.getStyleClass().add("property-price");

        Label statusLabel = new Label(property.getStatus().toString());
        statusLabel.getStyleClass().addAll("status-label", property.getStatus().toString().toLowerCase());

        // Buttons
        HBox buttons = new HBox(5);
        buttons.getStyleClass().add("button-container");

        MFXButton viewButton = new MFXButton("View Details");
        viewButton.setOnAction(e -> showPropertyDetails(property));

        MFXButton editButton = new MFXButton("Edit");
        editButton.setOnAction(e -> handleEdit(property));

        MFXButton deleteButton = new MFXButton("Delete");
        deleteButton.setOnAction(e -> handleDelete(property));

        buttons.getChildren().addAll(viewButton, editButton, deleteButton);

        info.getChildren().addAll(titleLabel, priceLabel, statusLabel);
        card.getChildren().addAll(imageView, info, buttons);

        return card;
    }

    private void loadProperties() {
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
        }

        updatePaginationControls();
    }

    private boolean filterByType(Property p, String type) {
        return "All Types".equals(type)
                || ("Residential".equals(type) && p instanceof ResidentialProperty)
                || ("Commercial".equals(type) && p instanceof CommercialProperty);
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
        currentPage = 0;
        loadProperties();
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
