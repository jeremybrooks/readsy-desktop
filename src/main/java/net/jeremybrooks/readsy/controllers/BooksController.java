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

import com.fasterxml.jackson.databind.ObjectMapper;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import net.jeremybrooks.readsy.ActiveState;
import net.jeremybrooks.readsy.BitHelper;
import net.jeremybrooks.readsy.BookUtils;
import net.jeremybrooks.readsy.Constants;
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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

public class BooksController {
    private static final Logger logger = LogManager.getLogger();

    private final AppModel appModel;
    @FXML
    private ListView<Book> bookList;
    @FXML
    private SplitPane splitPane;
    @FXML
    private Label lblDate;
    @FXML
    private Label lblHeading;
    @FXML
    private TextArea txtText;
    @FXML
    private CheckBox cbxRead;
    @FXML
    private Button btnPreviousDay;
    @FXML
    private Button btnToday;
    @FXML
    private Button btnNextDay;
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
        reloadBooksAndSelect("");

        pageObjectProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                lblHeading.setText("");
                lblDate.setText("");
                txtText.setText("");
            } else {
                lblHeading.setText(newValue.getHeading());
                txtText.setText(newValue.getText());
                Book book = bookList.getSelectionModel().getSelectedItem();
                if (book == null) {
                    lblDate.setText("");
                    cbxRead.setSelected(false);
                    cbxRead.setDisable(true);
                } else {
                    String date = String.format("%s - Day %s/%s",
                            Formatters.fullDateFormatter.format(book.getPageDate()),
                            BookUtils.getDayOfReadingYear(book),
                            BookUtils.getDaysInReadingYear(book));

                    lblDate.setText(date);
                    if (BookUtils.isPageDateInReadingRange(book)) {
                        cbxRead.setDisable(false);
                        BitHelper bh = new BitHelper(book.getStatusFlags());
                        cbxRead.setSelected(bh.isRead(BookUtils.getDayOfReadingYear(book)));
                    } else {
                        cbxRead.setSelected(false);
                        cbxRead.setDisable(true);
                    }
                }
            }
        });
        bookList.getSelectionModel().selectFirst();
        splitPane.getDividers().getFirst().setPosition(appModel.getConfiguration().getSplitPanePosition());
        loadPageForSelectedBook();
    }

    @FXML
    public void previousDay() {
        Book book = bookList.getSelectionModel().getSelectedItem();
        book.setPageDate(book.getPageDate().minusDays(1));
        loadPageForSelectedBook();
    }

    @FXML
    public void today() {
        Book book = bookList.getSelectionModel().getSelectedItem();
        book.setPageDate(LocalDate.now());
        loadPageForSelectedBook();
    }

    @FXML
    public void nextDay() {
        Book book = bookList.getSelectionModel().getSelectedItem();
        book.setPageDate(book.getPageDate().plusDays(1));
        loadPageForSelectedBook();
    }

    @FXML
    public void bookListClicked() {
        loadPageForSelectedBook();
    }

    private void reloadBooksAndSelect(String selectedBookTitle) {
        books.clear();
        Platform.runLater(new RefreshBooksWorker(books, appModel));
        if (selectedBookTitle != null && !selectedBookTitle.isEmpty()) {
            Platform.runLater(() -> {
                // select the new book
                Optional<Book> bookToSelect = bookList.getItems()
                        .stream()
                        .filter(book -> book.getTitle().equals(selectedBookTitle))
                        .findFirst();
                bookToSelect.ifPresent(book -> {
                    bookList.getSelectionModel().select(book);
                });
            });
        } else if (selectedBookTitle != null) {
            Platform.runLater(() -> {
                bookList.getSelectionModel().selectFirst();
            });
        }
        Platform.runLater(this::loadPageForSelectedBook);
    }

    private void loadPageForSelectedBook() {
        Page page;
        if (bookList.getItems().isEmpty()) {
            page = new Page();
            page.setHeading("Your library is empty.");
            page.setText("""
                    You can find books to read by clicking Help -> Get Books.
                    
                    Once you have downloaded a book, add it to your library by clicking File -> Add Book.""");
        } else {
            Book book = bookList.getSelectionModel().getSelectedItem();
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
        }
        pageObjectProperty.setValue(page);
    }

    /**
     * Mark a page read or unread when the user clicks the checkbox.
     */
    @FXML
    public void checkboxToggle() {
        Book book = bookList.getSelectionModel().getSelectedItem();
        int dayOfReadingYear = BookUtils.getDayOfReadingYear(book);
        BitHelper bh = new BitHelper(book.getStatusFlags());
        bh.setRead(dayOfReadingYear, cbxRead.isSelected());
        book.setStatusFlags(bh.toString());
        try {
            Files.write(Paths.get(book.getBookPath()),
                    MapperFactory.getObjectMapper().writeValueAsBytes(book));
            reloadBooksAndSelect(book.getTitle());
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

    @FXML
    private void addBook() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(Paths.get(System.getProperty("user.home"), "Downloads").toFile());
        chooser.setTitle("Open a Readsy Book");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Readsy files", "*.readsy"));
        File file = chooser.showOpenDialog(appModel.getStage());
        if (file != null) {
            try {
                // unzip to temp dir, getting book directory name
                Path source = Paths.get(file.getAbsolutePath());
                Path tempDir = Files.createTempDirectory("readsy");
                BookUtils.unzip(source, tempDir);
                Path newBookDir;
                try (Stream<Path> files = Files.list(tempDir)) {
                    newBookDir = files.findFirst().orElseThrow();
                }

                // load the book.json file
                ObjectMapper mapper = MapperFactory.getObjectMapper();
                Book newBook = mapper.readValue(Paths.get(String.valueOf(newBookDir), "book.json").toFile(), Book.class);

                // check to see if the book exists
                if (bookList.getItems().stream()
                        .anyMatch(book -> book.getTitle().equals(newBook.getTitle()))) {
                    logger.error("Book {} already exists, can't add it again.", newBook.getTitle());
                    Alert errorAlert = new Alert(Alert.AlertType.INFORMATION);
                    errorAlert.setTitle("Book Exists");
                    errorAlert.setHeaderText(String.format("The book \"%s\" is already in your library.",
                            newBook.getTitle()));
                    errorAlert.showAndWait();
                } else {
                    // set fields in book
                    if (newBook.getValidYear() == 0) {
                        LocalDate now = LocalDate.now();
                        newBook.setReadingStartDate(Formatters.shortISOFormatter.format(now));
                        newBook.setReadingEndDate(Formatters.shortISOFormatter.format(now.plusYears(1).minusDays(1)));
                    } else {
                        newBook.setReadingStartDate(LocalDate.of(newBook.getValidYear(), 1, 1)
                                .format(Formatters.shortISOFormatter));
                        newBook.setReadingEndDate(LocalDate.of(newBook.getValidYear(), 12, 31)
                                .format(Formatters.shortISOFormatter));
                    }
                    newBook.setBookPath(String.valueOf(Paths.get(appModel.getConfiguration().getBookDirectory(), String.valueOf(newBookDir.getFileName()))));

                    // write the updated book.json file
                    mapper.writeValue(Paths.get(String.valueOf(newBookDir), "book.json").toFile(), newBook);

                    // move the new book directory to the book directory
                    FileUtils.moveDirectory(newBookDir.toFile(), new File(newBook.getBookPath()));

                    // reload book list
                    reloadBooksAndSelect(newBook.getTitle());
                }
            } catch (Exception ioe) {
                logger.error("Error while adding book {}", file, ioe);
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("There was an error when trying to add the book.");
                errorAlert.setContentText(String.format("""
                         File: %s
                        Error: %s
                        See the logs for more detail.""", file.getAbsolutePath(), ioe.getMessage()));
                errorAlert.showAndWait();
            }
        }
    }

    @FXML
    private void deleteBook() {
        Book book = bookList.getSelectionModel().getSelectedItem();
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Delete Book?");
        a.setHeaderText(String.format("You are about to delete the book \"%s\".%nAre you sure?", book.getTitle()));
        a.showAndWait();
        if (a.getResult() == ButtonType.OK) {
            try {
                FileUtils.deleteDirectory(Paths.get(book.getBookPath()).getParent().toFile());
                reloadBooksAndSelect("");
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

    @FXML
    private void close() {
        appModel.getStage().close();
    }

    @FXML
    private void mnuGetBooksAction() {
        try {
            Desktop.getDesktop().browse(new URI(Constants.READSY_DOWNLOAD_PAGE));
        } catch (Exception e) {
            logger.error("Error trying to open {}", Constants.READSY_DOWNLOAD_PAGE, e);
        }
    }

    @FXML
    private void showBookInfo() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Book Information");
        Book book = bookList.getSelectionModel().getSelectedItem();
        if (book == null) {
            a.setHeaderText("Please select a book first.");
        } else {
            a.setHeaderText(String.format("%s by %s", book.getTitle(), book.getAuthor()));
            a.setContentText(String.format("""
                            Valid for %s
                            Reading from %s - %s
                            Book Version: %s""",
                    book.getValidYear() == 0 ? "any year" : "the year " + book.getValidYear(),
                    Formatters.longMonthAndDayAndYearFormatter.format(LocalDate.parse(book.getReadingStartDate(), Formatters.shortISOFormatter)),
                    Formatters.longMonthAndDayAndYearFormatter.format(LocalDate.parse(book.getReadingEndDate(), Formatters.shortISOFormatter)),
                    book.getVersion()));
        }
        a.showAndWait();
    }

    @FXML
    private void resetStatus() {
        Book book = bookList.getSelectionModel().getSelectedItem();
        if (book == null) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Book Information");
            a.setHeaderText("Please select a book first.");
        } else {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setTitle("Reset Status?");
            a.setHeaderText(String.format("Reset reading status for %s by %s", book.getTitle(), book.getAuthor()));
            a.setContentText("This will reset the reading status for the book. Your current progress will be lost. Are you sure?");
            Optional<ButtonType> result = a.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                book.setStatusFlags(Constants.NOTHING_READ);
                try {
                    MapperFactory.getObjectMapper().writeValue(Paths.get(book.getBookPath()).toFile(), book);
                    reloadBooksAndSelect(book.getTitle());
                } catch (Exception e) {
                    logger.error("Error while resetting reading status.", e);
                    Alert ea = new Alert(Alert.AlertType.ERROR);
                    ea.setTitle("Error Saving Status");
                    ea.setHeaderText("There was an error while trying to save the new status.");
                    ea.setContentText(String.format("Error message %s%nSee logs for details.", e.getMessage()));
                    ea.showAndWait();
                }
            }
        }
    }

    // todo
    @FXML
    private void markPreviousDaysRead() {

    }

    // todo
    @FXML
    private void changeStartDate() {
        Book book = bookList.getSelectionModel().getSelectedItem();
        if (book == null) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("No Book Selected");
            a.setHeaderText("Please select a book first.");
            a.showAndWait();
        } else if (book.getValidYear() != 0) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Invalid Operation");
            a.setHeaderText("Unable To Change Start Date");
            a.setContentText(String.format("""
                    This book is only valid for the year %s.
                    The start and end reading dates cannot be altered.""", book.getValidYear()));
            a.showAndWait();
        } else {
            DatePicker datePicker = new DatePicker(LocalDate.parse(book.getReadingStartDate(), Formatters.shortISOFormatter));
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Select A Date");
            alert.setHeaderText("""
                    Select a new reading start date for this book.
                    This will reset the reading status and make
                    the first reading day the selected date.""");

            // Set the DatePicker as the dialog's content
            alert.getDialogPane().setContent(datePicker);

            // Show the dialog and wait for user response
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // todo check the date to make sure it makes sense
                    LocalDate selectedDate = datePicker.getValue();
                    book.setStatusFlags(Constants.NOTHING_READ);
                    book.setReadingStartDate(selectedDate.format(Formatters.shortISOFormatter));
                    book.setReadingEndDate(selectedDate.plusYears(1).minusDays(1)
                            .format(Formatters.shortISOFormatter));
                    try {
                        MapperFactory.getObjectMapper().writeValue(Paths.get(book.getBookPath()).toFile(), book);
                        reloadBooksAndSelect(book.getTitle());
                    } catch (Exception e) {
                        logger.error("Error while changing reading start date.", e);
                        Alert ea = new Alert(Alert.AlertType.ERROR);
                        ea.setTitle("Error Changing Date");
                        ea.setHeaderText("There was an error while trying to change the reading start date.");
                        ea.setContentText(String.format("Error message %s%nSee logs for details.", e.getMessage()));
                        ea.showAndWait();
                    }
                }
            });
        }
    }
}