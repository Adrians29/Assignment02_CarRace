/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.adrianegl;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author adria
 */
public class Car {
    private int id;
    private double speed;
    private double distanceTravelled;
    private boolean finished;
    
    private final StackPane view;
    private final Color color;

    private Rectangle body;
    private Label numberLabel;
    
    public Car(int id, double speed, Color color){
        this.id = id;
        this.speed = speed;
        this.color = color;
        
        this.distanceTravelled = 0;
        this.finished = false;
        
        //create a general rectangle to represent the car
        body = new Rectangle(55, 27);

        body.setArcWidth(10);
        body.setArcHeight(10);
        body.setFill(color);

        body.setStroke(Color.WHITE);
        body.setStrokeWidth(1.5);
        
        //number to identifiy the car
        numberLabel = new Label(String.valueOf(id));
        numberLabel.setTextFill(Color.WHITE);
        numberLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        
        view = new StackPane(body, numberLabel);
        view.setPrefSize(55, 27);
        view.setMinSize(55, 27);
        view.setMaxSize(55, 27);
    }
    
    //the getters

    public int getId() {
        return id;
    }

    public double getSpeed() {
        return speed;
    }

    public double getDistanceTravelled() {
        return distanceTravelled;
    }

    public boolean isFinished() {
        return finished;
    }

    public StackPane getView() {
        return view;
    }

    public Color getColor() {
        return color;
    }

    //the setters
    
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setDistanceTravelled(double distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
    
    public void reset() {
        distanceTravelled = 0;
        finished = false;
        view.setLayoutX(10);
    }
    
    //the progress on the race
    public double getProgress(double totalDistance) {
        if (totalDistance <= 0) {
            return 0;
        }
        return Math.min(100, (distanceTravelled / totalDistance) * 100);
    }
    
}