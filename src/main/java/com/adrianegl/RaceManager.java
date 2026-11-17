/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.adrianegl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author adria
 */


public class RaceManager {
    private final Pane roadPane;
    private Pane miniMapPane;

    private final List<Car> cars = new ArrayList<>();
    private final List<Car> finishOrder = new ArrayList<>();
    private final List<Rectangle> roadLines = new ArrayList<>();
    private final List<Circle> mapDots = new ArrayList<>();

    private AnimationTimer animationTimer;
    private Consumer<List<Car>> onRaceFinished;

    private double totalDistance = 500;
    private double simulationSpeed = 1.0;
    private double roadOffset = 0;
    private long lastUpdate = 0;
    private boolean running = false;

    private static final double SCROLL_SPEED = 120;   // the speed to simulate motion on the road
    private static final double MAP_MARGIN = 30;      // the space with the map
    private static final double MAP_BAR_Y = 14;
    private static final double MAP_BAR_HEIGHT = 14;

    private final double carWidth = 55;
    private final double carHeight = 27;
    private final double startPosition = 10;
    private final double segmentWidth = 35;
    private final double segmentGap = 25;
    
    public RaceManager(Pane roadPane) {
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(roadPane.widthProperty());
        clip.heightProperty().bind(roadPane.heightProperty());
        roadPane.setClip(clip);
        
        initializeAnimation();
        
        
    }   
    
    private void initalizeAnimation() {

}
