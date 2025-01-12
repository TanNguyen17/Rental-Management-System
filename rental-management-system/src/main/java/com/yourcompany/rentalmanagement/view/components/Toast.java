package com.yourcompany.rentalmanagement.view.components;
/**
 * @author FTech
 */
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

public class Toast {

    public static void show(Window owner, String message, String type) {
        Popup popup = new Popup();
        popup.setAutoHide(true);

        HBox toastBox = new HBox();
        toastBox.getStyleClass().addAll("toast", "toast-" + type);
        toastBox.setAlignment(Pos.CENTER_LEFT);
        toastBox.setPrefWidth(350);

        // Load CSS file
        toastBox.getStylesheets().add(
                Toast.class.getResource("/css/components/toast.css").toExternalForm()
        );

        VBox messageBox = new VBox(4);
        messageBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(messageBox, Priority.ALWAYS);

        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("toast-message");

        // Get current time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        String timeStr = "Today, " + now.format(formatter);

        Label timeLabel = new Label(timeStr);
        timeLabel.getStyleClass().add("toast-time");

        messageBox.getChildren().addAll(messageLabel, timeLabel);

        Label closeButton = new Label("×");
        closeButton.getStyleClass().add("toast-close");
        closeButton.setOnMouseClicked(e -> popup.hide());

        toastBox.getChildren().addAll(messageBox, closeButton);
        popup.getContent().add(toastBox);

        popup.setOnShown(e -> {
            popup.setX(owner.getX() + owner.getWidth() - toastBox.getWidth() - 20);
            popup.setY(owner.getY() + owner.getHeight() - toastBox.getHeight() - 40);
        });

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), toastBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition pause = new PauseTransition(Duration.seconds(2.5));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), toastBox);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> popup.hide());

        SequentialTransition sequence = new SequentialTransition(fadeIn, pause, fadeOut);

        popup.show(owner);
        sequence.play();
    }

    public static void showSuccess(Window owner, String message) {
        show(owner, message, "success");
    }

    public static void showError(Window owner, String message) {
        show(owner, message, "error");
    }
}
