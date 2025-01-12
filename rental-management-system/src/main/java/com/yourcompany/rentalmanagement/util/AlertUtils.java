package com.yourcompany.rentalmanagement.util;
/**
 * @author FTech
 */
import javafx.scene.control.Alert;

public class AlertUtils {

    public static void showSuccessAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null); // Important: Clear header text for cleaner look
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null); // Important: Clear header text for cleaner look
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showWarningAlert(String title, String content){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
