package com.grape.lab.util.impl;

import com.grape.lab.util.ActionLogger;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;

@Component
@PropertySource("classpath:properties/app.properties")
public class FileActionLogger implements ActionLogger {

    @Value("${log.file}")
    private String fileName;

    private PrintWriter writer;

    @SneakyThrows
    @PostConstruct
    private void init() {
        this.writer = new PrintWriter(new FileOutputStream(fileName + "_" + LocalDate.now() + ".txt"), true);
    }

    @Override
    public void log(String message) {
        writer.println(message);
    }

    @PreDestroy
    public void destroy() throws Exception {
        writer.close();
    }
}
