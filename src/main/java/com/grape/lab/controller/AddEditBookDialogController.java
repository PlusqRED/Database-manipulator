package com.grape.lab.controller;

import com.grape.lab.dao.BookDao;
import com.grape.lab.model.Book;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.IntegerValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AddEditBookDialogController {
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
                case ADD:
                    add(builderBook);
                    break;
                case EDIT:
                    edit(builderBook);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            parentController.loadData();
            stage.close();
        }
    }

    private void edit(Book builderBook) {
        bookDao.delete(selectedBook.getId());
        bookDao.add(builderBook);
        targetList.clear();
    }

    private void add(Book builderBook) {
        bookDao.add(builderBook);
        targetList.clear();
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

    public enum Action {
        ADD,
        EDIT
    }
}
