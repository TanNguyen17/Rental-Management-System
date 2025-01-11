package com.yourcompany.rentalmanagement.view.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public class LoadingSpinner extends StackPane {

    @FXML
    private Region blocker;

    @FXML
    private ProgressIndicator spinner;

    public LoadingSpinner() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/LoadingSpinner.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

            // Add CSS class
            getStyleClass().add("loading-spinner");

            // Make sure it's on top but initially hidden
            setVisible(false);
            setMouseTransparent(false); // Block mouse events when visible

            // Make it fill the parent
            setMaxWidth(Double.MAX_VALUE);
            setMaxHeight(Double.MAX_VALUE);

            // Ensure blocker fills the space
            if (blocker != null) {
                blocker.setMaxWidth(Double.MAX_VALUE);
                blocker.setMaxHeight(Double.MAX_VALUE);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load LoadingSpinner.fxml", e);
        }
    }

    public void show() {
        setVisible(true);
        setMouseTransparent(false);
    }

    public void hide() {
        setVisible(false);
        setMouseTransparent(true);
    }

    @FXML
    private void initialize() {
        // Any additional initialization after FXML loading
        if (blocker != null) {
            blocker.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");
        }
        if (spinner != null) {
            spinner.setMaxWidth(100);
            spinner.setMaxHeight(100);
        }
    }
}
