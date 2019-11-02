package com.grape.lab;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("view/main.fxml"));
        fxmlLoader.setController(Main.ctx.getBean("mainController"));
        primaryStage.setScene(new Scene(fxmlLoader.load()));
        primaryStage.setTitle("Database Manipulator");
        primaryStage.show();
    }

}
