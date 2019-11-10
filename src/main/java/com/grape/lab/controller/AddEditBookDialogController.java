package com.grape.lab.controller;

import com.grape.lab.dao.BookDao;
import com.grape.lab.model.Book;
import com.grape.lab.util.ActionLogger;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.IntegerValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Component
public class AddEditBookDialogController {
    private ActionLogger actionLogger;
    private Action action;
    private BookDao bookDao;
    @FXML
    private JFXTextField nameField;
    @FXML
    private JFXTextField yearField;
    @FXML
    private JFXTextField authorField;
    @FXML
    private JFXTextField ratingField;
    @FXML
    private JFXButton button;
    @FXML
    private JFXSpinner spinner;
    private ObservableList<Book> targetList;
    private Stage stage;
    private Book selectedBook;
    private MainController parentController;

    public void initialize() {
        Stream.of(nameField, yearField, authorField, ratingField).forEach(this::setRequiredFieldValidator);
        Stream.of(yearField, ratingField).forEach(this::setIntegerFieldValidator);
    }

    private void setIntegerFieldValidator(JFXTextField jfxTextField) {
        IntegerValidator integerValidator = new IntegerValidator();
        integerValidator.setMessage("Required integer value!");
        jfxTextField.getValidators().add(integerValidator);
    }

    private void setRequiredFieldValidator(JFXTextField jfxTextField) {
        RequiredFieldValidator rfv = new RequiredFieldValidator();
        rfv.setMessage("Required field!");
        jfxTextField.getValidators().add(rfv);
        jfxTextField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                jfxTextField.validate();
            }
        });
    }

    @FXML
    void action() {
        if (nameField.validate()
                && yearField.validate()
                && authorField.validate()
                && ratingField.validate()
        ) {
            Book builderBook = Book.builder()
                    .name(nameField.getText())
                    .year(Integer.valueOf(yearField.getText()))
                    .author(authorField.getText())
                    .rating(Integer.valueOf(ratingField.getText()))
                    .build();
            switch (action) {
                case ADD: {
                    CompletableFuture
                            .runAsync(() -> showingSpinner(true))
                            .thenRun(() -> bookDao.add(builderBook))
                            .thenRun(() -> Platform.runLater(targetList::clear))
                            .thenRun(() -> Platform.runLater(parentController::loadData))
                            .thenRun(() -> showingSpinner(false))
                            .thenRun(() -> Platform.runLater(stage::close))
                            .thenRun(() -> actionLogger.log("Book added"));
                    break;
                }
                case EDIT: {
                    CompletableFuture
                            .runAsync(() -> showingSpinner(true))
                            .thenRunAsync(() -> bookDao.delete(selectedBook.getId()))
                            .thenRunAsync(() -> bookDao.add(builderBook))
                            .thenRun(() -> Platform.runLater(targetList::clear))
                            .thenRun(() -> Platform.runLater(parentController::loadData))
                            .thenRun(() -> showingSpinner(false))
                            .thenRun(() -> Platform.runLater(stage::close))
                            .thenRun(() -> actionLogger.log("Book edited"));
                    break;
                }
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    @SneakyThrows
    private void showingSpinner(boolean showing) {
        if (showing) {
            Platform.runLater(() -> spinner.setVisible(true));
        } else {
            Platform.runLater(() -> spinner.setVisible(false));
            Thread.sleep(200);
        }
    }

    public void setButtonText(String text) {
        button.setText(text);
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setTargetList(ObservableList<Book> items) {
        this.targetList = items;
    }

    public void setEditBook(Book selectedItem) {
        this.selectedBook = selectedItem;
        this.nameField.setText(selectedItem.getName());
        this.yearField.setText(selectedItem.getYear().toString());
        this.authorField.setText(selectedItem.getAuthor());
        this.ratingField.setText(selectedItem.getRating().toString());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setParentController(MainController mainController) {
        this.parentController = mainController;
    }

    @Autowired
    public void setBookDao(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    @Autowired
    public void setActionLogger(ActionLogger actionLogger) {
        this.actionLogger = actionLogger;
    }

    public enum Action {
        ADD,
        EDIT
    }
}
