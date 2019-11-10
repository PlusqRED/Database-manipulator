package com.grape.lab.controller;

import com.grape.lab.dao.BookDao;
import com.grape.lab.dao.LibraryDao;
import com.grape.lab.model.Book;
import com.grape.lab.model.Library;
import com.grape.lab.util.ActionLogger;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.IntegerValidator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class AddLibraryDialogController {
    private ActionLogger actionLogger;
    private LibraryDao libraryDao;
    private BookDao bookDao;

    @FXML
    private JFXComboBox<String> bookComboBox;

    @FXML
    private JFXTextField quantityField;

    @FXML
    private JFXSpinner spinner;

    private ObservableList<Library> targetList;
    private Stage stage;
    private MainController parentController;

    public void initialize() {
        IntegerValidator integerValidator = new IntegerValidator();
        integerValidator.setMessage("Required integer!");
        quantityField.getValidators().add(integerValidator);
    }

    @FXML
    void add() {
        if (!bookComboBox.getSelectionModel().isEmpty() && quantityField.validate()) {
            String selectedItem = bookComboBox.getSelectionModel().getSelectedItem();
            CompletableFuture.supplyAsync(() -> {
                showingSpinner(true);
                return bookDao.findByNameIgnoreCase(selectedItem).get(0);
            })
                    .thenApply(book -> Library.builder()
                            .book(book)
                            .quantity(Integer.valueOf(quantityField.getText()))
                            .build())
                    .thenAccept(libraryDao::add)
                    .thenRun(targetList::clear)
                    .thenRun(parentController::loadLibraries)
                    .thenRun(() -> showingSpinner(false))
                    .thenRun(() -> Platform.runLater(stage::close))
                    .thenRun(() -> actionLogger.log("Library added"));
        }
    }

    @SneakyThrows
    private void showingSpinner(boolean showing) {
        if (showing) {
            spinner.setVisible(true);
        } else {
            spinner.setVisible(false);
            Thread.sleep(200);
        }
    }

    public void setTargetList(ObservableList<Library> items) {
        this.targetList = items;
        bookComboBox.setItems(FXCollections.observableList(bookDao.findAll().stream()
                .map(Book::getName)
                .collect(Collectors.toList())));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setParentController(MainController mainController) {
        this.parentController = mainController;
    }

    @Autowired
    public void setLibraryDao(LibraryDao libraryDao) {
        this.libraryDao = libraryDao;
    }

    @Autowired
    public void setBookDao(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    @Autowired
    public void setActionLogger(ActionLogger actionLogger) {
        this.actionLogger = actionLogger;
    }
}
