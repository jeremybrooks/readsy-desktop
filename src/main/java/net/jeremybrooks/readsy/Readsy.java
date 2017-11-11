/*
 * readsy - read something new every day <http://jeremybrooks.net/readsy>
 *
 * Copyright (c) 2013-2017  Jeremy Brooks
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

import net.jeremybrooks.common.util.MacUtil;
import net.jeremybrooks.readsy.gui.MainWindow;
import net.jeremybrooks.readsy.gui.WelcomeDialog;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import java.awt.Image;
import java.io.File;


/**
 * Readsy entry point.
 * This is the entry point for the Readsy application.  This class takes
 * care of initializing logging, initializing the XML parser class, and creating
 * the main window.  A reference to the main window is kept here as a static
 * reference, so other classes can get it by calling Readsy.getMainWindow().
 *
 * @author Jeremy Brooks
 */
public class Readsy {

  /**
   * Version.
   */
  public static String VERSION = "";

  public static final String HOME_PAGE = "http://jeremybrooks.net/readsy";

  private static Logger logger;
  private static MainWindow mainWindow;

  private static File dataDir = new File(System.getProperty("user.home"), ".readsy");
  private static File contentDir = new File(dataDir, "content");

  public static Image WINDOW_IMAGE = (new ImageIcon(Readsy.class.getResource("/images/icon16.png")).getImage());

  /* Default constructor is private. */
  private Readsy() {
  }


  /**
   * Application entry point.
   * No command line arguments are supported.
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    if (MacUtil.isRunningOnMac()) {
      MacUtil.setMacMenuBar();
      new OSXSetup();
    }

    System.setProperty("WORDNIK_API_KEY", "5458ce49330219f23e0020790810f29b3818096f5fbbf8560");

    try {
      Readsy.VERSION = Readsy.class.getPackage().getImplementationVersion();
      if (VERSION == null) {
        VERSION = "unknown";
      }
    } catch (Exception e) {
      Readsy.VERSION = "unknown";
    }

    new Readsy().startup();
  }


  /**
   * Get a reference to the main window.
   *
   * @return reference to the main window.
   */
  public static MainWindow getMainWindow() {
    return mainWindow;
  }


  /**
   * Get a reference to the data directory.
   *
   * @return data directory.
   */
  public static File getDataDir() {
    return dataDir;
  }

  public static File getContentDir() {
    return contentDir;
  }


  /**
   * Do some startup stuff, then create the main window and show it.
   */
  private void startup() {
    try {
      if (!contentDir.exists() && !contentDir.mkdirs()) {
        throw new Exception("Unable to create content directory " + contentDir.getAbsolutePath());
      }
      PropertyManager.getInstance().init();
      PropertyConfigurator.configure(PropertyManager.getInstance().getProperties());
      logger = Logger.getLogger(Readsy.class);
      logger.debug("Logging configured.");

      logger.debug("XML Parser initialized successfully.");


    } catch (Exception e) {
      if (logger != null) {
        logger.fatal("Error during application startup.", e);
      }
      e.printStackTrace();
      JOptionPane.showMessageDialog(
          null,
          "An error occured during application startup.\n" +
              e.getMessage() + "\n" +
              "Program will abort.",
          "Fatal Error",
          JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    }
    mainWindow = new MainWindow();
    if (PropertyManager.getInstance().getProperty(PropertyManager.DROPBOX_ACCESS_TOKEN) == null) {
      new WelcomeDialog().setVisible(true);
    } else {
      mainWindow.setVisible(true, true);
      logger.debug("No Dropbox token, showing welcome dialog.");
    }

    Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook(mainWindow)));

    if (PropertyManager.getInstance().getPropertyAsBoolean(PropertyManager.PROPERTY_CHECK_FOR_UPDATES)
        && (!VERSION.equals("unknown"))) {
      Thread t = new Thread(new VersionChecker(mainWindow));
      t.setDaemon(true);
      t.start();
    }
  }
}
