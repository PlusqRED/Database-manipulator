package com.grape.lab.controller;

import com.grape.lab.Main;
import com.grape.lab.service.UserService;
import com.grape.lab.service.impl.DefaultUserService;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.base.IFXLabelFloatControl;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AuthController {
    @FXML
    private JFXTextField login_tf;

    @FXML
    private JFXPasswordField password_tf;

    @FXML
    private Label authErrorLabel;

    private UserService userService;
    private Stage parentStage;

    public void initialize() {
        Stream.of(login_tf, password_tf).map(tf -> (IFXLabelFloatControl) tf).forEach(this::addRequiredFieldValidator);
        setListener(login_tf);
        setListenerToPassword(password_tf);
    }

    private void setListenerToPassword(JFXPasswordField textField) {
        textField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                textField.validate();
            }
        });
    }

    private void setListener(JFXTextField textField) {
        textField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                textField.validate();
            }
        });
    }

    private void addRequiredFieldValidator(IFXLabelFloatControl jfxTextField) {
        RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
        requiredFieldValidator.setMessage("Required field!");
        jfxTextField.getValidators().add(requiredFieldValidator);
    }

    @FXML
    void signIn() {
        if (login_tf.validate() && password_tf.validate()) {
            String login = login_tf.getText();
            String password = password_tf.getText();
            DefaultUserService.SignInResult signInResult = userService.signIn(login, password);
            if (signInResult == DefaultUserService.SignInResult.OK) {
                authErrorLabel.setVisible(false);
                parentStage.close();
                loadMainFrame();
            } else {
                authErrorLabel.setText(signInResult.getErrorMessage());
                authErrorLabel.setVisible(true);
            }
        }
    }

    @SneakyThrows
    private void loadMainFrame() {
        val loader = new FXMLLoader(getClass()
                .getClassLoader()
                .getResource("view/main.fxml")
        );
        Stage stage = new Stage();
        MainController mainController = Main.ctx.getBean("mainController", MainController.class);
        mainController.setParentStage(stage);
        loader.setController(mainController);
        stage.setTitle("Управление библиотекой");
        stage.setScene(new Scene(loader.load()));
        stage.showAndWait();
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setParentStage(Stage parentStage) {
        this.parentStage = parentStage;
    }
}
