package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.controller.UserController;
import com.yourcompany.rentalmanagement.model.User;
import com.yourcompany.rentalmanagement.util.CloudinaryService;
import com.yourcompany.rentalmanagement.util.TimeFormat;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ProfileViewController implements Initializable {
    private UserController userController = new UserController();
    private CloudinaryService cloudinaryService = new CloudinaryService();

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
    private ChoiceBox<String> cityChoice;

    private String[] city = {"HCM", "DN", "HN"};

    @FXML
    private Button submitButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        User currentUser = userController.getUserProfile(5);

        if (currentUser != null) {
            username.setText(currentUser.getUsername());

            Image image = new Image(currentUser.getProfileImage());
            profileImage.setImage(image);

            firstName.setPromptText("First Name");
            firstName.setFocusTraversable(false);
            lastName.setPromptText("Last Name");

            email.setText(currentUser.getEmail());
            phoneNumber.setText(currentUser.getPhoneNumber());
            phoneNumber.setFocusTraversable(false);
            dateOfBirth.setPromptText(TimeFormat.dateFormat(currentUser.getDob()));
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

    public void submit(ActionEvent event) {
        errorFirstName.setText("");
        errorLastName.setText("");
        errorEmail.setText("");
        errorPhoneNumber.setText("");

        String firstNameText = firstName.getText();
        String lastNameText = lastName.getText();
        String emailText = email.getText();
        String phoneNumberText = phoneNumber.getText();

        if (firstNameText.isEmpty()) {
            errorFirstName.setText("First Name cannot be empty");
        }

        if (lastNameText.isEmpty()) {
            errorLastName.setText("Last Name cannot be empty");
        }

        if (emailText.isEmpty()) {
            errorEmail.setText("Email cannot be empty");
        }

        if (phoneNumberText.isEmpty()) {
            errorPhoneNumber.setText("Phone Number cannot be empty");
        }

        if (!isValidEmail(emailText)) {
            errorEmail.setText("Invalid Email");
        }

        if (!isValidPhoneNumber(phoneNumberText)) {
            errorPhoneNumber.setText("Invalid Phone Number");
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
                userController.updateUserImageLink(5, imageUrl);
                profileImage.setImage(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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
