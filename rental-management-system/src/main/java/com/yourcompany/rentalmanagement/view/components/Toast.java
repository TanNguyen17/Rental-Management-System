package com.yourcompany.rentalmanagement.view.components;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

public class Toast {
    public static void show(Window owner, String message, String type) {
        Popup popup = new Popup();
        popup.setAutoHide(true);

        HBox toastBox = new HBox(10);
        toastBox.getStyleClass().addAll("toast", "toast-" + type);
        toastBox.setAlignment(Pos.CENTER_LEFT);
        toastBox.setPrefWidth(300);
        
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.getStyleClass().add("toast-message");
        
        toastBox.getChildren().add(messageLabel);
        popup.getContent().add(toastBox);

        // Position at top-right
        popup.setOnShown(e -> {
            popup.setX(owner.getX() + owner.getWidth() - toastBox.getWidth() - 20);
            popup.setY(owner.getY() + 40);
        });

        // Auto hide after 3 seconds
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> popup.hide());

        popup.show(owner);
        delay.play();
    }

    public static void showSuccess(Stage owner, String message) {
        show(owner, message, "success");
    }

    public static void showError(Stage owner, String message) {
        show(owner, message, "error");
    }
}