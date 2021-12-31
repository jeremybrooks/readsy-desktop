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

import net.jeremybrooks.readsy.gui.MainWindow;
import net.jeremybrooks.readsy.gui.WelcomeDialog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Desktop;


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
  private static final Logger logger = LogManager.getLogger();


  /**
   * Application entry point.
   * No command line arguments are supported.
   *
   * @param args the command line arguments
   */
  public static void main(String... args) {
    // test for Desktop API support
    if (!Desktop.isDesktopSupported()) {
      errExit(2, null);
    }

    try {
      PropertyManager.getInstance().init();
    } catch (Exception e) {
      errExit(1, e);
    }

    try {
      Readsy.VERSION = Readsy.class.getPackage().getImplementationVersion();
      if (VERSION == null) {
        VERSION = "unknown";
      }
    } catch (Exception e) {
      Readsy.VERSION = "unknown";
    }

    // create the main window instance now, because the MacOSSetup class will
    // reference the instance
    new MainWindow();

    // If running on a Mac, set up the event handler
    if (System.getProperty("os.name").toLowerCase().contains("mac")) {
      System.setProperty("apple.laf.useScreenMenuBar", "true");
      try {
        Class.forName("net.jeremybrooks.readsy.MacOSSetup").getDeclaredConstructor().newInstance();
      } catch (Exception e) {
        logger.error("Could not find class.", e);
      }
    }

    if (PropertyManager.getInstance().getProperty(PropertyManager.READSY_FILE_DIRECTORY) == null) {
      SwingUtilities.invokeLater(() -> new WelcomeDialog().setVisible(true));
    } else {
      SwingUtilities.invokeLater(() ->
          MainWindow.instance.setVisible(true, true));
      logger.debug("No file directory selected, showing welcome dialog.");
    }

    Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook()));

    if (PropertyManager.getInstance().getPropertyAsBoolean(PropertyManager.PROPERTY_CHECK_FOR_UPDATES)
        && (!VERSION.equals("unknown"))) {
      Thread t = new Thread(new VersionChecker());
      t.setDaemon(true);
      t.start();
    }
  }

  private static void errExit(int exitCode, Exception e) {
    String message;
    String title;
    switch (exitCode) {
      case 1:
        message = "There was an error while trying to read the configuration file\n" +
            Constants.READSY_CONFIG_FILE.getAbsolutePath() +
            "\n\nCan your user create files at this location?";
        title = "Configuration Init Failure";
        break;
      case 2:
        message = "The Desktop API is not supported on this operating system.\n\nIf you are running a Debian or Ubuntu system,\ntry 'sudo apt-get install libgnome2-0'\n\nIf you are running a RedHat system,\ntry 'sudo yum install libgnome'\n\nFor other operating systems, please visit https://jeremybrooks.net/suprsetr/faq.html\n\nThis program will now exit.";
            title = "Desktop API Not Supported";
        break;
      default:
        message = "An unknown error occurred during startup.";
        title = "Startup Error";
    }
    if (logger != null) {
      logger.fatal(message, e);
    }
    JOptionPane.showMessageDialog(null,
        message, title, JOptionPane.ERROR_MESSAGE);
    System.exit(exitCode);
  }
}
