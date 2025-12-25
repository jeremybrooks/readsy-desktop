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
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import net.jeremybrooks.readsy.ActiveState;
import net.jeremybrooks.readsy.model.AppModel;
import net.jeremybrooks.readsy.workers.SaveConfigWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class WelcomeController {

    private final AppModel appModel;
    private static final Logger logger = LogManager.getLogger();

    @FXML private TextField txtDirectory;


    public WelcomeController(AppModel appModel) {
        this.appModel = appModel;
    }

    @FXML
    public void browse() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose A Directory");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File directory = chooser.showDialog(txtDirectory.getScene().getWindow());
        if (directory != null) {
            try {
                appModel.getConfiguration().setBookDirectory(directory.getAbsolutePath());
                new SaveConfigWorker(appModel);
                txtDirectory.setText(appModel.getConfiguration().getBookDirectory());
                appModel.setActiveState(ActiveState.BOOKS);
            } catch (Exception e) {
                logger.error("Error reading or writing configuration file.", e);
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.initOwner(appModel.getStage());
                a.setTitle("Configuration Error");
                a.setHeaderText("There was an error reading or writing the configuration file.");
                a.setContentText(String.format("The error was '%s'. See the log for details.",
                        e.getMessage()));
                a.showAndWait();
            }
        }
    }
}
