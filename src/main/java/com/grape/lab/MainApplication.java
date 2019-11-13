package com.grape.lab;

import com.grape.lab.controller.AuthController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("view/auth.fxml"));
        AuthController authController = Main.ctx.getBean("authController", AuthController.class);
        authController.setParentStage(primaryStage);
        fxmlLoader.setController(authController);
        primaryStage.setScene(new Scene(fxmlLoader.load()));
        primaryStage.setTitle("Авторизация");
        primaryStage.show();
    }

}
