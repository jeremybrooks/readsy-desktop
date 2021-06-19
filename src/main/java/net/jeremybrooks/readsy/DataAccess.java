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

import net.jeremybrooks.readsy.model.Entry;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author Jeremy Brooks
 */
public class DataAccess {
  private static final Logger logger = LogManager.getLogger();

  private static final SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
  private static final SimpleDateFormat MMdd = new SimpleDateFormat("MMdd");

  /**
   * Returns a List of directories that contain readsy data.
   *
   * @return sorted list of data directories.
   * @throws Exception from file operations.
   */
  public static List<String> getDataDirectoryNames() throws Exception {
    logger.debug("Getting data directory names");
    return Files.list(Paths.get(PropertyManager.getInstance().getProperty(PropertyManager.READSY_FILE_DIRECTORY)))
        .filter(Files::isDirectory)
        .map(p -> p.getFileName().toString())
        .sorted()
        .collect(Collectors.toList());
  }

  public static Properties getMetadata(String contentDirectory) throws Exception {
    logger.debug("Getting metadata for {}", contentDirectory);
    Properties properties = new Properties();
    Path file = Paths.get(PropertyManager.getInstance().getProperty(PropertyManager.READSY_FILE_DIRECTORY),
        contentDirectory, "metadata");
    try (InputStream in = Files.newInputStream(file)) {
      properties.load(in);
    }
    return properties;
  }

  public static void saveMetadata(String contentDirectory, Properties metadata) throws Exception {
    logger.debug("Saving metadata. contentDirectory={},metadata={}", contentDirectory, metadata);
    // write properties using a StringBuilder so we control the line separator
    StringBuilder sb = new StringBuilder("#\n");
    sb.append("#readsy desktop ").append(System.getProperty("os.name")).append(new Date()).append('\n');
    for (String name : metadata.stringPropertyNames()) {
      sb.append(name).append('=').append(metadata.getProperty(name)).append('\n');
    }
    Path target = Paths.get(PropertyManager.getInstance().getProperty(PropertyManager.READSY_FILE_DIRECTORY),
        contentDirectory, "metadata");
    Files.write(target, sb.toString().getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Get the entry for the specified date.
   * <p>If there are any errors, they will be logged and null returned.</p>
   * <p>If there is no entry for the specified date, null is returned.</p>
   *
   * @param date             the date to get entry for.
   * @param metadata         properties describing the entry.
   * @param contentDirectory directory to look in.
   * @return entry for the date.
   */
  public static Entry getEntryForDateFromDirectory(Date date, Properties metadata, String contentDirectory) {
    String filename = MMdd.format(date);
    logger.debug("Getting entry; date={},contentDirectory={},fileanme={}", date, contentDirectory, filename);
    Entry entry = null;

    // only get the entry if the file is for any year or the current year
    if (metadata.getProperty("year").equals("0") || metadata.getProperty("year").equals(yyyy.format(date))) {
      try {
        Path path = Paths.get(PropertyManager.getInstance().getProperty(PropertyManager.READSY_FILE_DIRECTORY),
            contentDirectory, filename);
        String content = Files.readString(path);
        entry = content == null ? null : new Entry(content);
      } catch (Exception e) {
        entry = null;
        logger.error("Error getting entry for date.", e);
      }
    }

    return entry;
  }

  public static void uploadFile(String path, byte[] data) throws Exception {
    Path target =
        Paths.get(PropertyManager.getInstance().getProperty(PropertyManager.READSY_FILE_DIRECTORY), path);
    Files.write(target, data);
  }

  public static void createDirectory(String path) throws Exception {
    Path target =
        Paths.get(PropertyManager.getInstance().getProperty(PropertyManager.READSY_FILE_DIRECTORY), path);
    Files.createDirectories(target);
  }

  public static boolean directoryExists(String path) {
    Path target =
        Paths.get(PropertyManager.getInstance().getProperty(PropertyManager.READSY_FILE_DIRECTORY), path);
    return Files.isDirectory(target);
  }

  public static void deletePath(String path) throws Exception {
    Path target =
        Paths.get(PropertyManager.getInstance().getProperty(PropertyManager.READSY_FILE_DIRECTORY), path);
    if (Files.isDirectory(target)) {
      FileUtils.deleteDirectory(target.toFile());
    } else {
      Files.delete(target);
    }
  }
}