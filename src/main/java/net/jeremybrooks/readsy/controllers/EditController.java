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
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.StringConverter;
import net.jeremybrooks.readsy.ActiveState;
import net.jeremybrooks.readsy.Formatters;
import net.jeremybrooks.readsy.MapperFactory;
import net.jeremybrooks.readsy.model.AppModel;
import net.jeremybrooks.readsy.model.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class EditController {
    private static final Logger logger = LogManager.getLogger();
    private final AppModel appModel;
    SimpleObjectProperty<LocalDate> localDateProperty = new SimpleObjectProperty<>();
    private final StringProperty heading = new SimpleStringProperty();
    private final StringProperty text = new SimpleStringProperty();
    @FXML
    Label lblDirectory;
    @FXML
    Label lblTitle;
    @FXML
    Label lblShortTitle;
    @FXML
    Label lblAuthor;
    @FXML
    Label lblYear;
    @FXML
    TextField txtHeading;
    @FXML
    TextArea txtText;
    @FXML
    Button btnPrevious;
    @FXML
    Button btnNext;
    @FXML
    Button btnDone;
    @FXML
    Label lblDate;
    @FXML
    ImageView imgCover;

    public EditController(AppModel appModel) {
        this.appModel = appModel;
        // if the window is closed, delegate to the same action as when
        // the user clicks the Done button
        appModel.getStage().setOnCloseRequest(event -> {
            event.consume();
            done(null);
        });
    }

    @FXML
    public void initialize() {
        lblDirectory.setText(appModel.getEditorBookDirectory().toString());
        lblTitle.setText(appModel.getBook().getTitle());
        lblShortTitle.setText(appModel.getBook().getShortTitle());
        lblAuthor.setText(appModel.getBook().getAuthor());
        lblYear.setText(appModel.getBook().getValidYear() == 0 ? "Any" : String.valueOf(appModel.getBook().getValidYear()));
        if (appModel.getBook().getValidYear() == 0) {
            localDateProperty.setValue(LocalDate.of(2013, 1, 1));
        } else {
            localDateProperty.setValue(LocalDate.of(appModel.getBook().getValidYear(), 1, 1));
        }
        Integer day = findFirstEmptyDay();
        if (day == null) {
            day = 1;
        }
        localDateProperty.setValue(localDateProperty.get().withDayOfYear(day));
        readPageAndSetText();
        StringConverter<LocalDate> converter = new StringConverter<>() {
            @Override
            public LocalDate fromString(String string) {
                return string == null || string.isEmpty() ? null : LocalDate.parse(string, Formatters.longMonthAndDayFormatter);
            }

            @Override
            public String toString(LocalDate date) {
                return date == null ? null : Formatters.longMonthAndDayFormatter.format(date);
            }
        };

        Path coverPath = Paths.get(String.valueOf(appModel.getEditorBookDirectory()), "cover.png");
        if (Files.exists(coverPath)) {
            try {
                imgCover.setImage(new Image(Files.newInputStream(coverPath)));
            } catch (Exception e) {
                logger.error("Could not read cover from {}", coverPath, e);
            }
        }

        lblDate.textProperty().bindBidirectional(localDateProperty, converter);
        txtHeading.textProperty().bindBidirectional(heading);
        txtText.textProperty().bindBidirectional(text);
        Platform.runLater(() -> txtHeading.requestFocus());
    }


    /*
     * Get a sorted list of all the json files, except the book.json file,
     * then read them to find the first one with an empty heading
     */
    private Integer findFirstEmptyDay() {
        logger.debug("Looking for first empty page.");
        final ObjectMapper mapper = MapperFactory.getObjectMapper();
        for (int day = 1; day < localDateProperty.get().lengthOfYear(); day++) {
            Path p = Paths.get(String.valueOf(appModel.getEditorBookDirectory()), day + ".json");
            logger.debug("Checking {}", p);
            try {
                Page page = mapper.readValue(p.toFile(), Page.class);
                if (page.getHeading().isEmpty() || page.getText().isEmpty()) {
                    logger.debug("Found empty page at path {}", p);
                    return day;
                }
            } catch (Exception e) {
                logger.error("Error reading path {}", p, e);
            }
        }
        // no empty page found
        return null;
    }


    @FXML
    private void goToPrevious(ActionEvent actionEvent) {
        saveEntry();
        // if we are at the beginning of the year, go to the end
        if (localDateProperty.get().getDayOfYear() == 1) {
            localDateProperty.setValue(
                    localDateProperty.get().withDayOfYear(localDateProperty.get().lengthOfYear()));
        } else {
            localDateProperty.setValue(
                    localDateProperty.get().minusDays(1));
        }
        readPageAndSetText();
    }

    @FXML
    private void goToNext(ActionEvent actionEvent) {
        saveEntry();
        // if we are at the end of the year, go to the beginning
        if (localDateProperty.get().getDayOfYear() == localDateProperty.get().lengthOfYear()) {
            localDateProperty.setValue(localDateProperty.get().withDayOfYear(1));
        } else {
            localDateProperty.setValue(localDateProperty.get().plusDays(1));
        }
        readPageAndSetText();
    }

    private void readPageAndSetText() {
        int day = localDateProperty.get().getDayOfYear();
        Path p = Paths.get(String.valueOf(appModel.getEditorBookDirectory()), day + ".json");
        try {
            Page page = MapperFactory.getObjectMapper().readValue(p.toFile(), Page.class);
            heading.set(page.getHeading());
            text.set(page.getText());
        } catch (Exception e) {
            logger.error("Error reading page for day {}", day , e);
            heading.set("");
            text.set("");
        }
    }

    private void saveEntry() {
        Page page = new Page();
        page.setHeading(txtHeading.getText().trim());
        page.setText(txtText.getText());
        Path pagePath = Paths.get(String.valueOf(appModel.getEditorBookDirectory()),
                localDateProperty.get().getDayOfYear() + ".json");
        logger.debug("Saving entry at path {}", pagePath);
        try {
            Files.writeString(pagePath, MapperFactory.getObjectMapper().writeValueAsString(page), StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("Error writing page to file at path {}", pagePath, e);
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.initOwner(appModel.getStage());
            a.setTitle("File Error");
            a.setHeaderText("Error while writing file.");
            a.setContentText(String.format("There was an error while writing the page to file.\n" +
                    "The file path was %s\n" +
                    "Please check the logs for details.", pagePath));
            a.showAndWait();
        }

    }

    private void makeZipFiles() {
        Path zipFileName = Paths.get(String.valueOf(appModel.getEditorBookDirectory().getParent()),
                lblShortTitle.getText() + ".zip");
        try (ZipOutputStream zipStream = new ZipOutputStream(Files.newOutputStream(zipFileName))) {
           Files.walkFileTree(appModel.getEditorBookDirectory(), new SimpleFileVisitor<>() {
               @Override
               public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
                   try {
                       Path targetFile = appModel.getEditorBookDirectory().relativize(file);
                       zipStream.putNextEntry(new ZipEntry(lblShortTitle.getText() + "/" + targetFile));
                       byte[] bytes = Files.readAllBytes(file);
                       zipStream.write(bytes, 0, bytes.length);
                       zipStream.closeEntry();
                   } catch (Exception e) {
                       logger.error("Error writing zip file.", e);
                       throw new RuntimeException("Error writing zip file.", e);
                   }
                   return FileVisitResult.CONTINUE;
               }
           });
        } catch (Exception e) {
            logger.error("Error while creating zip files", e);
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.initOwner(appModel.getStage());
            a.setTitle("I/O Error");
            a.setHeaderText("There was an error creating the zip files.");
            a.setContentText("The error message was \n" + e.getMessage() + "\n" +
                    "Check the logs for more details.");
            a.showAndWait();
        }
            Path readsyFileName = Paths.get(String.valueOf(appModel.getEditorBookDirectory().getParent()),
                    lblShortTitle.getText() + ".readsy");
        try {
            Files.copy(zipFileName, readsyFileName, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            logger.error("Error while copying zip file {} to {}", zipFileName, readsyFileName, e);
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.initOwner(appModel.getStage());
            a.setTitle("I/O Error");
            a.setHeaderText("There was an error copying the zip file.");
            a.setContentText("The error message was \n" + e.getMessage() + "\n" +
                    "Check the logs for more details.");
            a.showAndWait();        }
    }

    /* Check for any blank pages and warn the user. */
    private void validatePages() {
        if (findFirstEmptyDay() != null) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.initOwner(appModel.getStage());
            a.setTitle("Validation Warning");
            a.setHeaderText("Some problems were found in your book.");
            a.setContentText("- There are some blank headings or text");
            a.showAndWait();
        }
    }

    public void done(ActionEvent actionEvent) {
        saveEntry();
        validatePages();
        makeZipFiles();
        appModel.setActiveState(ActiveState.BOOKS);
    }
}
