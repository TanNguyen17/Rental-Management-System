package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.controller.UserController;
import com.yourcompany.rentalmanagement.model.User;
import com.yourcompany.rentalmanagement.util.CloudinaryService;
import com.yourcompany.rentalmanagement.util.TimeFormat;

import io.github.palexdev.materialfx.controls.MFXPasswordField;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.scene.input.KeyEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


public class ProfileViewController implements Initializable {
    private UserController userController = new UserController();
    private CloudinaryService cloudinaryService = new CloudinaryService();
    private User currentUser;

    @FXML
    private Text username;

    @FXML
    private ImageView profileImage;

    @FXML
    private Button uploadImageButton;

    @FXML
    private TextField firstName;

    @FXML
    private Text errorFirstName;

    @FXML
    private TextField lastName;

    @FXML
    private Text errorLastName;

    @FXML
    private TextField email;

    @FXML
    private Text errorEmail;

    @FXML
    private TextField phoneNumber;

    @FXML
    private Text errorPhoneNumber;

    @FXML
    private DatePicker dateOfBirth;

    @FXML
    private TextField streetNumber;

    @FXML
    private TextField  streetName;

    @FXML
    private ChoiceBox<String> cityChoice;

    @FXML
    private MFXPasswordField currentPassword;

    @FXML
    private Text errorCurrentPassword;

    @FXML
    private MFXPasswordField newPassword;

    @FXML
    private Text errorNewPassword;

    @FXML
    private MFXPasswordField confirmPassword;

    @FXML
    private Text errorConfirmPassword;

    @FXML
    private Button updatePasswordButton;

    @FXML
    private Button updateProfileButton;

    @FXML
    private Button updateAddressButton;

    private String[] city = {"HCM", "DN", "HN"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = userController.getUserProfile(5);

        if (currentUser != null) {
            username.setText(currentUser.getUsername());

            Image image = new Image(currentUser.getProfileImage());
            profileImage.setImage(image);

            firstName.setPromptText("First Name");
            lastName.setPromptText("Last Name");

            email.setText(currentUser.getEmail());
            phoneNumber.setText(currentUser.getPhoneNumber());
            dateOfBirth.setPromptText(TimeFormat.dateToString(currentUser.getDob()));
            streetName.setText(currentUser.getAddress().getStreet());
            streetNumber.setText(currentUser.getAddress().getNumber());
            cityChoice.setValue(currentUser.getAddress().getCity());
            cityChoice.getItems().addAll(city);
        } else {
            username.setText("tan");
            firstName.setPromptText("First Name");
            firstName.setFocusTraversable(false);
            lastName.setPromptText("Last Name");
            email.setPromptText("Email");
            phoneNumber.setPromptText("Phone Number");
            phoneNumber.setFocusTraversable(false);
            dateOfBirth.setPromptText("19/10/2003");
            cityChoice.setValue("tab");
            cityChoice.getItems().addAll(city);
        }
    }

    @FXML
    public void updateProfile(ActionEvent event) {
        errorFirstName.setText("");
        errorLastName.setText("");
        errorEmail.setText("");
        errorPhoneNumber.setText("");

        String firstNameText = firstName.getText();
        String lastNameText = lastName.getText();
        String emailText = email.getText();
        String phoneNumberText = phoneNumber.getText();
        String date = dateOfBirth.getValue().toString();

        if (validateProfile(firstNameText, lastNameText, emailText, phoneNumberText)) {
            Map<String, Object> data = new HashMap<>();
            data.put("firstName", firstNameText);
            data.put("lastName", lastNameText);
            data.put("email", emailText);
            data.put("phoneNumber", phoneNumberText);
            data.put("date", date);

            userController.updateProfile(5, data);
        }
    }

    @FXML
    public void updateAddress(ActionEvent event) {
        String streetNameText = streetName.getText();
        String streetNumberText = streetNumber.getText();
        String city = cityChoice.getValue();

        if (streetNameText == null) {

        }

        Map<String, Object> data = new HashMap<>();
        data.put("streetName", streetNameText);
        data.put("streetNumber", streetNumberText);
        data.put("city", city);

        userController.updateAddress(5, data);
    }

    @FXML
    public void handleUploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                Image image = new Image(new FileInputStream(selectedFile));
                String imageUrl = cloudinaryService.uploadImage(selectedFile);
                userController.updateImageLink(5, imageUrl);
                profileImage.setImage(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    void updatePassword(ActionEvent event) {
        errorCurrentPassword.setText("");
        errorNewPassword.setText("");
        errorConfirmPassword.setText("");

        String currentPasswordText = currentPassword.getText();
        String newPasswordText = newPassword.getText();
        String confirmPasswordText = confirmPassword.getText();

        if (validatePassword(currentPasswordText, newPasswordText, confirmPasswordText)) {
            userController.updatePassword(5, confirmPasswordText);
        }
    }

    private boolean validateProfile(String firstName, String lastName, String email, String phoneNumber) {
        boolean valid = true;
        if (firstName.isEmpty()) {
            errorFirstName.setText("First Name cannot be empty");
            valid = false;
        }

        if (lastName.isEmpty()) {
            errorLastName.setText("Last Name cannot be empty");
            valid = false;
        }

        if (email.isEmpty()) {
            errorEmail.setText("Email cannot be empty");
            valid = false;
        } else if (!isValidEmail(email)) {
            errorEmail.setText("Invalid Email");
            valid = false;
        }

        if (phoneNumber.isEmpty()) {
            errorPhoneNumber.setText("Phone Number cannot be empty");
            valid = false;
        } else if (!isValidPhoneNumber(phoneNumber)) {
            errorPhoneNumber.setText("Invalid Phone Number");
            valid = false;
        }

        return valid;
    }

    private boolean validatePassword(String currentPassword, String newPassword, String confirmPassword) {
        boolean valid = true;
        if (currentPassword.isEmpty()) {
            errorCurrentPassword.setText("Current Password cannot be empty");
            valid = false;
        } else if (!currentPassword.equals(currentUser.getPassword())) {
            errorCurrentPassword.setText("Current Password does not match");
            valid = false;
        }

        if (newPassword.isEmpty()) {
            errorNewPassword.setText("New Password cannot be empty");
            valid = false;
        } else if (confirmPassword.isEmpty()) {
            errorConfirmPassword.setText("Confirm Password cannot be empty");
            valid = false;
        } else if (!confirmPassword.equals(confirmPassword)) {
            errorConfirmPassword.setText("Confirm Password does not match");
            valid = false;
        } else if (confirmPassword.length() < 8) {
            errorNewPassword.setText("New Password should be at least 8 characters");
            valid = false;
        } else if (confirmPassword.matches(".*[!@#$%^&*()].*")) {
            errorNewPassword.setText("New Password must contain at least one special character");
            valid = false;
        }

        return valid;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w!#$%&'*+/=?^`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^`{|}~-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^\\d{3}\\d{3}\\d{4}$";  // Matches XXX-XXX-XXXX format
        return phoneNumber.matches(phoneRegex);
    }
}
