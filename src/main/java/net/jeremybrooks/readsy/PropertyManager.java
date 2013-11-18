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

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;


/**
 * Manage properties for the application.
 * Other classes can set and get persistent properties using this class.
 * This class is implemented as a Singleton.
 *
 * @author Jeremy Brooks
 */
public class PropertyManager {

	private Logger logger = Logger.getLogger(PropertyManager.class);
	private static PropertyManager instance = null;
	private Properties props = new Properties();
	private Properties secretProps = new Properties();



	/**
	 * Property for automatic update check.
	 */
	public static final String PROPERTY_CHECK_FOR_UPDATES = "readsy.checkForUpdates";

	/**
	 * Property for font size.
	 */
	public static final String PROPERTY_FONT_SIZE = "readsy.fontSize";

	/**
	 * Property for window width.
	 */
	public static final String PROPERTY_WINDOW_WIDTH = "readsy.windowWidth";

	/**
	 * Property for window height.
	 */
	public static final String PROPERTY_WINDOW_HEIGHT = "readsy.windowHeight";

	/**
	 * Property for window x position.
	 */
	public static final String PROPERTY_WINDOW_X = "readsy.windowX";

	/**
	 * Property for window y position.
	 */
	public static final String PROPERTY_WINDOW_Y = "readsy.windowY";

	public static final String DROPBOX_APP_KEY = "readsy.dropboxAppKey";
	public static final String DROPBOX_APP_SECRET = "readsy.dropboxAppSecret";
	public static final String DROPBOX_ENABLED = "readsy.dropboxEnabled";
	public static final String DROPBOX_ACCESS_TOKEN = "readsy.dropboxAccessToken";


	/**
	 * Creates a new instance of PropertyManager
	 */
	private PropertyManager() {
	}


	/**
	 * Get a reference to the only instance of this class.
	 *
	 * @return reference to the instance of PropertyManager.
	 */
	public static PropertyManager getInstance() {
		if (instance == null) {
			instance = new PropertyManager();
		}

		return instance;
	}


	/**
	 * Initialize the properties.
	 * This method must be called at least once before attempting to get or set
	 * properties.  This ensures that the data file exists and is usable.
	 *
	 * @throws Exception if the properties cannot be loaded or created, or
	 *                   if any unexpected error occurs.
	 */
	public void init() throws Exception {
		// LOAD CONFIGURATION IF POSSIBLE
		InputStream in = null;
		File configFile = new File(Readsy.getDataDir(), "readsy.properties");
		try {
			if (!configFile.exists()) {
				configFile.createNewFile();
				String logfile = (new File(Readsy.getDataDir(), "readsy.log")).getAbsolutePath();
				props = new Properties();
				// SET THE DEFAULT CHECK FOR UPDATES PROPERTY
				props.setProperty(PROPERTY_CHECK_FOR_UPDATES, "true");

				// SET LOGGING PROPERTIES
				props.setProperty("log4j.rootLogger", "DEBUG, FILE");
				props.setProperty("log4j.appender.FILE", "org.apache.log4j.RollingFileAppender");
				props.setProperty("log4j.appender.FILE.Threshold", "DEBUG");
				props.setProperty("log4j.appender.FILE.file", logfile);
				props.setProperty("log4j.appender.FILE.layout", "org.apache.log4j.PatternLayout");
				props.setProperty("log4j.appender.FILE.layout.ConversionPattern", "%5p %c [%t] %d{ISO8601} - %m%n");
				props.setProperty("log4j.appender.FILE.MaxFileSize", "2000KB");
				props.setProperty("log4j.appender.FILE.MaxBackupIndex", "5");

				props.setProperty(DROPBOX_ENABLED, "false");

				// SAVE THE NEW CONFIGURATION FILE
				this.saveProperties();
			}

			in = new FileInputStream(configFile);
			props.load(in);

			// SET THE DEFAULT FONT SIZE IF NEEDED
			if (props.getProperty(PropertyManager.PROPERTY_FONT_SIZE) == null) {
				props.setProperty(PropertyManager.PROPERTY_FONT_SIZE, "12");
				this.saveProperties();
			}

			// SET WINDOW POSITION AND SIZE PROPERTIES
			if (props.getProperty(PROPERTY_WINDOW_HEIGHT) == null) {
				java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
				props.setProperty(PROPERTY_WINDOW_X, Integer.toString((screenSize.width - 600) / 2));
				props.setProperty(PROPERTY_WINDOW_Y, Integer.toString((screenSize.height - 426) / 2));
				props.setProperty(PROPERTY_WINDOW_WIDTH, "600");
				props.setProperty(PROPERTY_WINDOW_HEIGHT, "426");
				this.saveProperties();
			}

			// SET DROPBOX ENABLED IF NEEDED
			if (props.getProperty(DROPBOX_ENABLED) == null) {
				props.setProperty(DROPBOX_ENABLED, "false");
				this.saveProperties();
			}

			// LOAD SECRET PROPERTIES
			secretProps.load(Readsy.class.getResourceAsStream("/secret.properties"));

		} finally {
			FileUtil.close(in);
		}
	}


	/**
	 * Get the current properties.
	 *
	 * @return the current application properties.
	 */
	public Properties getProperties() {
		return this.props;
	}


	/**
	 * Persist the current settings.
	 * Any errors will be logged.
	 */
	private void saveProperties() {
		OutputStream out = null;

		try {
			File configFile = new File(Readsy.getDataDir(), "readsy.properties");
			out = new FileOutputStream(configFile);
			props.store(out, "readsy configuration file	");

		} catch (Exception e) {
			logger.error("COULD NOT SAVE PROPERTIES.", e);
		} finally {
			FileUtil.close(out);
		}
	}


	/**
	 * Get the named property as a String.
	 *
	 * @param name name of the property to return.
	 * @return the named property, or null if the property does not exist.
	 */
	public String getProperty(String name) {
		String value = this.props.getProperty(name);
		if (value == null) {
			value = this.secretProps.getProperty(name);
		}
		return value;
	}



	/**
	 * Get the named property as a boolean.
	 *
	 * @param name name of the property to return.
	 * @return true if the value of the property is "true" or "yes", otherwise
	 *         returns false.
	 */
	public boolean getPropertyAsBoolean(String name) {
		boolean retval = false;

		String val = this.props.getProperty(name);
		if (val != null) {
			if (val.equalsIgnoreCase("true") || val.equalsIgnoreCase("yes")) {
				retval = true;
			}
		}

		return retval;
	}


	/**
	 * Gets the named property as an int.
	 *
	 * @param name name of the property to return.
	 * @return value of the property as an int. If the property does not exist,
	 *         or is not a valid float value, 0 is returned.
	 */
	public int getPropertyAsInt(String name) {
		int ret;

		try {
			ret = Integer.valueOf(this.props.getProperty(name));
		} catch (Exception e) {
			ret = 0;
		}

		return ret;
	}


	/**
	 * Set the property.
	 * The properties will be saved.
	 *
	 * @param name  name of the property.
	 * @param value value of the property.
	 */
	public void setProperty(String name, String value) {
		this.props.setProperty(name, value);
		this.saveProperties();
	}

	public void deleteProperty(String name) {
		this.props.remove(name);
		this.saveProperties();
	}
}
