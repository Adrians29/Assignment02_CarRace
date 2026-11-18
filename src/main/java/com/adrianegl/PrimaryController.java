package com.adrianegl;


import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class PrimaryController {

    @FXML private BorderPane rootPane;
    @FXML private Pane roadPane;
    @FXML private Pane miniMapPane;
    @FXML private VBox carControlsContainer;

    @FXML private Button addCarButton;
    @FXML private Button removeCarButton;
    @FXML private Button startButton;
    @FXML private Button pauseButton;
    @FXML private Button resetButton;

    @FXML private Slider distanceSlider;
    @FXML private Slider simulationSpeedSlider;

    @FXML private Label distanceValueLabel;
    @FXML private Label simulationSpeedValueLabel;
    @FXML private Label statusLabel;

    // ---- Datos ----
    private final List<Car> cars = new ArrayList<>();
    private RaceManager raceManager;

    private static final int MAX_CARS = 4;
    private static final int MIN_CARS = 1;

    private final Color[] carColors = {
        Color.DODGERBLUE,
        Color.CRIMSON,
        Color.LIMEGREEN,
        Color.ORANGE
    };

    @FXML
    public void initialize() {
        raceManager = new RaceManager(roadPane);
        raceManager.setMiniMapPane(miniMapPane);
        raceManager.setOnRaceFinished(this::showResults);
        distanceSlider.setMin(100);
        distanceSlider.setMax(2000);
        distanceSlider.setValue(500);
        distanceValueLabel.setText("500 m");
        raceManager.setTotalDistance(500);

        distanceSlider.valueProperty().addListener((obs, oldV, newV) -> {
            double distance = newV.doubleValue();
            distanceValueLabel.setText(String.format("%.0f m", distance));
            raceManager.setTotalDistance(distance);
        });
        simulationSpeedSlider.setMin(0.25);
        simulationSpeedSlider.setMax(2.0);
        simulationSpeedSlider.setValue(1.0);
        simulationSpeedValueLabel.setText("1.0x");
        raceManager.setSimulationSpeed(1.0);

        simulationSpeedSlider.valueProperty().addListener((obs, oldV, newV) -> {
            double speed = newV.doubleValue();
            simulationSpeedValueLabel.setText(String.format("%.1fx", speed));
            raceManager.setSimulationSpeed(speed);
        });
        addCar();
        statusLabel.setText("Ready to race");
    }


    @FXML
    private void handleAddCar() {
        if (cars.size() >= MAX_CARS) {
            statusLabel.setText("Maximum of 4 cars reached");
            return;
        }
        addCar();
        statusLabel.setText("Car added");
    }

    private void addCar() {
        int id = cars.size() + 1;
        Color color = carColors[cars.size()];
        Car car = new Car(id, 80, color);

        cars.add(car);
        createCarControls(car);
        raceManager.setCars(cars);
        updateControlsState();
    }

    @FXML
    private void handleRemoveCar() {
        if (cars.size() <= MIN_CARS) {
            statusLabel.setText("At least one car is required");
            return;
        }
        cars.remove(cars.size() - 1);

        // Quita la última fila de controles
        int last = carControlsContainer.getChildren().size() - 1;
        carControlsContainer.getChildren().remove(last);

        raceManager.setCars(cars);
        updateControlsState();
        statusLabel.setText("Car removed");
    }

    private void createCarControls(Car car) {
        HBox controlBox = new HBox(12);
        controlBox.setAlignment(Pos.CENTER_LEFT);
        controlBox.setStyle(
            "-fx-padding: 4 8 4 8;" +
            "-fx-background-color: #E2E8F0;" +
            "-fx-background-radius: 8;"
        );

        Label carLabel = new Label("Car " + car.getId());
        carLabel.setMinWidth(65);
        carLabel.setTextFill(car.getColor());
        carLabel.setStyle("-fx-font-weight: bold;");

        Slider speedSlider = new Slider(0, 200, car.getSpeed());
        speedSlider.setMajorTickUnit(50);
        speedSlider.setShowTickMarks(true);
        HBox.setHgrow(speedSlider, Priority.ALWAYS);

        Label speedLabel = new Label(String.format("%.0f km/h", car.getSpeed()));
        speedLabel.setMinWidth(85);

        speedSlider.valueProperty().addListener((obs, oldV, newV) -> {
            double speed = newV.doubleValue();
            car.setSpeed(speed);
            speedLabel.setText(String.format("%.0f km/h", speed));
        });

        controlBox.getChildren().addAll(carLabel, speedSlider, speedLabel);
        carControlsContainer.getChildren().add(controlBox);
    }


    @FXML
    private void handleStart() {
        if (cars.isEmpty()) {
            return;
        }
        if (cars.stream().allMatch(Car::isFinished)) {
            statusLabel.setText("Race finished. Press Reset to race again.");
            return;
        }
        raceManager.start();
        statusLabel.setText("Race in progress");
        updateControlsState();
    }

    @FXML
    private void handlePause() {
        raceManager.pause();
        statusLabel.setText("Race paused");
        updateControlsState();
    }

    @FXML
    private void handleReset() {
        raceManager.reset();
        statusLabel.setText("Race reset");
        updateControlsState();
    }

    private void showResults(List<Car> finishOrder) {
        StringBuilder sb = new StringBuilder();
        sb.append("Race finished! Winner: Car ").append(finishOrder.get(0).getId()).append("   |   ");
        for (int i = 0; i < finishOrder.size(); i++) {
            sb.append(i + 1).append(". Car ").append(finishOrder.get(i).getId()).append("  ");
        }
        statusLabel.setText(sb.toString().trim());
        updateControlsState();
    }


    private void updateControlsState() {
        boolean locked = raceManager.isRunning()
                || cars.stream().anyMatch(c -> c.getDistanceTravelled() > 0);

        addCarButton.setDisable(locked || cars.size() >= MAX_CARS);
        removeCarButton.setDisable(locked || cars.size() <= MIN_CARS);
        distanceSlider.setDisable(locked);
    }
}
