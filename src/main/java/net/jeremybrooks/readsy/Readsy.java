/*
 * readsy - read something new every day <http://jeremybrooks.net/readsy>
 *
 * Copyright (c) 2013-2021  Jeremy Brooks
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

package net.jeremybrooks.readsy;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import net.jeremybrooks.readsy.model.AppModel;
import net.jeremybrooks.readsy.model.Configuration;
import net.jeremybrooks.readsy.workers.SaveConfigWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Readsy entry point.
 *
 * @author Jeremy Brooks
 */
public class Readsy extends Application {
    private static final Logger logger = LogManager.getLogger();

    @Override
    public void start(Stage stage) {
        AppModel appModel = new AppModel();
        appModel.setStage(stage);
        try {
            appModel.setConfiguration(getConfiguration());
            if (appModel.getConfiguration().getBookDirectory() == null ||
                    appModel.getConfiguration().getBookDirectory().isEmpty()) {
                appModel.setActiveState(ActiveState.WELCOME);
            }
            appModel.setVersion(Readsy.class.getPackage().getImplementationVersion());

            ViewManager viewManager = new ViewManager(appModel);
            Scene scene = new Scene(viewManager.getCurrentView(), appModel.getConfiguration().getWindowWidth(), appModel.getConfiguration().getWindowHeight());
            scene.rootProperty().bind(viewManager.currentViewProperty());
            stage.setScene(scene);
            stage.setX(appModel.getConfiguration().getWindowX());
            stage.setY(appModel.getConfiguration().getWindowY());
            stage.show();
        } catch (Exception e) {
            errExit(1, e);
        }
    }

    public static void main(String... args) {
        System.out.println(System.getProperty("os.name"));
        // If running on a Mac, set up the event handler
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            try {
                Class.forName("net.jeremybrooks.readsy.MacOSSetup").getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                logger.error("Could not find class.", e);
            }
        }
        launch();
    }

    private static Configuration getConfiguration() throws IOException {
        Path configDir = Paths.get(Constants.READSY_CONFIG_DIR);
        if (!Files.exists(configDir)) {
            Files.createDirectories(configDir);
        }
        ObjectMapper mapper = MapperFactory.getObjectMapper();
        Configuration configuration;
        Path configFile = Paths.get(Constants.READSY_CONFIG_FILE);
        if (Files.exists(configFile)) {
            configuration = mapper.readValue(configFile.toFile(), Configuration.class);
        } else {
            configuration = new Configuration();
            new SaveConfigWorker(configuration);
        }
        return configuration;
    }

    private static void errExit(int exitCode, Exception e) {
        String message;
        String title = switch (exitCode) {
            case 1 -> {
                message = "There was an error while trying to read the configuration file\n" +
                        Constants.READSY_CONFIG_FILE +
                        "\n\nCan your user create files at this location?";
                yield "Configuration Init Failure";
            }
            case 2 -> {
                message = "The Desktop API is not supported on this operating system.\n\nIf you are running a Debian or Ubuntu system,\ntry 'sudo apt-get install libgnome2-0'\n\nIf you are running a RedHat system,\ntry 'sudo yum install libgnome'\n\nFor other operating systems, please visit https://jeremybrooks.net/suprsetr/faq.html\n\nThis program will now exit.";
                yield "Desktop API Not Supported";
            }
            default -> {
                message = "An unknown error occurred during startup.";
                yield "Startup Error";
            }
        };
        if (logger != null) {
            logger.fatal(message, e);
        }
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(message);
        a.showAndWait();
        System.exit(exitCode);
    }
}
