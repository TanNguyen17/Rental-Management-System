package com.yourcompany.rentalmanagement.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.yourcompany.rentalmanagement.controller.UserController;
import com.yourcompany.rentalmanagement.model.User;
import com.yourcompany.rentalmanagement.util.AddressData;
import com.yourcompany.rentalmanagement.util.AlertUtils;
import com.yourcompany.rentalmanagement.util.CloudinaryService;
import com.yourcompany.rentalmanagement.util.UserSession;

import io.github.palexdev.materialfx.controls.MFXPasswordField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;


public class ProfileView implements Initializable {
    private UserController userController;
    private CloudinaryService cloudinaryService = new CloudinaryService();
    private User currentUser = UserSession.getInstance().getCurrentUser();

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
    private Text errorStreetNumber;

    @FXML
    private TextField streetName;

    @FXML
    private Text errorStreetName;

    @FXML
    private ChoiceBox<String> provinceChoice;

    @FXML
    private ChoiceBox<String> districtChoice;

    @FXML
    private ChoiceBox<String> wardChoice;

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
        username.setText(currentUser.getUsername());

        Image image = new Image(currentUser.getProfileImage() != null ? currentUser.getProfileImage() : "https://res.cloudinary.com/dqydgahsj/image/upload/v1735456851/q7ldgrgk68q8fnwqadkw.jpg");
        profileImage.setImage(image);

        firstName.setPromptText(currentUser.getUsername() != null ? currentUser.getUsername() : "First Name");
        lastName.setPromptText(currentUser.getUsername() != null ? currentUser.getUsername() : "Last Name");
        email.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "Email");

        phoneNumber.setText(currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "Phone Number");
        dateOfBirth.setValue(currentUser.getDob() != null ? currentUser.getDob() : LocalDate.of(2025, 1, 1));
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
    private void updateProfile(ActionEvent event) {
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
            data.put("username", firstNameText);
            data.put("email", emailText);
            data.put("phoneNumber", phoneNumberText);
            data.put("dob", date);
            userController.updateProfile(currentUser.getId(), data, currentUser.getRole());
        }
    }

    @FXML
    public void updateAddress(ActionEvent event) {
        String streetNameText = streetName.getText();
        String streetNumberText = streetNumber.getText();
        String province = provinceChoice.getValue();
        String city = districtChoice.getValue();

        if (streetNameText == null) {
            errorStreetName.setText("Please enter street name");
        }

        if (streetNumberText == null) {
            errorStreetNumber.setText("Please enter street number");
        }

        if (streetNameText != null && streetNumberText != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("streetName", streetNameText);
            data.put("streetNumber", streetNumberText);
            data.put("province", province);
            data.put("city", city);
            userController.updateAddress(currentUser.getId(), data, currentUser.getRole());
        }

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
                userController.updateImageLink(currentUser.getId(), imageUrl, currentUser.getRole());
                profileImage.setImage(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void updatePassword(ActionEvent event) {
        errorCurrentPassword.setText("");
        errorNewPassword.setText("");
        errorConfirmPassword.setText("");

        String currentPasswordText = currentPassword.getText();
        String newPasswordText = newPassword.getText();
        String confirmPasswordText = confirmPassword.getText();

        if (validatePassword(currentPasswordText, newPasswordText, confirmPasswordText)) {
            userController.updatePassword(currentUser.getId(), currentPasswordText, confirmPasswordText, currentUser.getRole());
        }
    }

    // Validate profile info
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

    // Validate password
    private boolean validatePassword(String currentPassword, String newPassword, String confirmPassword) {
        boolean valid = true;
        if (currentPassword.isEmpty()) {
            errorCurrentPassword.setText("Current Password cannot be empty");
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
        } else if (!confirmPassword.matches(".*[!@#$%^&*()].*")) {
            errorNewPassword.setText("New Password must contain at least one special character");
            valid = false;
        }

        return valid;
    }

    // Validate email
    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w!#$%&'*+/=?^`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^`{|}~-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        return email.matches(emailRegex);
    }

    // Validate phone number
    private boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^\\d{3}\\d{3}\\d{4}$";  // Matches XXX-XXX-XXXX format
        return phoneNumber.matches(phoneRegex);
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

    public void showSuccessAlert(String title, String content) {
        AlertUtils.showSuccessAlert(title, content);
    }

    public void showErrorAlert(String title, String content) {
        AlertUtils.showErrorAlert(title, content);
    }
}
