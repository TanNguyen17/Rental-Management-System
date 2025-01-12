package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.controller.ResidentialPropertyController;
import com.yourcompany.rentalmanagement.model.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ResidentialPropertyManagementView implements Initializable {
    List<ResidentialProperty> residentialProperties = new ArrayList<>();
    ResidentialPropertyController residentialPropertyController = new ResidentialPropertyController();


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

    private void initializeColumn(){
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
        viewMoreCol.setCellFactory(col -> new TableCell<>(){
            @Override
            public void updateItem(Button item, boolean empty){
                super.updateItem(item, empty);
                setText(null);
                setGraphic(null);
                if (!empty){
                    Button button = new Button("View More");
                    button.setOnAction(e -> {
                        System.out.println("You pressed View More");
                    });
                    setText(null);
                    setGraphic(button);
                }
            }
        });
    }

    private void initializeDeleteColumn() {
        deleteCol.setCellFactory(col -> new TableCell<>(){
            @Override
            public void updateItem(Button item, boolean empty){
                super.updateItem(item, empty);
                setText(null);
                setGraphic(null);
                if (!empty){
                    Button button = new Button("Delete");
                    button.setOnAction(e -> {
                        System.out.println("You pressed Delete");
                    });
                    setText(null);
                    setGraphic(button);
                }
            }
        });
    }


}