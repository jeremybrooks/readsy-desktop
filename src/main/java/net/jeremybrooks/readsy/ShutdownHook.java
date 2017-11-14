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

import net.jeremybrooks.readsy.gui.MainWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class will run when the JVM is shutting down.
 *
 * This is used to save the window position at shutdown.
 *
 * @author Jeremy Brooks
 */
public class ShutdownHook implements Runnable {

	private MainWindow mainWindow;
	private Logger logger = LogManager.getLogger(ShutdownHook.class);

	/**
	 * Constructor.
	 *
	 * @param mainWindow reference to the main window.
	 */
	public ShutdownHook(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}


	/**
	 * Tasks to execute at shutdown.
	 */
	public void run() {
		logger.info("Exiting");
		this.mainWindow.savePositionAndSize();
	}
}
