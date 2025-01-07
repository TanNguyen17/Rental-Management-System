package com.yourcompany.rentalmanagement.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.hibernate.Session;

import com.yourcompany.rentalmanagement.dao.PropertyDao;
import com.yourcompany.rentalmanagement.dao.impl.PropertyDaoImpl;
import com.yourcompany.rentalmanagement.model.Address;
import com.yourcompany.rentalmanagement.model.CommercialProperty;
import com.yourcompany.rentalmanagement.model.Owner;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.ResidentialProperty;
import com.yourcompany.rentalmanagement.util.CloudinaryService;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import com.yourcompany.rentalmanagement.util.UserSession;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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

    // Address fields
    @FXML
    private TextField streetField;
    @FXML
    private TextField numberField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField stateField;

    // Commercial fields
    @FXML
    private VBox commercialFields;
    @FXML
    private TextField businessTypeField;
    @FXML
    private CheckBox parkingSpaceCheck;
    @FXML
    private TextField squareFootageField;

    // Residential fields
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

    public PropertyFormController() {
        cloudinaryService = new CloudinaryService();
        propertyDao = new PropertyDaoImpl();
    }

    @FXML
    public void initialize() {
        // Initialize property type combo
        propertyTypeCombo.getItems().addAll("Residential", "Commercial");
        propertyTypeCombo.setOnAction(e -> handlePropertyTypeChange());

        // Initialize status combo
        statusCombo.getItems().addAll(
                Property.propertyStatus.AVAILABLE,
                Property.propertyStatus.UNDER_MAINTENANCE
        );

        // Add number-only validation to price field
        priceField.textProperty().addListener((obs, old, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                priceField.setText(old);
            }
        });
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
            System.out.println("Form validation failed");
            return;
        }

        try {
            System.out.println("Attempting to create property...");
            // Upload image first
            String imageUrl = cloudinaryService.uploadImage(selectedImage);
            System.out.println("Image uploaded successfully: " + imageUrl);

            // Create property based on type
            if ("Commercial".equals(propertyTypeCombo.getValue())) {
                System.out.println("Creating commercial property...");
                createCommercialProperty(imageUrl);
            } else {
                System.out.println("Creating residential property...");
                createResidentialProperty(imageUrl);
            }

            showSuccess("Property created successfully!");
            System.out.println("Property created successfully");
            clearForm();

            // Close the form window after successful creation
            ((Stage) titleField.getScene().getWindow()).close();
        } catch (Exception e) {
            System.err.println("Error creating property: " + e.getMessage());
            e.printStackTrace();
            showError("Error creating property: " + e.getMessage());
        }
    }

    private void createCommercialProperty(String imageUrl) {
        CommercialProperty property = new CommercialProperty();
        // Set common properties
        setCommonProperties(property, imageUrl);

        // Set commercial-specific properties
        property.setBusinessType(businessTypeField.getText());
        property.setParkingSpace(parkingSpaceCheck.isSelected());
        property.setSquareFootage(Double.parseDouble(squareFootageField.getText()));

        propertyDao.createCommercialProperty(property);
    }

    private void createResidentialProperty(String imageUrl) {
        ResidentialProperty property = new ResidentialProperty();
        // Set common properties
        setCommonProperties(property, imageUrl);

        // Set residential-specific properties
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

        // Set address
        Address address = new Address();
        address.setStreet(streetField.getText());
        address.setNumber(numberField.getText());
        address.setCity(cityField.getText());
        address.setState(stateField.getText());
        property.setAddress(address);

        // Set owner from current user session
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            long ownerId = UserSession.getInstance().getCurrentUser().getId();
            Owner owner = session.get(Owner.class, ownerId);
            property.setOwner(owner);
        }
    }

    private boolean validateForm() {
        // Basic validation
        if (titleField.getText().isEmpty()
                || priceField.getText().isEmpty()
                || propertyTypeCombo.getValue() == null
                || statusCombo.getValue() == null
                || streetField.getText().isEmpty()
                || numberField.getText().isEmpty()
                || cityField.getText().isEmpty()
                || stateField.getText().isEmpty()
                || selectedImage == null) {
            showError("Please fill in all required fields (marked with *)");
            return false;
        }

        // Validate property type specific fields
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
    }

    // ... validation and helper methods ...
}
