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
        
        roadPane.widthProperty().addListener((obs, o, n) -> rebuildRoad());
        roadPane.heightProperty().addListener((obs, o, n) -> rebuildRoad());
    }   
    
    public void setMiniMapPane(Pane pane) {
        this.miniMapPane = pane;
        pane.widthProperty().addListener((obs, o, n) -> rebuildRoad());
        rebuildMiniMap();
    }
    
    public void setOnRaceFinished(Consumer<List<Car>> callback) {
        this.onRaceFinished = callback;
    }
    
    public void setCars(List<Car> newCars) {
        cars.clear();
        cars.addAll(newCars);
        finishOrder.clear();
        rebuildRoad();
        rebuildMiniMap();
    }
    
    public void setTotalDistance(double distance) {
        if (distance > 0) {
            totalDistance = distance;
            rebuildMiniMap();
        }
    }
    
    public void setSimulationSpeed(double speed) {
        simulationSpeed = Math.max(0, speed);
    }
    
    public boolean isRunning() {
        return running;
    }
    
    
    private void initializeAnimation() {
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }
                double deltaTime = (now - lastUpdate) / 1_000_000_000.0;
                lastUpdate = now;
                deltaTime = Math.min(deltaTime, 0.1);
                
                if (!running) {
                    return;
                }
                double adjustedTime = deltaTime * simulationSpeed;
                updateCars(adjustedTime);
                updateRoadLines(adjustedTime);
            }
        };
                
    }
    
    public void start() {
        if (running || cars.isEmpty()) {
            return;
        }
        boolean allFinished = cars.stream().allMatch(Car::isFinished);
        if (allFinished) {
            return;
        }
        running = true;
        lastUpdate = 0;
        animationTimer.start();
    }
    
    public void pause() {
        running = false;
        if (animationTimer != null) {
            animationTimer.stop();
        }
        lastUpdate = 0;
    }
    
    public void reset() {
        pause();
        roadOffset = 0;
        finishOrder.clear();
        for (Car car : cars) {
            car.reset();
        }
        rebuildRoad();
        rebuildMiniMap();
    }

    private void updateCars(double deltaTime) {
        boolean allFinished = true;
        double availableWidth = Math.max(1, roadPane.getWidth() - carWidth - startPosition);

        for (Car car : cars) {
            if (car.isFinished()) {
                continue;
            }

            double speedMS = car.getSpeed() / 3.6; // km/h -> m/s
            double newDistance = Math.min(
                    car.getDistanceTravelled() + speedMS * deltaTime,
                    totalDistance);
            car.setDistanceTravelled(newDistance);

            double progress = newDistance / totalDistance;
            car.getView().setLayoutX(startPosition + progress * availableWidth);

            if (newDistance >= totalDistance) {
                car.setFinished(true);
                finishOrder.add(car); // guarda el orden de llegada
            } else {
                allFinished = false;
            }
        }

        updateMiniMap();

        if (allFinished && !cars.isEmpty()) {
            pause();
            if (onRaceFinished != null) {
                onRaceFinished.accept(new ArrayList<>(finishOrder));
            }
        }
    }



    
    public void rebuildRoad() {
        
    }
    
    public void rebuildMiniMap() {
        
    }

}
