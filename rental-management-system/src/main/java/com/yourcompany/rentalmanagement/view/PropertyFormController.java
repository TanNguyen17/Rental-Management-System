package com.yourcompany.rentalmanagement.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

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

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class PropertyFormController {

    @FXML
    private TextField titleField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private ComboBox<String> propertyTypeCombo;
    @FXML
    private ComboBox<Property.propertyStatus> statusCombo;
    @FXML
    private TextField priceField;

    @FXML
    private TextField streetField;
    @FXML
    private TextField numberField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField stateField;

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

    private File selectedImage;
    private final CloudinaryService cloudinaryService;
    private final PropertyDao propertyDao;
    @FXML
    private ComboBox<Host> hostComboBox;
    private final HostDao hostDao;

    private LoadingSpinner loadingSpinner;

    private boolean isEditMode = false;
    private Property propertyToEdit;

    @FXML
    private Button submitButton;

    public PropertyFormController() {
        cloudinaryService = new CloudinaryService();
        propertyDao = new PropertyDaoImpl();
        hostDao = new HostDaoImpl();
    }

    @FXML
    public void initialize() {

        propertyTypeCombo.setItems(FXCollections.observableArrayList("Residential", "Commercial"));
        propertyTypeCombo.setPromptText("Select Property Type *");
        propertyTypeCombo.setOnAction(e -> handlePropertyTypeChange());

        statusCombo.setItems(FXCollections.observableArrayList(
                Property.propertyStatus.AVAILABLE,
                Property.propertyStatus.UNDER_MAINTENANCE
        ));
        statusCombo.setPromptText("Select Status *");

        priceField.textProperty().addListener((obs, old, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                priceField.setText(old);
            }
        });

        // host combo box
        loadHosts();

        statusCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == Property.propertyStatus.AVAILABLE && hostComboBox.getValue() == null) {
                showWarning("Warning: Without a host, tenants can only view the property");
            }
        });

        // loading spinner
        loadingSpinner = new LoadingSpinner();

        titleField.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getStylesheets().addAll(
                        getClass().getResource("/css/common.css").toExternalForm(),
                        getClass().getResource("/css/property-form.css").toExternalForm(),
                        getClass().getResource("/css/components/loading-spinner.css").toExternalForm()
                );

                VBox root = (VBox) titleField.getScene().getRoot();

                if (!root.getChildren().contains(loadingSpinner)) {
                    root.getChildren().add(loadingSpinner);

                    loadingSpinner.setManaged(false);
                    loadingSpinner.setMouseTransparent(false);

                    loadingSpinner.toFront();
                }
            }
        });

        propertyTypeCombo.getItems().addAll("Residential", "Commercial");
        propertyTypeCombo.setValue("Residential"); // Set default value

        propertyTypeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateFieldVisibility(newVal);
        });
        
        updateFieldVisibility(propertyTypeCombo.getValue());
    }

    private void handlePropertyTypeChange() {
        String type = propertyTypeCombo.getValue();
        commercialFields.setVisible("Commercial".equals(type));
        commercialFields.setManaged("Commercial".equals(type));
        residentialFields.setVisible("Residential".equals(type));
        residentialFields.setManaged("Residential".equals(type));
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

        try {
            VBox root = (VBox) titleField.getScene().getRoot();
            Platform.runLater(() -> {
                loadingSpinner.show(root);
                root.setDisable(true);
            });

            String imageUrl;
            if (selectedImage != null) {
                imageUrl = cloudinaryService.uploadImage(selectedImage);
            } else if (isEditMode) {
                imageUrl = propertyToEdit.getImageLink();
            } else {
                showError("Please select an image");
                return;
            }

            if (isEditMode) {
                updateProperty(imageUrl);
            } else {
                createNewProperty(imageUrl);
            }

            showSuccess(isEditMode ? "Property updated successfully!" : "Property created successfully!");
            clearForm();

            Platform.runLater(() -> {
                ((Stage) titleField.getScene().getWindow()).close();
            });

        } catch (Exception e) {
            Platform.runLater(() -> {
                showError("Error " + (isEditMode ? "updating" : "creating") + " property: " + e.getMessage());
            });
        } finally {
            VBox root = (VBox) titleField.getScene().getRoot();
            Platform.runLater(() -> {
                loadingSpinner.hide(root);
                root.setDisable(false);
            });
        }
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
        address.setCity(cityField.getText());
        address.setState(stateField.getText());
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
                || cityField.getText().isEmpty()
                || stateField.getText().isEmpty()
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

        return true;
    }

    private void showSuccess(String message) {
        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText(message);
    }

    private void showError(String message) {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText(message);
    }

    private void clearForm() {
        titleField.clear();
        descriptionField.clear();
        propertyTypeCombo.setValue(null);
        statusCombo.setValue(null);
        priceField.clear();
        streetField.clear();
        numberField.clear();
        cityField.clear();
        stateField.clear();
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
            List<Host> hosts = hostDao.getAllHosts();
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

        // Set property type and trigger visibility update
        if (property instanceof ResidentialProperty) {
            propertyTypeCombo.setValue("Residential");
            ResidentialProperty rp = (ResidentialProperty) property;
            // ... set residential fields ...
        } else if (property instanceof CommercialProperty) {
            propertyTypeCombo.setValue("Commercial");
            CommercialProperty cp = (CommercialProperty) property;
            // ... set commercial fields ...
        }

        // Force update field visibility
        updateFieldVisibility(propertyTypeCombo.getValue());

        // Populate form fields
        titleField.setText(property.getTitle());
        descriptionField.setText(property.getDescription());
        priceField.setText(String.valueOf(property.getPrice()));
        statusCombo.setValue(property.getStatus());

        // Set address fields
        Address address = property.getAddress();
        if (address != null) {
            streetField.setText(address.getStreet());
            numberField.setText(address.getNumber());
            cityField.setText(address.getCity());
            stateField.setText(address.getState());
        }

        if (!property.getHosts().isEmpty()) {
            hostComboBox.setValue(property.getHosts().get(0));
        }

        try {
            propertyImageView.setImage(new Image(property.getImageLink()));
        } catch (Exception e) {
            showError("Error loading property image");
        }

        submitButton.setText("Update Property");
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

    // ... validation and helper methods ...
}
