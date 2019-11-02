package com.grape.lab.controller;

import com.grape.lab.dao.BookDao;
import com.grape.lab.dao.LibraryDao;
import com.grape.lab.model.Book;
import com.grape.lab.model.Library;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.IntegerValidator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AddLibraryDialogController {

    private LibraryDao libraryDao;
    private BookDao bookDao;

    @FXML
    private JFXComboBox<String> bookComboBox;

    @FXML
    private JFXTextField quantityField;

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
            Book book = bookDao.findByNameIgnoreCase(selectedItem).get(0);
            Library libraryToAdd = Library.builder()
                    .book(book)
                    .quantity(Integer.valueOf(quantityField.getText()))
                    .build();
            libraryDao.add(libraryToAdd);
            targetList.clear();
            parentController.loadLibraries();
            stage.close();
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
}
