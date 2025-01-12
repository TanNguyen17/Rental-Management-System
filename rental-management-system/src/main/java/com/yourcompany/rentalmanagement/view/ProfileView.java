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
    public void initialize(URL url, ResourceBundle rb) {
        loadUserData();
    }

    private void loadUserData() {
        if (userSession.getCurrentUser() != null) {
            username.setText(userSession.getCurrentUser().getUsername());
            phoneNumber.setText(userSession.getCurrentUser().getPhoneNumber());
            email.setText(userSession.getCurrentUser().getEmail());
            // Set date of birth if available
            if (userSession.getCurrentUser().getDob() != null) {
                dateOfBirth.setValue(userSession.getCurrentUser().getDob());
            }
            // Load address data if available
            if (userSession.getCurrentUser().getAddress() != null) {
                Address address = userSession.getCurrentUser().getAddress();
                number.setText(address.getNumber());
                street.setText(address.getStreet());
                ward.setText(address.getWard());
                district.setText(address.getDistrict());
                city.setText(address.getCity());
            }
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
