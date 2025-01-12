package com.yourcompany.rentalmanagement.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.yourcompany.rentalmanagement.controller.CommercialPropertyController;
import com.yourcompany.rentalmanagement.model.Address;
import com.yourcompany.rentalmanagement.model.CommercialProperty;
import com.yourcompany.rentalmanagement.model.Owner;
import com.yourcompany.rentalmanagement.model.Property;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class CommercialPropertyManagementView implements Initializable {

    List<CommercialProperty> commercialProperties = new ArrayList<>();
    CommercialPropertyController commercialPropertyController = new CommercialPropertyController();
    private ViewRentalPropertiesController propertyController = new ViewRentalPropertiesController();

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

        commercialProperties = commercialPropertyController.getAllCommercialProperties();
        commercialPropertyTableView.setItems(FXCollections.observableArrayList(commercialProperties));
    }

    private void initializeColumn() {
        // Set column widths
        titleCol.setPrefWidth(120);
        addressCol.setPrefWidth(200);
        ownerCol.setPrefWidth(110);
        priceCol.setPrefWidth(100);
        statusCol.setPrefWidth(100);
        businessTypeCol.setPrefWidth(120);
        parkingSpaceCol.setPrefWidth(120);
        squareFootageCol.setPrefWidth(120);
        viewMoreCol.setPrefWidth(120);
        deleteCol.setPrefWidth(120);

        // Set cell value factories
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
                        CommercialProperty property = getTableView().getItems().get(getIndex());
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
                        CommercialProperty property = getTableView().getItems().get(getIndex());
                        propertyController.handleDelete(property);
                        commercialProperties.remove(property);
                        commercialPropertyTableView.refresh();
                    });
                    setText(null);
                    setGraphic(button);
                }
            }
        });
    }

}
