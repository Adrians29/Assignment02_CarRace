package com.adrianegl;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author adria
 */

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.List;

public class SplashController {

    @FXML private VBox rootBox;
    @FXML private VBox roadBox;
    @FXML private ImageView car1View;
    @FXML private ImageView car2View;
    @FXML private ImageView car3View;
    @FXML private ImageView car4View;
    @FXML private ProgressBar progressBar;
    @FXML private Label percentLabel;

    private List<ImageView> carViews;

    @FXML
    public void initialize() {
        Rectangle clip = new Rectangle(800, 360);
        clip.setArcWidth(18);
        clip.setArcHeight(18);
        roadBox.setClip(clip);

        carViews = List.of(car1View, car2View, car3View, car4View);
        for (int i = 0; i < carViews.size(); i++) {
            ImageView view = carViews.get(i);
            view.setImage(Car.loadCarImage(i, 380)); 
            view.setSmooth(true);
            view.setTranslateX(-900);
        }

        progressBar.progressProperty().addListener((obs, oldV, newV) -> {
            int percent = (int) Math.round(newV.doubleValue() * 100);
            percentLabel.setText(percent >= 100 ? "Ready!" : "Loading... " + percent + "%");
        });
    }

    public void play(Runnable onFinished) {
        ParallelTransition entry = new ParallelTransition();
        for (int i = 0; i < carViews.size(); i++) {
            TranslateTransition t = new TranslateTransition(Duration.millis(900), carViews.get(i));
            t.setFromX(-900);
            t.setToX(0);
            t.setDelay(Duration.millis(i * 250L));
            t.setInterpolator(Interpolator.EASE_OUT);
            entry.getChildren().add(t);
        }

        Timeline loading = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
            new KeyFrame(Duration.seconds(3.2), new KeyValue(progressBar.progressProperty(), 1.0))
        );
        ParallelTransition intro = new ParallelTransition(entry, loading);

        ParallelTransition exit = new ParallelTransition();
        for (int i = 0; i < carViews.size(); i++) {
            TranslateTransition t = new TranslateTransition(Duration.millis(600), carViews.get(i));
            t.setToX(1000);
            t.setDelay(Duration.millis(i * 100L));
            t.setInterpolator(Interpolator.EASE_IN);
            exit.getChildren().add(t);
        }

        FadeTransition fade = new FadeTransition(Duration.millis(300), rootBox);
        fade.setFromValue(1);
        fade.setToValue(0);

        SequentialTransition all = new SequentialTransition(
            intro, new PauseTransition(Duration.millis(250)), exit, fade
        );
        all.setOnFinished(e -> onFinished.run());
        all.play();
    }
}

