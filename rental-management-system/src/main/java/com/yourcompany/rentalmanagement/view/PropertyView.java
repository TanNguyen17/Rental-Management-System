package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.controller.PropertyController;
import com.yourcompany.rentalmanagement.model.CommercialProperty;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.ResidentialProperty;
import com.yourcompany.rentalmanagement.util.AddressData;
import com.yourcompany.rentalmanagement.util.HibernateUtil;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
//        loadAvailableProperty();
//        setupPropertyList();
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
        //Handle exceptions from the controller
        try {
            List<Property> properties = propertyController.getPropertyByStatus(Property.propertyStatus.AVAILABLE, data);
            propertyList.setItems(FXCollections.observableList(properties));
        } catch (Exception e) {
            System.err.println("Error loading properties: " + e.getMessage());
            //Show error in UI
            Platform.runLater(() -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Error loading properties");
                alert.showAndWait();
            });
        }
    }

    private void setupPropertyList() {
        propertyList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Property property, boolean empty) {
                super.updateItem(property, empty);

                if (empty || property == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    HBox card = new HBox(10);
                    card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #ddd;");

                    ImageView imageView = new ImageView();
                    imageView.setFitHeight(100);
                    imageView.setFitWidth(100);

                    if (property.getImageLink() != null && !property.getImageLink().isEmpty()) {
                        new Thread(() -> {
                            try {
                                Image image = new Image(property.getImageLink());
                                Platform.runLater(() -> imageView.setImage(image));
                            } catch (IllegalArgumentException e) {
                                Platform.runLater(() -> imageView.setImage(new Image("/images/default_property.png")));
                                System.err.println("Invalid image path: " + property.getImageLink());
                            }
                        }).start();
                    } else {
                        imageView.setImage(new Image("/images/default_property.png"));
                    }

                    VBox info = new VBox(5);
                    info.getChildren().addAll(
                            new Label("Title: " + property.getTitle()),
                            new Label("Price: $" + property.getPrice()),
                            new Label("Status: " + property.getStatus())
                    );

                    Button viewButton = new Button("View Details");
                    viewButton.setOnAction(e -> showPropertyDetails(property));

                    Button rentButton = new Button("Rent");
                    rentButton.setOnAction(e -> rentProperty(property));

                    card.getChildren().addAll(imageView, info, viewButton, rentButton);
                    setGraphic(card);
                    setText(null);
                }
            }
        });
    }

    private void showPropertyDetails(Property property) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Property refreshedProperty;
            if (property instanceof ResidentialProperty) {
                refreshedProperty = session.createQuery(
                        "FROM ResidentialProperty p "
                        + "LEFT JOIN FETCH p.address "
                        + "WHERE p.id = :id", ResidentialProperty.class)
                        .setParameter("id", property.getId())
                        .getSingleResult();
            } else {
                refreshedProperty = session.createQuery(
                        "FROM CommercialProperty p "
                        + "LEFT JOIN FETCH p.address "
                        + "WHERE p.id = :id", CommercialProperty.class)
                        .setParameter("id", property.getId())
                        .getSingleResult();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PropertyDetails.fxml"));
            Scene scene = new Scene(loader.load());

            PropertyDetailsController controller = loader.getController();
            controller.setProperty(refreshedProperty);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Property Details");
            stage.show();
        } catch (IOException e) {
            System.err.println("Error showing property details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void rentProperty(Property property) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Implement rent property logic here
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RentalAgreementCreationView.fxml"));
            Scene scene = new Scene(loader.load());

            if (property instanceof ResidentialProperty) {
                ResidentialProperty residentialProperty = (ResidentialProperty) property;
                data = propertyController.getResidentialPropertyData(residentialProperty.getId());

            } else if (property instanceof CommercialProperty) {
                CommercialProperty commercialProperty = (CommercialProperty) property;
                data = propertyController.getResidentialPropertyData(commercialProperty.getId());
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
}