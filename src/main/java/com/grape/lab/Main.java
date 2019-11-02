package com.grape.lab;

import com.grape.lab.config.AppConfiguration;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static ApplicationContext ctx;

    public static void main(String[] args) {
        ctx = new AnnotationConfigApplicationContext(AppConfiguration.class);
        new Main().launch();
    }

    @SneakyThrows
    private void launch() {
        MainApplication.launch(MainApplication.class);
    }
}
