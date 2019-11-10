package com.grape.lab.controller;

import com.grape.lab.dao.BookDao;
import com.grape.lab.dao.LibraryDao;
import com.grape.lab.model.Book;
import com.grape.lab.model.Library;
import com.grape.lab.util.ActionLogger;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Component
public class MainController {
    private ActionLogger actionLogger;
    private final BookDao bookDao;
    private final LibraryDao libraryDao;
    private AddEditBookDialogController addEditBookDialogController;
    private AddLibraryDialogController addLibraryDialogController;
    private boolean reversedCustomSort;

    @FXML
    private TableView<Book> bookTableView;

    @FXML
    private JFXTextField bookSearchField;

    @FXML
    private TableView<Library> libraryTableView;

    public MainController(BookDao bookDao, LibraryDao libraryDao) {
        this.bookDao = bookDao;
        this.libraryDao = libraryDao;
    }

    public void initialize() {
        prepareBookColumns();
        prepareLibraryColumns();
        loadData();
        loadLibraries();
    }

    public void customSort() {
        ObservableList<Book> itemsToSort = bookTableView.getItems();
        Comparator<Book> comparator = comparing(Book::getName)
                .thenComparing(Book::getYear);
        if (reversedCustomSort) {
            comparator = comparator.reversed();
        }
        reversedCustomSort = !reversedCustomSort;
        itemsToSort.setAll(itemsToSort.sorted(comparator));
    }

    private void prepareLibraryColumns() {
        libraryTableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        libraryTableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("bookName"));
        libraryTableView.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("bookAuthor"));
        libraryTableView.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("quantity"));
    }

    public void loadData() {
        List<Book> all = bookDao.findAll();
        bookTableView.getItems().addAll(all);
    }

    private void prepareBookColumns() {
        bookTableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        bookTableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        bookTableView.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("year"));
        bookTableView.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("author"));
        bookTableView.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("rating"));
    }

    @FXML
    void bookAdd() throws IOException {
        val loader = loadBookAddEditDialog();
        loader.setController(addEditBookDialogController);
        Parent dialogContent = loader.load();
        AddEditBookDialogController controller = loader.getController();
        controller.setTargetList(bookTableView.getItems());
        controller.setButtonText("Добавить");
        Stage stage = new Stage();
        controller.setAction(AddEditBookDialogController.Action.ADD);
        controller.setStage(stage);
        controller.setParentController(this);
        stage.setTitle("Добавление книги");
        stage.setScene(new Scene(dialogContent));
        stage.showAndWait();
    }

    @SneakyThrows
    private FXMLLoader loadBookAddEditDialog() {
        return new FXMLLoader(getClass()
                .getClassLoader()
                .getResource("view/add_edit_book_dialog.fxml")
        );
    }

    @FXML
    @SneakyThrows
    void bookEdit() {
        if (!bookTableView.getSelectionModel().isEmpty()) {
            val loader = loadBookAddEditDialog();
            loader.setController(addEditBookDialogController);
            Parent dialogContent = loader.load();
            AddEditBookDialogController controller = loader.getController();
            controller.setTargetList(bookTableView.getItems());
            controller.setButtonText("Редактировать");
            controller.setEditBook(bookTableView.getSelectionModel().getSelectedItem());
            Stage stage = new Stage();
            controller.setAction(AddEditBookDialogController.Action.EDIT);
            controller.setParentController(this);
            controller.setStage(stage);
            stage.setTitle("Редактирование книги");
            stage.setScene(new Scene(dialogContent));
            stage.showAndWait();
        }
    }

    @FXML
    void bookRemove() {
        if (!bookTableView.getSelectionModel().isEmpty()) {
            Integer selectedId = bookTableView.getSelectionModel().getSelectedItem().getId();
            CompletableFuture.runAsync(() -> bookDao.delete(selectedId))
                    .thenRun(() -> Platform.runLater(bookTableView.getItems()::clear))
                    .thenRun(() -> Platform.runLater(this::loadData))
                    .thenRun(() -> actionLogger.log("Book removed"));
        }
    }

    @FXML
    @SneakyThrows
    void bookSaveToFile() {
        List<Book> books = new ArrayList<>(bookTableView.getItems());
        String header = bookTableView.getColumns().stream()
                .map(TableColumnBase::getText)
                .collect(Collectors.joining(";"));
        PrintWriter writer = new PrintWriter(new FileOutputStream("books.csv"), true);
        writer.println(header);
        books.forEach(book -> printBook(writer, book));
        actionLogger.log("Books saved to file");
    }

    private void printBook(PrintWriter writer, Book book) {
        String line = String.join(";",
                String.valueOf(book.getId()),
                book.getName(),
                String.valueOf(book.getYear()),
                book.getAuthor(),
                String.valueOf(book.getRating()));
        writer.println(line);
    }

    @FXML
    void bookSearchByName() {
        String searchText = bookSearchField.getText().trim();
        ObservableList<Book> items = bookTableView.getItems();
        if (!searchText.isEmpty()) {
            items.clear();
            CompletableFuture.supplyAsync(() -> bookDao.findByNameIgnoreCase(searchText))
                    .thenAccept(items::addAll)
                    .thenRun(() -> actionLogger.log("Book searched"));
        } else {
            items.clear();
            loadData();
        }
    }

    @FXML
    @SneakyThrows
    void libraryAdd() {
        val loader = new FXMLLoader(getClass()
                .getClassLoader()
                .getResource("view/add_library_dialog.fxml"));
        loader.setController(addLibraryDialogController);
        Parent dialogContent = loader.load();
        AddLibraryDialogController controller = loader.getController();
        controller.setTargetList(libraryTableView.getItems());
        Stage stage = new Stage();
        controller.setStage(stage);
        controller.setParentController(this);
        stage.setTitle("Добавление книг в библиотеку");
        stage.setScene(new Scene(dialogContent));
        stage.showAndWait();
    }

    public void loadLibraries() {
        List<Library> libraries = libraryDao.findAll();
        libraryTableView.getItems().addAll(libraries);
    }

    public void libraryDelete() {
        if (!libraryTableView.getSelectionModel().isEmpty()) {
            Library selectedItem = libraryTableView.getSelectionModel().getSelectedItem();
            CompletableFuture.runAsync(() -> libraryDao.delete(selectedItem))
                    .thenRun(libraryTableView.getItems()::clear)
                    .thenRun(this::loadLibraries)
                    .thenRun(() -> actionLogger.log("Library deleted"));
        }
    }

    @Autowired
    public void setAddEditBookDialogController(AddEditBookDialogController addEditBookDialogController) {
        this.addEditBookDialogController = addEditBookDialogController;
    }

    @Autowired
    public void setAddLibraryDialogController(AddLibraryDialogController addLibraryDialogController) {
        this.addLibraryDialogController = addLibraryDialogController;
    }

    @Autowired
    public void setActionLogger(ActionLogger actionLogger) {
        this.actionLogger = actionLogger;
    }
}
