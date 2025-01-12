package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.controller.UserController;
import com.yourcompany.rentalmanagement.model.Address;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import com.yourcompany.rentalmanagement.model.User;
import com.yourcompany.rentalmanagement.model.UserRole;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.controlsfx.control.CheckComboBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class UserManagementView {
    List<User> users = new ArrayList<>();
    UserController userController = new UserController();

    @FXML
    private Button addNewBtn;

    @FXML
    private TableView<User> userTableView;

    @FXML
    private TableColumn<User, Long> IdCol;

    @FXML
    private TableColumn<User, String> userNameCol;

    @FXML
    private TableColumn<User, LocalDate> dobCol;

    @FXML
    private TableColumn<User, String> emailCol;

    @FXML
    private TableColumn<User, String> phoneNumCol;

    @FXML
    private TableColumn<User, UserRole> roleSpaceCol;

    @FXML
    private TableColumn<User, Button> viewMoreCol;

    @FXML
    private CheckComboBox<String> roleFilter;

    // Initialization Method
    @FXML
    public void initialize() {
        configureTableColumns();
        // initializeViewMoreColumn();
        configureTableColumns();
        configureRoleFilterCheckComboBox();

        new Thread(() -> {
            List<User> allUsers = getAllUsers();
            Platform.runLater(() -> {
                if (!allUsers.isEmpty()) {
                    users.addAll(allUsers);
                    userTableView.setItems(FXCollections.observableArrayList(users));
                }
            });
        }).start();
    }

    public List<User> getAllUsers(){
        // Combine into one list
        List<User> allUsers = new ArrayList<>();



        // Cast each list to List<User> and add them
        allUsers.addAll((List<User>) (List<?>) userController.getAllTenant());
        allUsers.addAll((List<User>) (List<?>) userController.getAllOwners());
        allUsers.addAll((List<User>) (List<?>) userController.getAllHosts());
        return allUsers;
    }

    private void configureTableColumns() {
        IdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        dobCol.setCellValueFactory(new PropertyValueFactory<>("dob"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneNumCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        roleSpaceCol.setCellValueFactory(new PropertyValueFactory<>("role"));
    }

    private void configureRoleFilterCheckComboBox() {
        ObservableList<String> roles = FXCollections.observableArrayList("TENANT", "OWNER", "HOST");
        roleFilter.getItems().addAll(roles);

        roleFilter.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) change -> {
            filterTableByRoles(roleFilter.getCheckModel().getCheckedItems());
        });
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
//                        long raId = this.getTableView().getItems().get(getIndex()).getId();
//                        openUpdateForm(raId);
                    });
                    setText(null);
                    setGraphic(button);
                }
            }
        });
    }

    private void filterTableByRoles(List<String> selectedRoles) {
        ObservableList<User> allUsers = FXCollections.observableArrayList(getAllUsers()); // Retrieve all users

        if (!selectedRoles.isEmpty()) {
            ObservableList<User> filteredUsers = FXCollections.observableArrayList();
            for (User user : allUsers) {
                if (selectedRoles.contains(user.getRole().toString())) {
                    filteredUsers.add(user);
                }
            }
            userTableView.setItems(filteredUsers);
        } else {
            userTableView.setItems(allUsers);
        }
    }
}