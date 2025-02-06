package com.yourcompany.rentalmanagement.view;
/**
 * @author FTech
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.hibernate.Session;

import com.yourcompany.rentalmanagement.dao.HostDao;
import com.yourcompany.rentalmanagement.dao.PropertyDao;
import com.yourcompany.rentalmanagement.dao.impl.HostDaoImpl;
import com.yourcompany.rentalmanagement.dao.impl.PropertyDaoImpl;
import com.yourcompany.rentalmanagement.model.Address;
import com.yourcompany.rentalmanagement.model.CommercialProperty;
import com.yourcompany.rentalmanagement.model.Host;
import com.yourcompany.rentalmanagement.model.Owner;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.ResidentialProperty;
import com.yourcompany.rentalmanagement.util.CloudinaryService;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import com.yourcompany.rentalmanagement.util.UserSession;
import com.yourcompany.rentalmanagement.view.components.LoadingSpinner;
import com.yourcompany.rentalmanagement.view.components.Toast;
import com.yourcompany.rentalmanagement.util.AddressData;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.event.ActionEvent;

public class PropertyFormController {
    private HostDaoImpl hostDaoImpl;
    private LoadingSpinner loadingSpinner;
    private boolean isEditMode = false;
    private Property propertyToEdit;
    private File selectedImage;
    private final CloudinaryService cloudinaryService;
    private final PropertyDao propertyDao;

    @FXML
    private TextField titleField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private ComboBox<String> propertyTypeCombo;
    @FXML
    private ComboBox<Property.PropertyStatus> statusCombo;
    @FXML
    private TextField priceField;

    @FXML
    private VBox commercialFields;
    @FXML
    private TextField businessTypeField;
    @FXML
    private CheckBox parkingSpaceCheck;
    @FXML
    private TextField squareFootageField;

    @FXML
    private VBox residentialFields;
    @FXML
    private TextField bedroomsField;
    @FXML
    private CheckBox gardenCheck;
    @FXML
    private CheckBox petFriendlyCheck;

    @FXML
    private ImageView propertyImageView;
    @FXML
    private Label messageLabel;

    @FXML
    private ComboBox<Host> hostComboBox;

    @FXML
    private Button submitButton;

    @FXML
    private ComboBox<String> provinceCombo;
    @FXML
    private ComboBox<String> districtCombo;
    @FXML
    private ComboBox<String> wardCombo;

    @FXML
    private TextField streetField;
    @FXML
    private TextField numberField;

    public PropertyFormController() {
        cloudinaryService = new CloudinaryService();
        propertyDao = new PropertyDaoImpl();
        hostDaoImpl = new HostDaoImpl();
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            if (titleField.getScene() != null) {
                titleField.getScene().getStylesheets().add(
                        getClass().getResource("/css/property-form.css").toExternalForm()
                );
            }
        });

        setupFilters();
        setupLoadingSpinner();

        priceField.textProperty().addListener((obs, old, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                priceField.setText(old);
            }
        });

        // host combo box
        loadHosts();

        statusCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == Property.PropertyStatus.AVAILABLE && hostComboBox.getValue() == null) {
                showWarning("Warning: Without a host, tenants can only view the property");
            }
        });

        // loading spinner
        setupLoadingSpinner();

        propertyTypeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateFieldVisibility(newVal);
        });

        updateFieldVisibility(propertyTypeCombo.getValue());
        bedroomsField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                bedroomsField.setText(oldValue);
            }
        });

        // Set window size
        Platform.runLater(() -> {
            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.setWidth(1280);
            stage.setHeight(720);
            stage.setMinWidth(800);
            stage.setMinHeight(600);
        });

        initializeAddressFields();
    }

    private void setupFilters() {
        // Clear and set property types
        propertyTypeCombo.getItems().clear();
        propertyTypeCombo.getItems().addAll("Residential", "Commercial");
        propertyTypeCombo.setValue("Residential");
        propertyTypeCombo.setOnAction(e -> handlePropertyTypeChange());

        // Status setup
        statusCombo.getItems().clear();
        statusCombo.getItems().addAll(
                Property.PropertyStatus.AVAILABLE,
                Property.PropertyStatus.UNDER_MAINTENANCE
        );
        statusCombo.setValue(Property.PropertyStatus.AVAILABLE);

        if (isEditMode && propertyToEdit != null) {
            String propertyType = propertyToEdit instanceof ResidentialProperty ? "Residential" : "Commercial";
            propertyTypeCombo.setValue(propertyType);

            if (propertyToEdit.getStatus() == Property.PropertyStatus.RENTED) {
                statusCombo.setDisable(true);
                statusCombo.setValue(Property.PropertyStatus.RENTED);
            } else {
                statusCombo.setValue(propertyToEdit.getStatus());
            }
        }

        handlePropertyTypeChange();
    }

    private void handlePropertyTypeChange() {
        String type = propertyTypeCombo.getValue();

        commercialFields.setVisible(false);
        commercialFields.setManaged(false);
        residentialFields.setVisible(false);
        residentialFields.setManaged(false);

        if ("Commercial".equals(type)) {
            commercialFields.setVisible(true);
            commercialFields.setManaged(true);
            bedroomsField.clear();
            gardenCheck.setSelected(false);
            petFriendlyCheck.setSelected(false);
        } else if ("Residential".equals(type)) {
            residentialFields.setVisible(true);
            residentialFields.setManaged(true);
            businessTypeField.clear();
            parkingSpaceCheck.setSelected(false);
            squareFootageField.clear();
        }
    }

    @FXML
    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        selectedImage = fileChooser.showOpenDialog(null);
        if (selectedImage != null) {
            try {
                Image image = new Image(new FileInputStream(selectedImage));
                propertyImageView.setImage(image);
            } catch (FileNotFoundException e) {
                showError("Error loading image: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSubmit() {
        if (!validateForm()) {
            return;
        }

        loadingSpinner.show();

        // Upload image in background
        CompletableFuture.supplyAsync(() -> {
            try {
                if (selectedImage != null) {
                    return cloudinaryService.uploadImage(selectedImage);
                } else if (isEditMode) {
                    return propertyToEdit.getImageLink();
                }
                throw new IllegalStateException("Please select an image");
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }).thenAcceptAsync(imageUrl -> {
            try {
                if (isEditMode) {
                    updateProperty(imageUrl);
                } else {
                    createNewProperty(imageUrl);
                }

                Platform.runLater(() -> {
                    showSuccess(isEditMode ? "Property updated successfully!" : "Property created successfully!");
                    ((Stage) titleField.getScene().getWindow()).close();
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError("Error: " + e.getMessage()));
            } finally {
                Platform.runLater(() -> loadingSpinner.hide());
            }
        }, Platform::runLater).exceptionally(throwable -> {
            Platform.runLater(() -> {
                showError(throwable.getMessage());
                loadingSpinner.hide();
            });
            return null;
        });
    }

    private void createCommercialProperty(String imageUrl) {
        CommercialProperty property = new CommercialProperty();
        setCommonProperties(property, imageUrl);

        property.setBusinessType(businessTypeField.getText());
        property.setParkingSpace(parkingSpaceCheck.isSelected());
        property.setSquareFootage(Double.parseDouble(squareFootageField.getText()));

        propertyDao.createCommercialProperty(property);
    }

    private void createResidentialProperty(String imageUrl) {
        ResidentialProperty property = new ResidentialProperty();
        setCommonProperties(property, imageUrl);

        property.setNumberOfBedrooms(Integer.parseInt(bedroomsField.getText()));
        property.setGardenAvailability(gardenCheck.isSelected());
        property.setPetFriendliness(petFriendlyCheck.isSelected());

        propertyDao.createResidentialProperty(property);
    }

    private void setCommonProperties(Property property, String imageUrl) {
        property.setTitle(titleField.getText());
        property.setDescription(descriptionField.getText());
        property.setPrice(Double.parseDouble(priceField.getText()));
        property.setStatus(statusCombo.getValue());
        property.setImageLink(imageUrl);

        Address address = new Address();
        address.setStreet(streetField.getText());
        address.setNumber(numberField.getText());
        address.setWard(wardCombo.getValue());
        address.setDistrict(districtCombo.getValue());
        address.setCity(provinceCombo.getValue());
        property.setAddress(address);

        if (isEditMode) {
            property.setOwner(propertyToEdit.getOwner());
        } else {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Owner owner = session.get(Owner.class, UserSession.getInstance().getCurrentUser().getId());
                property.setOwner(owner);
            }
        }

        // Store selected host ID for DAO to handle
        Host selectedHost = hostComboBox.getValue();
        if (selectedHost != null) {
            property.setHostId(selectedHost.getId());
        }
    }

    private boolean validateForm() {
        if (titleField.getText().isEmpty()
                || priceField.getText().isEmpty()
                || propertyTypeCombo.getValue() == null
                || statusCombo.getValue() == null
                || streetField.getText().isEmpty()
                || numberField.getText().isEmpty()
                || provinceCombo.getValue() == null
                || districtCombo.getValue() == null
                || wardCombo.getValue() == null
                || (!isEditMode && selectedImage == null)) {
            showError("Please fill in all required fields (marked with *)");
            return false;
        }

        if ("Commercial".equals(propertyTypeCombo.getValue())) {
            if (businessTypeField.getText().isEmpty() || squareFootageField.getText().isEmpty()) {
                showError("Please fill in all commercial property fields");
                return false;
            }
        } else if ("Residential".equals(propertyTypeCombo.getValue())) {
            if (bedroomsField.getText().isEmpty()) {
                showError("Please fill in all residential property fields");
                return false;
            }
        }

        if (provinceCombo.getValue() == null || provinceCombo.getValue().isEmpty()) {
            showError("Please select a province/city");
            return false;
        }
        if (districtCombo.getValue() == null || districtCombo.getValue().isEmpty()) {
            showError("Please select a district");
            return false;
        }
        if (wardCombo.getValue() == null || wardCombo.getValue().isEmpty()) {
            showError("Please select a ward");
            return false;
        }

        return true;
    }

    private void showSuccess(String message) {
        Toast.showSuccess((Stage) titleField.getScene().getWindow(), message);
    }

    private void showError(String message) {
        Toast.showError((Stage) titleField.getScene().getWindow(), message);
    }

    private void clearForm() {
        titleField.clear();
        descriptionField.clear();
        propertyTypeCombo.setValue(null);
        statusCombo.setValue(null);
        priceField.clear();
        streetField.clear();
        numberField.clear();
        provinceCombo.setValue(null);
        districtCombo.setValue(null);
        wardCombo.setValue(null);
        districtCombo.setDisable(true);
        wardCombo.setDisable(true);
        businessTypeField.clear();
        parkingSpaceCheck.setSelected(false);
        squareFootageField.clear();
        bedroomsField.clear();
        gardenCheck.setSelected(false);
        petFriendlyCheck.setSelected(false);
        selectedImage = null;
        propertyImageView.setImage(null);
        messageLabel.setText("");
        hostComboBox.setValue(null);
    }

    private void loadHosts() {
        try {
            List<Host> hosts = hostDaoImpl.loadAll();
            hostComboBox.getItems().addAll(hosts);
            hostComboBox.setCellFactory(param -> new ListCell<Host>() {
                @Override
                protected void updateItem(Host host, boolean empty) {
                    super.updateItem(host, empty);
                    if (empty || host == null) {
                        setText(null);
                    } else {
                        setText(host.getUsername());
                    }
                }
            });

            hostComboBox.setConverter(new StringConverter<Host>() {
                @Override
                public String toString(Host host) {
                    return host == null ? null : host.getUsername();
                }

                @Override
                public Host fromString(String string) {
                    return null; // Not needed for this use case
                }
            });
        } catch (Exception e) {
            System.err.println("Error loading hosts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showWarning(String message) {
        messageLabel.setStyle("-fx-text-fill: orange;");
        messageLabel.setText(message);
    }

    public void setEditMode(Property property) {
        isEditMode = true;
        propertyToEdit = property;
        submitButton.setText("Update Property");

        // Set common fields
        titleField.setText(property.getTitle());
        descriptionField.setText(property.getDescription());
        priceField.setText(String.valueOf(property.getPrice()));

        // Set address fields
        Address address = property.getAddress();
        if (address != null) {
            streetField.setText(address.getStreet());
            numberField.setText(address.getNumber());
            provinceCombo.setValue(address.getCity());
            Platform.runLater(() -> {
                provinceCombo.fireEvent(new ActionEvent());
                districtCombo.setValue(address.getDistrict());
                districtCombo.fireEvent(new ActionEvent());
                wardCombo.setValue(address.getWard());
            });
        }

        // Set host if exists
        if (!property.getHosts().isEmpty()) {
            hostComboBox.setValue(property.getHosts().get(0));
        }

        // Load image
        try {
            propertyImageView.setImage(new Image(property.getImageLink()));
        } catch (Exception e) {
            showError("Error loading property image");
        }

        // Set type-specific fields
        if (property instanceof ResidentialProperty) {
            ResidentialProperty rp = (ResidentialProperty) property;
            propertyTypeCombo.setValue("Residential");
            bedroomsField.setText(String.valueOf(rp.getNumberOfBedrooms()));
            gardenCheck.setSelected(rp.isGardenAvailability());
            petFriendlyCheck.setSelected(rp.isPetFriendliness());
        } else if (property instanceof CommercialProperty) {
            CommercialProperty cp = (CommercialProperty) property;
            propertyTypeCombo.setValue("Commercial");
            businessTypeField.setText(cp.getBusinessType());
            parkingSpaceCheck.setSelected(cp.isParkingSpace());
            squareFootageField.setText(String.valueOf(cp.getSquareFootage()));
        }

        setupFilters(); // This will handle the status combo setup
    }

    private void updateProperty(String imageUrl) {
        if (propertyToEdit instanceof ResidentialProperty) {
            ResidentialProperty property = (ResidentialProperty) propertyToEdit;
            setCommonProperties(property, imageUrl);
            property.setNumberOfBedrooms(Integer.parseInt(bedroomsField.getText()));
            property.setGardenAvailability(gardenCheck.isSelected());
            property.setPetFriendliness(petFriendlyCheck.isSelected());
            propertyDao.updateProperty(property);
        } else if (propertyToEdit instanceof CommercialProperty) {
            CommercialProperty property = (CommercialProperty) propertyToEdit;
            setCommonProperties(property, imageUrl);
            property.setBusinessType(businessTypeField.getText());
            property.setParkingSpace(parkingSpaceCheck.isSelected());
            property.setSquareFootage(Double.parseDouble(squareFootageField.getText()));
            propertyDao.updateProperty(property);
        }
    }

    private void createNewProperty(String imageUrl) {
        if ("Commercial".equals(propertyTypeCombo.getValue())) {
            createCommercialProperty(imageUrl);
        } else {
            createResidentialProperty(imageUrl);
        }
    }

    private void updateFieldVisibility(String propertyType) {
        boolean isResidential = "Residential".equals(propertyType);

        // Residential fields
        bedroomsField.setVisible(isResidential);
        bedroomsField.setManaged(isResidential);
        gardenCheck.setVisible(isResidential);
        gardenCheck.setManaged(isResidential);
        petFriendlyCheck.setVisible(isResidential);
        petFriendlyCheck.setManaged(isResidential);

        // Commercial fields
        businessTypeField.setVisible(!isResidential);
        businessTypeField.setManaged(!isResidential);
        parkingSpaceCheck.setVisible(!isResidential);
        parkingSpaceCheck.setManaged(!isResidential);
        squareFootageField.setVisible(!isResidential);
        squareFootageField.setManaged(!isResidential);
    }

    private void setupLoadingSpinner() {
        loadingSpinner = new LoadingSpinner();

        Platform.runLater(() -> {
            if (titleField.getScene() != null) {
                // Get the root ScrollPane
                ScrollPane scrollPane = (ScrollPane) titleField.getScene().getRoot();
                Node originalContent = scrollPane.getContent();

                // Create a container for the spinner and content
                StackPane container = new StackPane();
                container.getStyleClass().add("form-container");

                // Add the original content and spinner to the container
                container.getChildren().addAll(originalContent, loadingSpinner);

                // Configure spinner
                loadingSpinner.setVisible(false);
                loadingSpinner.setMouseTransparent(true);
                loadingSpinner.toFront();

                // Set size bindings
                loadingSpinner.prefWidthProperty().bind(container.widthProperty());
                loadingSpinner.prefHeightProperty().bind(container.heightProperty());

                // Set the container as the ScrollPane's content
                scrollPane.setContent(container);

                // Add method to show/hide spinner
                loadingSpinner.show();
                loadingSpinner.hide();
            }
        });
    }

    private void initializeAddressFields() {
        // Initialize ComboBoxes
        provinceCombo.setItems(FXCollections.observableArrayList(AddressData.provinceCities.keySet()));

        // Add listeners for cascading selection
        provinceCombo.setOnAction(e -> {
            String selectedProvince = provinceCombo.getValue();
            if (selectedProvince != null) {
                List<String> districts = AddressData.provinceCities.get(selectedProvince);
                districtCombo.setItems(FXCollections.observableArrayList(districts));
                districtCombo.setDisable(false);
                // Clear subsequent selections
                districtCombo.setValue(null);
                wardCombo.setValue(null);
                wardCombo.setDisable(true);
            }
        });

        districtCombo.setOnAction(e -> {
            String selectedDistrict = districtCombo.getValue();
            if (selectedDistrict != null) {
                List<String> wards = AddressData.cityWards.get(selectedDistrict);
                wardCombo.setItems(FXCollections.observableArrayList(wards));
                wardCombo.setDisable(false);
            }
        });

        // Initially disable district and ward selection
        districtCombo.setDisable(true);
        wardCombo.setDisable(true);
    }

    private Address createAddress() {
        Address address = new Address();
        address.setStreet(streetField.getText());
        address.setNumber(numberField.getText());
        address.setWard(wardCombo.getValue());
        address.setDistrict(districtCombo.getValue());
        address.setCity(provinceCombo.getValue());
        return address;
    }

    // ... validation and helper methods ...
}
