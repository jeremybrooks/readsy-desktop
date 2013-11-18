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
