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

package net.jeremybrooks.readsy.workers;

import net.jeremybrooks.readsy.model.AppModel;
import net.jeremybrooks.readsy.Constants;
import net.jeremybrooks.readsy.MapperFactory;
import net.jeremybrooks.readsy.model.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Save the application configuration.
 * <p>
 *     The supplied application configuration will be saved in the Constants.READSY_CONFIG_FILE
 *     in a separate thread.
 * </p>
 */
public class SaveConfigWorker {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Create an instance of this class, saving the configuration found in the AppModel class.
     * @param appModel model containing the configuration to save.
     */
    public SaveConfigWorker(AppModel appModel) {
        this(appModel.getConfiguration());
    }

    /**
     * Create an instance of this class, saving the provided configuration.
     *
     * @param configuration configuration to save.
     */
    public SaveConfigWorker(Configuration configuration) {
        try (ExecutorService xs = Executors.newSingleThreadExecutor()) {
            xs.submit(() -> {
                Path configFile = Paths.get(Constants.READSY_CONFIG_FILE);
                try (OutputStream out = Files.newOutputStream(configFile)) {
                    MapperFactory.getObjectMapper().writeValue(out, configuration);
                } catch (Exception e) {
                    logger.error("Error writing config file {}", configFile, e);
                }
            });
            xs.shutdown();
        }
    }
}
