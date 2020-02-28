/*
 * readsy - read something new every day <http://jeremybrooks.net/readsy>
 *
 * Copyright (c) 2013-2020  Jeremy Brooks
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

package net.jeremybrooks.readsy.gui.workers;

import net.jeremybrooks.common.gui.WorkerDialog;
import net.jeremybrooks.readsy.DataAccess;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.SwingWorker;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Jeremy Brooks
 */
public class InstallFileWorker extends SwingWorker<Void, Void> {
  private Logger logger = LogManager.getLogger(InstallFileWorker.class);
  private File file;
  private Exception error;

  public InstallFileWorker(File file) {
    this.file = file;
  }


  @Override
  protected Void doInBackground() throws Exception {
    logger.debug("Installing data file " + file.getAbsolutePath());
    ResourceBundle bundle = ResourceBundle.getBundle("localization.worker");
    // first, find metadata and count entries
    double total = 0.0;
    try (ZipFile zipFile = new ZipFile(file)) {
      Properties metadata = null;
      Enumeration<? extends ZipEntry> entries = zipFile.entries();
      while (entries.hasMoreElements()) {
        total++;
        ZipEntry entry = entries.nextElement();
        if (!entry.isDirectory() && entry.getName().endsWith("metadata")) {
          try (InputStream in = zipFile.getInputStream(entry)) {
            metadata = new Properties();
            metadata.load(in);
          }
        }
      }
      if (metadata == null) {
        throw new Exception("Invalid file format: no metadata file found.");
      }
      String directory = metadata.getProperty("shortDescription");
      if (DataAccess.directoryExists(directory)) {
        throw new Exception("The directory '" + directory + "' already exists.");
      }

      // now create folder and upload all the entries
      DataAccess.createDirectory(directory);
      entries = zipFile.entries();
      int count = 0;
      while (entries.hasMoreElements()) {
        ZipEntry entry = entries.nextElement();
        if (!entry.isDirectory()) {
          count++;
          try (InputStream in = zipFile.getInputStream(entry);
               ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            IOUtils.copy(in, out);
            firePropertyChange(WorkerDialog.EVENT_DIALOG_MESSAGE, "",
                bundle.getString("ifw.messageUploadingFile") + " " + entry.getName());
            DataAccess.uploadFile(entry.getName(), out.toByteArray());
            double percent = count/total*100;
            firePropertyChange("progress", "", (int) percent);
          }
        }
      }
    } catch (Exception e) {
      this.error = e;
    }
    return null;
  }

  public Exception getError() {
    return this.error;
  }
}
