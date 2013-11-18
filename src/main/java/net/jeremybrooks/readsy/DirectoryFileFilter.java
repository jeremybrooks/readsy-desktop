/*
 * readsy - read something new every day
 *
 *     Copyright (C) 2103  Jeremy Brooks
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
 *     You may contact the program's author at jeremyb@whirljack.net
 */

package net.jeremybrooks.readsy;

import java.io.File;

public class DirectoryFileFilter implements java.io.FileFilter {
	public boolean accept(File pathname) {
		return pathname != null && pathname.isDirectory();
	}

}