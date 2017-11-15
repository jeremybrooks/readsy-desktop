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

import net.jeremybrooks.common.util.IOUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

	private Logger logger = LogManager.getLogger(PropertyManager.class);
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
				props = new Properties();
				// SET THE DEFAULT CHECK FOR UPDATES PROPERTY
				props.setProperty(PROPERTY_CHECK_FOR_UPDATES, "true");

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

			// LOAD SECRET PROPERTIES
			secretProps.load(Readsy.class.getResourceAsStream("/secret.properties"));

		} finally {
			IOUtil.close(in);
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
			IOUtil.close(out);
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
