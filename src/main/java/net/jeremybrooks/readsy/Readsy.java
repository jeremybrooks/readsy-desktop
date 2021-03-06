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

package net.jeremybrooks.readsy;

import net.jeremybrooks.readsy.gui.MainWindow;
import net.jeremybrooks.readsy.gui.WelcomeDialog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import java.awt.Desktop;
import java.awt.Image;
import java.io.File;


/**
 * Readsy entry point.
 * This is the entry point for the Readsy application.  This class takes
 * care of initializing logging, and creating
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

  private static Logger logger = LogManager.getLogger();
  private static MainWindow mainWindow;

  private static File dataDir = new File(System.getProperty("user.home"), ".readsy");

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
  public static void main(String... args) {
    // test for Desktop API support
    if (!Desktop.isDesktopSupported()) {
      JOptionPane.showMessageDialog(null,
          "The Desktop API is not supported on this operating system.\n\nIf you are running a Debian or Ubuntu system,\ntry 'sudo apt-get install libgnome2-0'\n\nIf you are running a RedHat system,\ntry 'sudo yum install libgnome'\n\nFor other operating systems, please visit http://jeremybrooks.net/suprsetr/faq.html\n\nThis program will now exit.",
          "Desktop API Not Supported",
          JOptionPane.ERROR_MESSAGE);
      System.exit(2);
    }

    // If running on a Mac, set up the event handler
    if (System.getProperty("os.name").contains("Mac")) {
      System.setProperty("apple.laf.useScreenMenuBar", "true");
      try {
        Class.forName("net.jeremybrooks.readsy.MacOSSetup").getDeclaredConstructor().newInstance();
      } catch (Exception e) {
        logger.error("Could not find class.", e);
      }
    }


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


  /**
   * Do some startup stuff, then create the main window and show it.
   */
  private void startup() {
    try {
      PropertyManager.getInstance().init();
    } catch (Exception e) {
      if (logger != null) {
        logger.fatal("Error during application startup.", e);
      }
      e.printStackTrace();
      JOptionPane.showMessageDialog(
          null,
          "An error occurred during application startup.\n" +
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
