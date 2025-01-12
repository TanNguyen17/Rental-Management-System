package com.yourcompany.rentalmanagement.view;

import java.net.URL;
import java.util.ResourceBundle;

import com.yourcompany.rentalmanagement.model.Address;
import com.yourcompany.rentalmanagement.util.UserSession;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class ProfileView implements Initializable {

    @FXML
    private Text username;
    @FXML
    private TextField phoneNumber;
    @FXML
    private TextField email;
    @FXML
    private DatePicker dateOfBirth;
    @FXML
    private ImageView profileImage;
    @FXML
    private TextField number;
    @FXML
    private TextField street;
    @FXML
    private TextField ward;
    @FXML
    private TextField district;
    @FXML
    private TextField city;
    @FXML
    private PasswordField currentPassword;
    @FXML
    private PasswordField newPassword;
    @FXML
    private PasswordField confirmPassword;

    private UserSession userSession = UserSession.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userController = new UserController();
        // Load User Data in Background
        new Thread(() -> {
            // Load user data
            currentUser = userController.getUserProfile(currentUser.getId(), currentUser.getRole());

            // Update UI on JavaFX Application Thread
            Platform.runLater(() -> {
                if (currentUser != null) {
                    initialProfile();
                }
            });
        }).start();
            // Load address data
            Platform.runLater(() -> {
                initialAddress();
                provinceChoice.setOnAction(event -> {
                    String selectedProvince = provinceChoice.getValue();
                    updateDistrictCombobox(selectedProvince);
                });

                districtChoice.setOnAction(event -> {
                    String selectedCity = districtChoice.getValue();
                    updateWardCombobox(selectedCity);
                });
            });
    }

    private void initialProfile() {
        System.out.println(currentUser.getUsername());
        username.setText(currentUser.getUsername());

        Image image = new Image(currentUser.getProfileImage() != null ? currentUser.getProfileImage() : "https://res.cloudinary.com/dqydgahsj/image/upload/v1736670115/zguznvwm7ib3exi1g1ko.png");
        profileImage.setImage(image);

        firstName.setPromptText(currentUser.getUsername() != null ? currentUser.getUsername() : "First Name");
        lastName.setPromptText(currentUser.getUsername() != null ? currentUser.getUsername() : "Last Name");
        email.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "Email");

        phoneNumber.setText(currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "Phone Number");
        dateOfBirth.setValue(currentUser.getDob() != null ? currentUser.getDob() : LocalDate.of(2025, 1, 1));

        paymentText.setVisible(false);
        paymentChoice.setVisible(false);

        if (currentUser.getRole() != null && currentUser.getRole().equals(UserRole.TENANT)) {
            Tenant tenantUser = (Tenant) currentUser;
            paymentText.setVisible(true);
            paymentChoice.setVisible(true);
            paymentChoice.setValue(tenantUser.getPaymentMethod() != null ? tenantUser.getPaymentMethod() : null);
            ObservableList<Payment.paymentMethod> methodOptions = FXCollections.observableArrayList(Payment.paymentMethod.values());
            paymentChoice.setItems(methodOptions);
        }
    }

    // handle add current user address
    private void initialAddress() {
        provinceChoice.getItems().addAll(AddressData.provinceCities.keySet());
        if (currentUser.getAddress() != null) {
            streetName.setText(currentUser.getAddress().getStreet());
            streetNumber.setText(currentUser.getAddress().getNumber());

            if (provinceChoice.getItems().contains(currentUser.getAddress().getCity())) {
                provinceChoice.setValue(currentUser.getAddress().getCity());
                updateDistrictCombobox(currentUser.getAddress().getCity());

                //Populate city choice and set its value
                if (districtChoice.getItems().contains(currentUser.getAddress().getCity())) {
                    districtChoice.setValue(currentUser.getAddress().getCity());
                    updateDistrictCombobox(currentUser.getAddress().getCity());

                    if (wardChoice.getItems().contains(currentUser.getAddress().getWard())) {
                        wardChoice.setValue(currentUser.getAddress().getWard());
                        updateWardCombobox(currentUser.getAddress().getWard());
                    }

                } else {
                    System.err.println("City/Province not found in ComboBox: " + currentUser.getAddress().getCity());
                }          }
        } else {
            streetName.setText("Street");
            streetNumber.setText("Street Number");
        }
    }

    @FXML
    private void updateProfile() {
        // Implement profile update logic
        System.out.println("Updating profile...");
    }

    @FXML
    private void handleUploadImage() {
        // Implement image upload logic
        System.out.println("Uploading image...");
    }

    @FXML
    private void updateAddress() {
        // Implement address update logic
        System.out.println("Updating address...");
    }

    @FXML
    private void updatePassword() {
        if (newPassword.getText().equals(confirmPassword.getText())) {
            // Implement password update logic
            System.out.println("Updating password...");
        } else {
            showErrorAlert("Password Error", "New passwords do not match");
        }
    }

    public void showSuccessAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
