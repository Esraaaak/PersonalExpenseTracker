package com.mycompany.personalexpensetrackerr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("/view/Login.fxml"), 520, 420);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    static void setRoot(String fxmlPath) throws IOException {
        scene.setRoot(loadFXML(fxmlPath));
    }

    private static Parent loadFXML(String fxmlPath) throws IOException {
        URL url = App.class.getResource(fxmlPath);

        if (url == null) {
            throw new IOException("FXML file not found: " + fxmlPath);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(url);
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
