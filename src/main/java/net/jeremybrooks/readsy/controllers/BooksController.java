/*
 * readsy - read something new every day <http://jeremybrooks.net/readsy>
 *
 * Copyright (c) 2013-2025  Jeremy Brooks
 *
 * This file is part of readsy.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.jeremybrooks.readsy.controllers;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import net.jeremybrooks.readsy.ActiveState;
import net.jeremybrooks.readsy.BitHelper;
import net.jeremybrooks.readsy.BookUtils;
import net.jeremybrooks.readsy.Formatters;
import net.jeremybrooks.readsy.MapperFactory;
import net.jeremybrooks.readsy.gui.BookCell;
import net.jeremybrooks.readsy.model.AppModel;
import net.jeremybrooks.readsy.model.Book;
import net.jeremybrooks.readsy.model.Page;
import net.jeremybrooks.readsy.workers.RefreshBooksWorker;
import net.jeremybrooks.readsy.workers.SaveConfigWorker;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BooksController {
    private static final Logger logger = LogManager.getLogger();

    private final AppModel appModel;
    @FXML
    private ListView<Book> bookList;
    @FXML private SplitPane splitPane;
    @FXML private Label lblDate;
    @FXML private Label lblHeading;
    @FXML private TextArea txtText;
    @FXML private CheckBox cbxRead;
    @FXML private Button btnPreviousDay;
    @FXML private Button btnToday;
    @FXML private Button btnNextDay;
    private final ObjectProperty<Page> pageObjectProperty = new SimpleObjectProperty<>();

    private final ObservableList<Book> books = FXCollections.observableArrayList();

    public BooksController(AppModel appModel) {
        this.appModel = appModel;
        appModel.getStage().setTitle("Readsy - " + appModel.getVersion());
        appModel.getStage().setOnHiding(event -> {
            // Handle the close event
            appModel.getConfiguration().setWindowX(appModel.getStage().xProperty().intValue());
            appModel.getConfiguration().setWindowY(appModel.getStage().yProperty().intValue());
            appModel.getConfiguration().setWindowWidth(appModel.getStage().widthProperty().intValue());
            appModel.getConfiguration().setWindowHeight(appModel.getStage().heightProperty().intValue());
            appModel.getConfiguration().setSplitPanePosition(splitPane.getDividers().getFirst().getPosition());
            new SaveConfigWorker(appModel);
        });
    }

    @FXML
    public void initialize() {
        bookList.setItems(books);
        bookList.setCellFactory(lv -> new BookCell());
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            executor.execute(new RefreshBooksWorker(books, appModel));
        }

        pageObjectProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                lblHeading.setText("");
                txtText.setText("");
                lblDate.setText("");
                cbxRead.setSelected(false);
                cbxRead.setDisable(true);
            } else {
                lblHeading.setText(newValue.getHeading());
                txtText.setText(newValue.getText());
                Book book = bookList.getSelectionModel().getSelectedItem();
                lblDate.setText(Formatters.fullDateFormatter.format(book.getPageDate()));
                if (BookUtils.isPageDateInReadingRange(book)) {
                    cbxRead.setDisable(false);
                    BitHelper bh = new BitHelper(book.getStatusFlags());
                    cbxRead.setSelected(bh.isRead(BookUtils.getDayOfReadingYear(book)));
                } else {
                    cbxRead.setSelected(false);
                    cbxRead.setDisable(true);
                }
            }
        });
        bookList.getSelectionModel().selectFirst();
        splitPane.getDividers().getFirst().setPosition(appModel.getConfiguration().getSplitPanePosition());
        loadPageForSelectedBook();
    }

    @FXML public void previousDay() {
        Book book = bookList.getSelectionModel().getSelectedItem();
        book.setPageDate(book.getPageDate().minusDays(1));
        loadPageForSelectedBook();
    }
    @FXML public void today() {
        Book book = bookList.getSelectionModel().getSelectedItem();
        book.setPageDate(LocalDate.now());
        loadPageForSelectedBook();
    }
    @FXML public void nextDay() {
        Book book = bookList.getSelectionModel().getSelectedItem();
        book.setPageDate(book.getPageDate().plusDays(1));
        loadPageForSelectedBook();
    }

    @FXML public void bookListClicked() {
        loadPageForSelectedBook();
    }

    private void loadPageForSelectedBook() {
        Book book = bookList.getSelectionModel().getSelectedItem();
        Page page;
        if (BookUtils.isPageDateInReadingRange(book)) {
            int day = BookUtils.getDayOfReadingYear(book);
            Path bookDir = Paths.get(book.getBookPath()).getParent();
            Path pagePath = Paths.get(bookDir.toString(), String.format("%d.json", day));
            try {
                page = MapperFactory.getObjectMapper()
                        .readValue(Files.newInputStream(pagePath), Page.class);
            } catch (IOException ioe) {
                page = new Page();
                page.setHeading("Error");
                page.setText(String.format("There was an error reading the page.%n%n%s", ioe.getMessage()));
            }
        } else {
           page = new Page();
           page.setHeading("Outside Reading Year");
           page.setText(String.format("The requested date of %s is outside the reading year for this book%n%nThis book is scheduled to be read from %s through %s.",
                   Formatters.longMonthAndDayAndYearFormatter.format(book.getPageDate()),
                   Formatters.longMonthAndDayAndYearFormatter.format(LocalDate.parse(book.getReadingStartDate(), Formatters.shortISOFormatter)),
                   Formatters.longMonthAndDayAndYearFormatter.format(LocalDate.parse(book.getReadingEndDate(), Formatters.shortISOFormatter))));
        }
        pageObjectProperty.setValue(page);
    }

    /**
     * Mark a page read or unread when the user clicks the checkbox.
     */
    @FXML public void checkboxToggle() {
        Book book = bookList.getSelectionModel().getSelectedItem();
        int dayOfReadingYear = BookUtils.getDayOfReadingYear(book);
        BitHelper bh = new BitHelper(book.getStatusFlags());
        bh.setRead(dayOfReadingYear, cbxRead.isSelected());
        book.setStatusFlags(bh.toString());
        try {
            Files.write(Paths.get(book.getBookPath()),
                    MapperFactory.getObjectMapper().writeValueAsBytes(book));
            books.clear();
            Platform.runLater(new RefreshBooksWorker(books, appModel));
        } catch (Exception ex) {
            logger.error("Error while writing book {} to file {}", book.getTitle(), book.getBookPath(), ex);
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("There was an error writing data.");
            a.setContentText(String.format("Something went wrong while trying to save reading state.%n" +
                    "Book title: %s%n" +
                    "      File: %s%n" +
                    "     Error: %s%n" +
                    "See the logs for more detail.", book.getTitle(), book.getBookPath(), ex.getMessage()));
            a.showAndWait();
        }
    }

    @FXML
    private void menuOpenEditor() {
        appModel.setActiveState(ActiveState.NEW_BOOK);
    }

    @FXML private void addBook() {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("TODO");
        a.setHeaderText("Add Book");
        a.showAndWait();
    }
    @FXML private void deleteBook() {
        Book book = bookList.getSelectionModel().getSelectedItem();
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Delete Book?");
        a.setHeaderText(String.format("You are about to delete the book %s.%nAre you sure?", book.getTitle()));
        a.showAndWait();
        if (a.getResult() == ButtonType.OK) {
            try {
                FileUtils.deleteDirectory(Paths.get(book.getBookPath()).getParent().toFile());
                pageObjectProperty.setValue(null);
                books.clear();
                Platform.runLater(new RefreshBooksWorker(books, appModel));
            } catch (Exception e) {
                logger.error("Error while deleting book from {}", book.getBookPath(), e);
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("There was an error when trying to delete the book.");
                errorAlert.setContentText(String.format("""
                        Title: %s
                         Path: %s
                        Error: %s
                        See the logs for more detail.""", book.getTitle(), book.getBookPath(), e.getMessage()));
                errorAlert.showAndWait();
            }
        }
    }
    @FXML private void close() {
        appModel.getStage().close();
    }
}