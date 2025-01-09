package com.yourcompany.rentalmanagement.view.components;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class LoadingSpinner extends StackPane {

    public LoadingSpinner() {
        getStyleClass().add("loading-spinner");

        for (int i = 0; i < 8; i++) {
            Circle circle = new Circle(4);
            circle.getStyleClass().add("spinner-circle");
            getChildren().add(circle);
        }
    }

    public void show(VBox parent) {
        setVisible(true);
        toFront();
    }

    public void hide(VBox parent) {
        setVisible(false);
        toBack();
    }
}
