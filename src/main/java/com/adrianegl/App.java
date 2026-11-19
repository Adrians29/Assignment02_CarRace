package com.adrianegl;

import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader splashLoader = new FXMLLoader(App.class.getResource("splash.fxml"));
        Parent splashRoot = splashLoader.load();
        SplashController splash = splashLoader.getController();

        Scene scene = new Scene(splashRoot, 1100, 750);
        scene.getStylesheets().add(
            App.class.getResource("styles.css").toExternalForm()
        );

        stage.setTitle("Car Racing Simulator");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        splash.play(() -> {
            try {
                Parent mainRoot = FXMLLoader.load(App.class.getResource("primary.fxml"));
                mainRoot.setOpacity(0);
                scene.setRoot(mainRoot);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(400), mainRoot);
                fadeIn.setToValue(1);
                fadeIn.play();
            } catch (IOException e) {
                throw new RuntimeException("No se pudo cargar primary.fxml", e);
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
