package com.example.compilador;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class compiladorApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(compiladorApplication.class.getResource("compilador.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1360, 690);
        stage.setTitle("Gymterpreter");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}