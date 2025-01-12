package com.yourcompany.rentalmanagement.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.yourcompany.rentalmanagement.controller.ResidentialPropertyController;
import com.yourcompany.rentalmanagement.model.Address;
import com.yourcompany.rentalmanagement.model.Owner;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.ResidentialProperty;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ResidentialPropertyManagementView implements Initializable {

    List<ResidentialProperty> residentialProperties = new ArrayList<>();
    ResidentialPropertyController residentialPropertyController = new ResidentialPropertyController();
    private ViewRentalPropertiesController propertyController = new ViewRentalPropertiesController();

    @FXML
    TableView<ResidentialProperty> residentialPropertyTableView = new TableView<>();

    @FXML
    TableColumn<ResidentialProperty, String> titleCol = new TableColumn<>();

    @FXML
    TableColumn<ResidentialProperty, Address> addressCol = new TableColumn<>();

    @FXML
    TableColumn<ResidentialProperty, Owner> ownerCol = new TableColumn<>();

    @FXML
    TableColumn<ResidentialProperty, Double> priceCol = new TableColumn<>();

    @FXML
    TableColumn<ResidentialProperty, Property.propertyStatus> statusCol = new TableColumn<>();

    @FXML
    TableColumn<ResidentialProperty, Boolean> gardenAvailabilityCol = new TableColumn<>();

    @FXML
    TableColumn<ResidentialProperty, Integer> bedroomNumCol = new TableColumn<>();

    @FXML
    TableColumn<ResidentialProperty, Boolean> petFriendlinessCol = new TableColumn<>();

    @FXML
    private TableColumn<ResidentialProperty, Button> viewMoreCol = new TableColumn<>();

    @FXML
    private TableColumn<ResidentialProperty, Button> deleteCol = new TableColumn<>();

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        initializeColumn();
        initializeViewMoreColumn();
        initializeDeleteColumn();
        new Thread(() -> {
            List<ResidentialProperty> allResidentialProperties = residentialPropertyController.getAllResidentialProperty();
            Platform.runLater(() -> {
                if (!allResidentialProperties.isEmpty()) {
                    residentialProperties.addAll(allResidentialProperties);
                    residentialPropertyTableView.setItems(FXCollections.observableArrayList(residentialProperties));
                }
            });
        }).start();
//        residentialProperties = FXCollections.observableArrayList(residentialPropertyController.getAllResidentialProperty());
//        residentialPropertyTableView.setItems(residentialProperties);
    }

    private void initializeColumn() {
        // Set column widths
        titleCol.setPrefWidth(120);
        addressCol.setPrefWidth(200);
        ownerCol.setPrefWidth(110);
        priceCol.setPrefWidth(100);
        statusCol.setPrefWidth(100);
        gardenAvailabilityCol.setPrefWidth(140);
        bedroomNumCol.setPrefWidth(140);
        petFriendlinessCol.setPrefWidth(120);
        viewMoreCol.setPrefWidth(120);
        deleteCol.setPrefWidth(120);

        // Set cell value factories
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("owner"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        gardenAvailabilityCol.setCellValueFactory(new PropertyValueFactory<>("gardenAvailability"));
        bedroomNumCol.setCellValueFactory(new PropertyValueFactory<>("numberOfBedrooms"));
        petFriendlinessCol.setCellValueFactory(new PropertyValueFactory<>("petFriendliness"));
    }

    private void initializeViewMoreColumn() {
        viewMoreCol.setCellFactory(col -> new TableCell<>() {
            @Override
            public void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                setGraphic(null);
                if (!empty) {
                    Button button = new Button("Edit");
                    button.getStyleClass().add("edit-button");
                    button.setOnAction(e -> {
                        ResidentialProperty property = getTableView().getItems().get(getIndex());
                        propertyController.handleEdit(property);
                    });
                    setText(null);
                    setGraphic(button);
                }
            }
        });
    }

    private void initializeDeleteColumn() {
        deleteCol.setCellFactory(col -> new TableCell<>() {
            @Override
            public void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                setGraphic(null);
                if (!empty) {
                    Button button = new Button("Delete");
                    button.getStyleClass().add("delete-button");
                    button.setOnAction(e -> {
                        ResidentialProperty property = getTableView().getItems().get(getIndex());
                        propertyController.handleDelete(property);
                        residentialProperties.remove(property);
                        residentialPropertyTableView.refresh();
                    });
                    setText(null);
                    setGraphic(button);
                }
            }
        });
    }

}
