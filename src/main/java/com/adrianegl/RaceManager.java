/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.adrianegl;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

    private static final double SCROLL_SPEED = 120;   
    private static final double MAP_MARGIN = 30;      
    private static final double MAP_BAR_Y = 14;
    private static final double MAP_BAR_HEIGHT = 14;

    private final double carWidth = Car.WIDTH;
    private final double carHeight = Car.HEIGHT;
    private final double startPosition = 10;
    private final double segmentWidth = 35;
    private final double segmentGap = 25;

    public RaceManager(Pane roadPane) {
        this.roadPane = roadPane;
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
        pane.widthProperty().addListener((obs, o, n) -> rebuildMiniMap());
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
            rebuildMiniMap(); // actualiza las etiquetas de metros del mapa
        }
    }

    public double getTotalDistance() {
        return totalDistance;
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
                finishOrder.add(car); 
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
        if (roadPane == null) {
            return;
        }
        roadPane.getChildren().clear();
        roadLines.clear();

        int numberOfCars = Math.max(1, cars.size());
        double height = roadPane.getHeight();
        double width = roadPane.getWidth();
        if (height <= 0 || width <= 0) {
            return;
        }

        double laneHeight = height / numberOfCars;
        double cycle = segmentWidth + segmentGap;

        int dividers = Math.max(1, numberOfCars - 1);
        for (int i = 1; i <= dividers; i++) {
            double y = (numberOfCars == 1) ? height / 2 : i * laneHeight;

            for (double x = 0; x < width + cycle; x += cycle) {
                Rectangle line = new Rectangle(segmentWidth, 3);
                line.setFill(Color.WHITE);
                line.setUserData(x);               
                line.setLayoutX(x - roadOffset);
                line.setLayoutY(y - 1.5);
                roadLines.add(line);
                roadPane.getChildren().add(line);
            }
        }

        double availableWidth = Math.max(1, width - carWidth - startPosition);
        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            double centerY = laneHeight * i + laneHeight / 2;
            car.getView().setLayoutY(centerY - carHeight / 2);

            double progress = car.getDistanceTravelled() / totalDistance;
            car.getView().setLayoutX(startPosition + progress * availableWidth);

            roadPane.getChildren().add(car.getView());
        }
    }

    private void updateRoadLines(double deltaTime) {
        double cycle = segmentWidth + segmentGap;

        roadOffset = (roadOffset + SCROLL_SPEED * deltaTime) % cycle;

        for (Rectangle line : roadLines) {
            double baseX = (Double) line.getUserData();
            line.setLayoutX(baseX - roadOffset);
        }
    }

    private void rebuildMiniMap() {
        if (miniMapPane == null) {
            return;
        }
        miniMapPane.getChildren().clear();
        mapDots.clear();

        double w = miniMapPane.getWidth();
        if (w <= 0) {
            return;
        }

        double barWidth = w - 2 * MAP_MARGIN;

        Rectangle bar = new Rectangle(MAP_MARGIN, MAP_BAR_Y, barWidth, MAP_BAR_HEIGHT);
        bar.setArcWidth(8);
        bar.setArcHeight(8);
        bar.setFill(Color.web("#CBD5E1"));
        miniMapPane.getChildren().add(bar);

        for (int i = 0; i <= 4; i++) {
            double x = MAP_MARGIN + barWidth * i / 4.0;

            Line tick = new Line(x, MAP_BAR_Y - 4, x, MAP_BAR_Y + MAP_BAR_HEIGHT + 4);
            tick.setStroke(Color.web("#64748B"));

            Label label = new Label(String.format("%.0f m", totalDistance * i / 4.0));
            label.setPrefWidth(60);
            label.setAlignment(Pos.CENTER);
            label.setLayoutX(x - 30);
            label.setLayoutY(MAP_BAR_Y + MAP_BAR_HEIGHT + 6);
            label.setStyle("-fx-font-size: 11px; -fx-text-fill: #475569;");

            miniMapPane.getChildren().addAll(tick, label);
        }

        for (Car car : cars) {
            Circle dot = new Circle(5, car.getColor());
            dot.setStroke(Color.WHITE);
            mapDots.add(dot);
            miniMapPane.getChildren().add(dot);
        }
        updateMiniMap();
    }

    private void updateMiniMap() {
        if (miniMapPane == null || mapDots.size() != cars.size() || mapDots.isEmpty()) {
            return;
        }
        double barWidth = miniMapPane.getWidth() - 2 * MAP_MARGIN;
        double centerY = MAP_BAR_Y + MAP_BAR_HEIGHT / 2;
        int n = mapDots.size();

        for (int i = 0; i < n; i++) {
            double progress = cars.get(i).getDistanceTravelled() / totalDistance;
            Circle dot = mapDots.get(i);
            dot.setCenterX(MAP_MARGIN + progress * barWidth);
            dot.setCenterY(centerY + (i - (n - 1) / 2.0) * 3);
        }
    }
    
}