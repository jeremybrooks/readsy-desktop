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
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

import static net.jeremybrooks.readsy.Constants.VERSION_URL;

/**
 * Check for a new version of the program.
 *
 * @author Jeremy Brooks
 */
public class VersionChecker implements Runnable {

	private static final Logger logger = LogManager.getLogger();


	/**
	 * Run loop for the Runnable.
	 * <p>This method will check to see if there is a new version available.
	 * It runs as a separate Thread so that it will not block the GUI if the
	 * network connection is slow or missing.</p>
	 */
	public void run() {
		HttpURLConnection conn = null;
		String latestVersion;
		try {
      // WAIT A LITTLE BIT TO MAKE SURE THE MAIN WINDOW HAS BEEN CREATED
      Thread.sleep(2000);
      conn = (HttpURLConnection) new URL(VERSION_URL).openConnection();
		  try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
        latestVersion = in.readLine();
      }
		  logger.debug("Got version {} from {}", latestVersion, VERSION_URL);
			if (this.updated(Readsy.VERSION, latestVersion)) {
				logger.debug("New version is available.");
				MainWindow.instance.newVersionAvailable();
			}
		} catch (Exception e) {
			logger.warn("ERROR WHILE CHECKING FOR A NEW VERSION.", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	/**
	 * Compare a new version with an old version to see if there is an update available.
	 *
	 * <p>If the parameters are null or empty, this method will return false. Each part of the version
	 * must be an Integer value. So "1.0.0" is a valid version; "1.0.0a" is not.</p>
	 *
	 * <p>The versions are compared by splitting on the "." and then comparing each unit.
	 * If the "old" unit is greater than the "new" unit, comparison stops and returns false.
	 * If the "new" unit is greater than the "old" unit, comparison stops and returns true.</p>
	 *
	 * @param oldVersion the old (or current) version.
	 * @param newVersion the new version to compare with old version.
	 * @return true if newVersion is greater than oldVersion.
	 */
	public boolean updated(String oldVersion, String newVersion) {
		if (StringUtils.isEmpty(oldVersion)) {
			return false;
		}
		if (StringUtils.isEmpty(newVersion)) {
			return false;
		}
		if (oldVersion.equals(newVersion)) {
			return false;
		}

		boolean updated = false;

		StringTokenizer oldT = new StringTokenizer(oldVersion, ".");
		StringTokenizer newT = new StringTokenizer(newVersion, ".");

		try {
			while (oldT.hasMoreTokens() && newT.hasMoreTokens()) {
				Integer oldPart = Integer.decode(oldT.nextToken());
				Integer newPart = Integer.decode(newT.nextToken());
				if (oldPart > newPart) {
					updated = false;
					break;
				}
				if (newPart > oldPart) {
					updated = true;
					break;
				}
			}
		} catch (Exception e) {
			updated = false;
			logger.warn("Error trying to determine version [oldVersion={}] [newVersion={}]. Returning false.", oldVersion, newVersion);
		}

		return updated;
	}
}
