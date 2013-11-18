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

package net.jeremybrooks.readsy.bo;

import java.io.File;

/**
 * Object representing the root element of a readsy file.
 * <p/>
 * When rendered, the root element should look like this:
 * <p/>
 * <document description="" shortDescription="" year="" version=""/>
 * <p/>
 * Note that there is a file attribute in here, but that is strictly for
 * convenience.  It is not rendered to the XML file.
 *
 * @author Jeremy Brooks
 */
public class ReadsyRootElement {

	/* The file we were read from. */
	private File file = null;

	/* The description attribute of the root element. */
	private String description = "";

	/* The shortDescription attribute of the root element. */
	private String shortDescription = "";

	/* The ignoreYear attribute of the root element. */
	private boolean ignoreYear = false;

	/* The year attribute of the root element. */
	private int year;

	/* The file version. */
	private int version;

	/**
	 * Creates a new instance of ReadsyRootElement.
	 */
	public ReadsyRootElement() {
	}


	/**
	 * Get file.
	 *
	 * @return the file this root element was found in.
	 */
	public File getFile() {
		return file;
	}


	/**
	 * Set file.
	 *
	 * @param file set the file attribute.
	 */
	public void setFile(File file) {
		this.file = file;
	}


	/**
	 * Get description.
	 *
	 * @return value of the description attribute.
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * Set description.
	 *
	 * @param description the value of the description attribute.
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	/**
	 * Get short description.
	 *
	 * @return value of the shortDescription attribute.
	 */
	public String getShortDescription() {
		return shortDescription;
	}


	/**
	 * Set short description.
	 *
	 * @param shortDescription the value of the shortDescription attribute.
	 */
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}


	/**
	 * Get state of the ignoreYear flag.
	 *
	 * @return true if the year part of the date should be ignored.
	 */
	public boolean isIgnoreYear() {
		return ignoreYear;
	}


	/**
	 * Set state of the ignoreYear flag.
	 *
	 * @param ignoreYear value of the ignoreYear attribute.
	 */
	public void setIgnoreYear(boolean ignoreYear) {
		this.ignoreYear = ignoreYear;
	}


	/**
	 * The year this file is good for. Zero indicates any year.
	 *
	 * @return year this file is used for, or zero for any year.
	 */
	public int getYear() {
		return year;
	}


	/**
	 * Set the year this file is used for.
	 * Calling this method with a negative value will set the year to zero.
	 *
	 * @param year the year this file is used for. Zero indicates any year.
	 */
	public void setYear(int year) {
		if (year >= 0) {
			this.year = year;
		} else {
			this.year = 0;
		}
	}

	/**
	 * Get the major version of readsy that this file is compatible with.
	 *
	 * @return compatibility version.
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Set the major version of readsy that this file is compatible with.
	 *
	 * @param version compatibility version of this file.
	 */
	public void setVersion(int version) {
		this.version = version;
	}
}
