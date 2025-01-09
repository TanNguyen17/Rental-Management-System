package com.yourcompany.rentalmanagement.view.components;

import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class LoadingSpinner extends StackPane {

    public Runnable show;
    public Runnable hide;

    public LoadingSpinner() {
        getStyleClass().add("loading-spinner");

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setProgress(-1);
        spinner.setPrefSize(50, 50);

        Rectangle overlay = new Rectangle();
        overlay.setFill(Color.rgb(255, 255, 255, 0.8));
        overlay.widthProperty().bind(widthProperty());
        overlay.heightProperty().bind(heightProperty());

        getChildren().addAll(overlay, spinner);
        setAlignment(Pos.CENTER);

        setVisible(false);
        setMouseTransparent(true);

        show = () -> setVisible(true);
        hide = () -> setVisible(false);
    }

    public void show() {
        if (show != null) {
            show.run();
        }
    }

    public void hide() {
        if (hide != null) {
            hide.run();
        }
    }
}
