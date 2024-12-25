package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.controller.UserController;
import com.yourcompany.rentalmanagement.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfileViewController implements Initializable {
    UserController userController = new UserController();
    @FXML
    private Text username;

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

        username.setText(currentUser.getUsername());
        firstName.setPromptText("First Name");
        firstName.setFocusTraversable(false);
        lastName.setPromptText("Last Name");
        email.setPromptText("Email");
        phoneNumber.setPromptText("Phone Number");
        phoneNumber.setFocusTraversable(false);
        dateOfBirth.setPromptText("19/10/2003");
        cityChoice.setValue("tab");
        cityChoice.getItems().addAll(currentUser.getAddress().getCity());
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

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w!#$%&'*+/=?^`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^`{|}~-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^\\d{3}\\d{3}\\d{4}$";  // Matches XXX-XXX-XXXX format
        return phoneNumber.matches(phoneRegex);
    }
}
