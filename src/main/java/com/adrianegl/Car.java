/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.adrianegl;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * @author adria
 */
public class Car {

    public static final double WIDTH = 70;
    public static final double HEIGHT = 24;
    public static final int IMAGE_COUNT = 4;
    private final int id;
    private double speed;
    private double distanceTravelled;
    private boolean finished;
    private final StackPane view;
    private final Color color;

    public Car(int id, double speed, Color color) {
        this.id = id;
        this.speed = speed;
        this.color = color;
        this.distanceTravelled = 0;
        this.finished = false;

        ImageView carImage = new ImageView(loadCarImage(id - 1, WIDTH * 2));
        carImage.setFitWidth(WIDTH);
        carImage.setFitHeight(HEIGHT);
        carImage.setPreserveRatio(true);
        carImage.setSmooth(true);
        carImage.setEffect(new DropShadow(4, Color.rgb(0, 0, 0, 0.55)));

        Label numberLabel = new Label(String.valueOf(id));
        numberLabel.setTextFill(Color.WHITE);
        numberLabel.setStyle(
            "-fx-font-weight: bold;" +
            "-fx-font-size: 10px;" +
            "-fx-background-color: rgba(0,0,0,0.60);" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 0 4 0 4;"
        );

        view = new StackPane(carImage, numberLabel);
        StackPane.setAlignment(numberLabel, Pos.CENTER);
        view.setPrefSize(WIDTH, HEIGHT);
        view.setMinSize(WIDTH, HEIGHT);
        view.setMaxSize(WIDTH, HEIGHT);
    }

    public static Image loadCarImage(int index, double width) {
        int n = Math.floorMod(index, IMAGE_COUNT) + 1;
        String path = Car.class.getResource("images/car" + n + ".png").toExternalForm();
        // requestedWidth, requestedHeight (0 = automático), preserveRatio, smooth
        return new Image(path, width, 0, true, true);
    }

    // Getters
    public int getId() { return id; }
    public double getSpeed() { return speed; }
    public double getDistanceTravelled() { return distanceTravelled; }
    public boolean isFinished() { return finished; }
    public StackPane getView() { return view; }
    public Color getColor() { return color; }

    // Setters
    public void setSpeed(double speed) { this.speed = Math.max(0, speed); }
    public void setDistanceTravelled(double distance) { this.distanceTravelled = Math.max(0, distance); }
    public void setFinished(boolean finished) { this.finished = finished; }

    public void reset() {
        distanceTravelled = 0;
        finished = false;
        view.setLayoutX(10);
    }

    public double getProgress(double totalDistance) {
        if (totalDistance <= 0) {
            return 0;
        }
        return Math.min(100, (distanceTravelled / totalDistance) * 100);
    }
}