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

import net.jeremybrooks.readsy.gui.MainWindow;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Check for a new version of the program.
 *
 * @author Jeremy Brooks
 */
public class VersionChecker implements Runnable {

	/**
	 * Logging.
	 */
	private Logger logger = Logger.getLogger(VersionChecker.class);

	/**
	 * The URL where version info is found.
	 */
	private static final String VERSION_URL = Readsy.HOME_PAGE + "/VERSION";

	/**
	 * Reference to the main window.
	 */
	private MainWindow mainWindow = null;


	/**
	 * Creates a new instance of VersionChecker.
	 *
	 * @param mainWindow reference to the applications main window.
	 */
	public VersionChecker(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}


	/**
	 * Run loop for the Runnable.
	 * <p/>
	 * <p>This method will check to see if there is a new version available.
	 * It runs as a separate Thread so that it will not block the GUI if the
	 * network connection is slow or missing.</p>
	 */
	public void run() {
		HttpURLConnection conn = null;
		BufferedReader in = null;
		String latestVersion;


		try {
			// WAIT A LITTLE BIT TO MAKE SURE THE MAIN WINDOW HAS BEEN CREATED
			Thread.sleep(2000);

			// GET THE VERSION WEB PAGE
			conn = (HttpURLConnection) new URL(VERSION_URL).openConnection();
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			latestVersion = in.readLine();

			logger.debug("Got version " + latestVersion + " from " + VERSION_URL);

			if (latestVersion.compareTo(Readsy.VERSION) == 1) {
				mainWindow.newVersionAvailable();
			}

		} catch (Exception e) {
			logger.warn("ERROR WHILE CHECKING FOR A NEW VERSION.", e);
		} finally {
			FileUtil.close(in);
			if (conn != null) {
				conn.disconnect();
			}
		}

	}
}
