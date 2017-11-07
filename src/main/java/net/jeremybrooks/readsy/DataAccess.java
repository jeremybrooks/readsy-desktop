/*
 * readsy - read something new every day
 *
 *     Copyright (C) 2013  Jeremy Brooks
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     You may contact the programs author at jeremyb@whirljack.net
 */

package net.jeremybrooks.readsy;

import net.jeremybrooks.common.comparator.StringComparator;
import net.jeremybrooks.common.util.IOUtil;
import net.jeremybrooks.readsy.bo.Entry;
import net.jeremybrooks.readsy.dropbox.DropboxHelper;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Jeremy Brooks
 */
public class DataAccess {
  private static Logger logger = Logger.getLogger(DataAccess.class);

  private static SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
  private static SimpleDateFormat MMdd = new SimpleDateFormat("MMdd");

  private static Map<String, String> fileCache = new HashMap<>();

  /**
   * Returns a List of directories that contain readsy data.
   * <p>This method will look in Dropbox if Dropbox sync is configured; otherwise it will look in
   * the content directory.</p>
   *
   * @return sorted list of data directories.
   */
  public static List<String> getDataDirectoryNames() {
    logger.debug("Getting data directory names");
    List<String> names = new ArrayList<>();
    try {
      names.addAll(DropboxHelper.getInstance().getFoldersAtPath("/"));
    } catch (Exception e) {
      logger.error("Error getting data directory names.", e);
    }
    Collections.sort(names, new StringComparator());
    return names;
  }

  public static Properties getMetadata(String contentDirectory) throws Exception {
    logger.debug("Getting metadata for " + contentDirectory);
    Properties properties = new Properties();
    DropboxHelper.getInstance().loadPropertiesFromFile(properties, contentDirectory + "/metadata");
    return properties;
  }

  public static void saveMetadata(String contentDirectory, Properties metadata) throws Exception {
    logger.debug("Saving metadata. contentDirectory=" + contentDirectory + ",metadata=" + metadata);
    // write properties using a StringBuilder so we control the line separator
    StringBuilder sb = new StringBuilder("#\n");
    sb.append("#readsy desktop ").append(System.getProperty("os.name")).append(new Date()).append('\n');
    for (String name : metadata.stringPropertyNames()) {
      sb.append(name).append('=').append(metadata.getProperty(name)).append('\n');
    }
    byte[] propertiesData = sb.toString().getBytes("UTF-8");
    DropboxHelper.getInstance().uploadPropertiesData(contentDirectory + "/metadata", propertiesData);
  }

  /**
   * Get the entry for the specified date.
   * <p>If there are any errors, they will be logged and null returned.</p>
   * <p>If there is no entry for the specified date, null is returned.</p>
   *
   * @param date the date to get entry for.
   * @param metadata properties describing the entry.
   * @param contentDirectory directory to look in.
   * @return entry for the date.
   */
  public static Entry getEntryForDateFromDirectory(Date date, Properties metadata, String contentDirectory) {
    String filename = MMdd.format(date);
    logger.debug("Getting entry; date=" + date + ",contentDirectory=" + contentDirectory + ",filename=" + filename);
    Entry entry = null;

    // only get the entry if the file is for any year or the current year
    if (metadata.getProperty("year").equals("0") || metadata.getProperty("year").equals(yyyy.format(date))) {
      try {
        String path = contentDirectory + "/" + filename;
        String content = null;
        if (fileCache.containsKey(path)) {
          logger.debug("Content was in cache.");
          content = fileCache.get(path);
        } else {
          logger.debug("Content not in cache, loading from Dropbox.");
          byte[] data = DropboxHelper.getInstance().getFile(path);
          if (data != null) {
            content = new String(data, "UTF-8");
          }
          fileCache.put(path, content);
        }
        entry = content == null ? null : entryFromReader(new StringReader(content));
      } catch (Exception e) {
        entry = null;
        logger.error("Error getting entry for date.", e);
      }
    }

    return entry;
  }

  private static Entry entryFromReader(Reader reader) throws Exception {
    BufferedReader in = null;
    Entry entry = new Entry();
    try {
      in = new BufferedReader(reader);
      entry.setHeading(in.readLine());
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = in.readLine()) != null) {
        sb.append(line).append('\n');
      }
      entry.setText(sb.toString());
    } finally {
      IOUtil.close(in);
    }
    return entry;
  }

  public static void uploadFile(String path, byte[] data) throws Exception {
    DropboxHelper.getInstance().uploadFile(path, data);
  }

  public static void createDirectory(String path) throws Exception {
    DropboxHelper.getInstance().createFolder(path);
  }

  public static boolean directoryExists(String path) throws Exception {
    return DropboxHelper.getInstance().pathExists(path);
  }

  public static void deletePath(String path) throws Exception {
    logger.debug("Deleting path " + path);
    DropboxHelper.getInstance().delete(path);
  }
}