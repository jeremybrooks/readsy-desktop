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

package net.jeremybrooks.readsy.gui;

import javax.swing.filechooser.FileFilter;

/**
 * An implementation of FileFilter for use in JFileChoosers.
 * This implementation allows files that end with "*.xml".
 *
 * @author Jeremy Brooks
 */
public class SwingReadsyFileFilter extends FileFilter {

	/**
	 * The extension to allow.
	 */
	private final String extension = ".readsy";

	/**
	 * Creates a new instance of SwingReadsyFileFilter
	 */
	public SwingReadsyFileFilter() {
	}


	/**
	 * Implement the accept method.
	 *
	 * @param f the file to compare.
	 * @return
	 */
	public boolean accept(java.io.File f) {
		boolean ret = false;
		if (f != null) {
			if (f.isDirectory()) {
				ret = true;
			} else if (f.getName().toLowerCase().endsWith(this.extension)) {
				ret = true;
			}
		}

		return ret;
	}


	/**
	 * Get description of allowed files.
	 *
	 * @return description.
	 */
	public String getDescription() {
		return "readsy files (*.readsy)";
	}
}
