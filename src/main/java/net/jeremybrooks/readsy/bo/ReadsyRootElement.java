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
