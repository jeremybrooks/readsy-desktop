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

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.jeremybrooks.readsy.ActiveState;
import net.jeremybrooks.readsy.Constants;
import net.jeremybrooks.readsy.MapperFactory;
import net.jeremybrooks.readsy.model.AppModel;
import net.jeremybrooks.readsy.model.Book;
import net.jeremybrooks.readsy.model.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class EditStartController {
    private static final Logger logger = LogManager.getLogger();
    private final AppModel appModel;
    @FXML
    private Button btnBrowse;
    @FXML
    private Button btnCreate;
    @FXML
    private TextField txtTitle;
    @FXML
    private TextField txtShortTitle;
    @FXML
    private TextField txtAuthor;
    @FXML
    private TextField txtYear;
    @FXML private TextField txtVersion;
    @FXML
    private ImageView imgCover;
    @FXML
    private Button btnChangeCover;
    private byte[] coverImageBytes;

    public EditStartController(AppModel appModel) {
        this.appModel = appModel;

        appModel.getStage().setOnCloseRequest(event -> {
            // Handle the close event
            System.out.println("Stage is closing");
            // Optionally consume the event to prevent closing
            event.consume();
            appModel.setActiveState(ActiveState.BOOKS);
        });
    }

    @FXML
    public void initialize() {
        try(InputStream in = EditStartController.class.getResourceAsStream("/images/emptycover.png")) {
            assert in != null;
            coverImageBytes = in.readAllBytes();
            imgCover.setImage(new Image(new ByteArrayInputStream(coverImageBytes)));
        } catch (Exception e) {
            logger.warn("Could not read bytes from emptycover.png", e);
        }

    }

    @FXML
    private void browseCover() {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(Paths.get(System.getProperty("user.home")).toFile());
        fc.setTitle("Choose Cover Image");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        File cover = fc.showOpenDialog(btnChangeCover.getScene().getWindow());
        if (cover != null) {
            try (InputStream in = new FileInputStream(cover)) {
                coverImageBytes = in.readAllBytes();
                imgCover.setImage(new Image(new ByteArrayInputStream(coverImageBytes)));
            } catch (Exception e) {
                logger.error("Error loading file {}", cover, e);
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.initOwner(appModel.getStage());
                a.setTitle("I/O Error");
                a.setHeaderText("Error loading the cover file.");
                a.setContentText("The cover file at " + cover.getAbsolutePath() + "\n" +
                        "could not be loaded due to an error. The error message was:\n" +
                        e.getMessage() + "\n" +
                        "See the logs for more information.");
                a.showAndWait();
            }
        }
    }

    @FXML
    private void browse() {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Select Book Directory");
        dc.setInitialDirectory(Paths.get(System.getProperty("user.home")).toFile());
        File directory = dc.showDialog(btnBrowse.getScene().getWindow());
        Path p = Paths.get(directory.getAbsolutePath(), "book.json");
        if (Files.exists(p)) {
            try {
                Book book = MapperFactory.getObjectMapper().readValue(p.toFile(), Book.class);
                appModel.setEditorBookDirectory(p.getParent());
                appModel.setBook(book);
                appModel.setActiveState(ActiveState.EDIT_BOOK);
            } catch (Exception e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.initOwner(appModel.getStage());
                a.setTitle("Invalid Book");
                a.setHeaderText("Could not read book.json file.");
                a.setContentText(String.format("The error was '%s'. See the log for details.",
                        e.getMessage()));
                a.showAndWait();
            }
        } else {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.initOwner(appModel.getStage());
            a.setTitle("No Content Found");
            a.setHeaderText("The directory did not contain Readsy book data.");
            a.setContentText("There was no book.json file in the directory.\n" +
                    "Please ensure the directory has Readsy book data and try again.");
            a.showAndWait();
        }
    }

    @FXML
    private void create() {
        if (validateEntry()) {
            Book book = new Book();
            book.setValidYear(Integer.parseInt(txtYear.getText()));
            book.setTitle(txtTitle.getText().trim());
            book.setShortTitle(txtShortTitle.getText().trim());
            book.setAuthor(txtAuthor.getText().trim());
            book.setVersion(txtVersion.getText().trim());
            book.setStatusFlags(Constants.NOTHING_READ);

            Path directory = Paths.get(System.getProperty("user.home"), book.getShortTitle());
            if (Files.exists(directory)) {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.initOwner(appModel.getStage());
                a.setTitle("Book Directory Exists");
                a.setHeaderText(String.format("The book directory named %s already exists.", book.getShortTitle()));
                a.setContentText("There is already a directory at " + directory + "\n" +
                        "To edit the book at this location click Browse and select the directory.\n" +
                        "You can also change the short title or delete the directory.");
                a.showAndWait();
            } else {
                try {
                    Files.createDirectories(directory);

                    // create empty files for dates
                    int numberOfEntries = 365;
                    int day = 1;
                    Calendar calendar = new GregorianCalendar();
                    // for a "0" year file, create 365 entries
                    // otherwise create actual number of days in the year entries
                    if (book.getValidYear() != 0) {
                        calendar.set(Calendar.YEAR, book.getValidYear());
                        numberOfEntries = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
                    }

                    Path file;
                    Page page = new Page();
                    page.setHeading("");
                    page.setText("");
                    String blankPage = MapperFactory.getObjectMapper().writeValueAsString(page);
                    while (day <= numberOfEntries) {
                        file = Paths.get(String.valueOf(directory), day + ".json");
                        Files.writeString(file, blankPage, StandardCharsets.UTF_8);
                        day++;
                    }

                    // write the book file
                    file = Paths.get(String.valueOf(directory), "book.json");
                    Files.writeString(file, MapperFactory.getObjectMapper().writeValueAsString(book), StandardCharsets.UTF_8);

                    // write the cover file
                    Path coverFile = Paths.get(String.valueOf(directory), "cover.png");
                    Files.write(coverFile, coverImageBytes);

                    appModel.setEditorBookDirectory(directory);
                    appModel.setBook(book);
                    appModel.setActiveState(ActiveState.EDIT_BOOK);
                } catch (Exception e) {
                    logger.error("Error creating directory {}", directory, e);
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.initOwner(appModel.getStage());
                    a.setTitle("File Creation Error");
                    a.setHeaderText("Could not create the directory.");
                    a.setContentText("There was an error while trying to create the directory\n" + directory);
                    a.showAndWait();
                }
            }
        }
    }

    private boolean validateEntry() {
        boolean valid = true;
        StringBuilder err = new StringBuilder();
        if (txtTitle.getText().trim().isEmpty()) {
            err.append("  * You must enter a Title.\n");
            valid = false;
        }
        if (txtShortTitle.getText().trim().isEmpty()) {
            err.append("  * You must enter a Short Title.\n");
            valid = false;
        }
        if (txtAuthor.getText().trim().isEmpty()) {
            err.append(" * You must enter an Author.\n");
            valid = false;
        }
        if (!txtYear.getText().isEmpty()) {
            try {
                Integer.parseInt(txtYear.getText().trim());
            } catch (NumberFormatException nfe) {
                err.append(" * Year is not a valid number.\n");
                valid = false;
            }
        }
        if (txtVersion.getText().trim().isEmpty()) {
            err.append(" * You must enter a Version.\n");
            valid = false;
        }

        if (!valid) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.initOwner(appModel.getStage());
            a.setTitle("Invalid Entries");
            a.setHeaderText("Required fields are missing.");
            a.setContentText("Please correct the following errors:\n" + err);
            a.showAndWait();
        } else {
            if (this.txtYear.getText().trim().isEmpty()) {
                this.txtYear.setText("0");
            }
        }

        return valid;
    }
}
