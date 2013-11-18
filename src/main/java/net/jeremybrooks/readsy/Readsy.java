/*
 * Copyright (c) 2013, Jeremy Brooks
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */

package net.jeremybrooks.readsy;

import net.jeremybrooks.readsy.gui.MainWindow;
import net.jeremybrooks.readsy.gui.WelcomeDialog;
import net.jeremybrooks.readsy.parser.XMLParser;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.swing.JOptionPane;
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
		if (System.getProperty("os.name").contains("Mac")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
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
		boolean firstRun = false;
		try {
			if (!contentDir.exists()) {
				if (!contentDir.mkdirs()) {
					throw new Exception("Unable to create content directory " + contentDir.getAbsolutePath());
				}
				firstRun = true;
			}
			PropertyManager.getInstance().init();
			PropertyConfigurator.configure(PropertyManager.getInstance().getProperties());
			logger = Logger.getLogger(Readsy.class);
			logger.debug("Logging configured.");

			if (XMLParser.getInstance() == null) {
				throw new Exception("Could not initialize XML Parser.");
			}
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
		if (firstRun) {
			logger.debug("First run; showing welcome dialog.");
			new WelcomeDialog().setVisible(true);
		} else {
			mainWindow.setVisible(true, true);
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
