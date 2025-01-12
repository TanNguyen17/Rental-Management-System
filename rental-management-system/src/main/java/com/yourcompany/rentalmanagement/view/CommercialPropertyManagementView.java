package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.controller.CommercialPropertyController;
import com.yourcompany.rentalmanagement.model.Address;
import com.yourcompany.rentalmanagement.model.Owner;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.CommercialProperty;
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
import java.util.ResourceBundle;

public class CommercialPropertyManagementView implements Initializable {
    ObservableList<CommercialProperty> commercialProperties;
    CommercialPropertyController commercialPropertyController = new CommercialPropertyController();


    @FXML
    TableView<CommercialProperty> commercialPropertyTableView = new TableView<>();

    @FXML
    TableColumn<CommercialProperty, String> titleCol = new TableColumn<>();

    @FXML
    TableColumn<CommercialProperty, Address> addressCol = new TableColumn<>();

    @FXML
    TableColumn<CommercialProperty, Owner> ownerCol = new TableColumn<>();

    @FXML
    TableColumn<CommercialProperty, Double> priceCol = new TableColumn<>();

    @FXML
    TableColumn<CommercialProperty, Property.propertyStatus> statusCol = new TableColumn<>();

    @FXML
    TableColumn<CommercialProperty, String> businessTypeCol = new TableColumn<>();

    @FXML
    TableColumn<CommercialProperty, Boolean> parkingSpaceCol = new TableColumn<>();

    @FXML
    TableColumn<CommercialProperty, Double> squareFootageCol = new TableColumn<>();

    @FXML
    private TableColumn<CommercialProperty, Button> viewMoreCol = new TableColumn<>();

    @FXML
    private TableColumn<CommercialProperty, Button> deleteCol = new TableColumn<>();

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        initializeColumn();
        initializeViewMoreColumn();
        initializeDeleteColumn();
        commercialProperties =
                FXCollections.observableArrayList(commercialPropertyController.getAllCommercialProperties());
        commercialPropertyTableView.setItems(commercialProperties);
    }

    private void initializeColumn(){
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("owner"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        businessTypeCol.setCellValueFactory(new PropertyValueFactory<>("businessType"));
        parkingSpaceCol.setCellValueFactory(new PropertyValueFactory<>("parkingSpace"));
        squareFootageCol.setCellValueFactory(new PropertyValueFactory<>("squareFootage"));
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