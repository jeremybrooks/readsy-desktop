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

package net.jeremybrooks.readsy.bo;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Object representing an entry element of a readsy XML file.
 * <p/>
 * When rendered, the element should look like this:
 * <p/>
 * <entry date="">
 * <heading></heading>
 * <text></text>
 * </entry>
 *
 * @author Jeremy Brooks
 */
public class ReadsyEntryElement {

	/* The date of this entry, in MMDD format. */
	private String date;

	/* Flag indicating if this entry has been read. */
	private boolean read;

	/* The heading element of this entry element. */
	private String heading;

	/* The text element of this entry element. */
	private String text;


	/**
	 * Create a new ReadsyEntryElement and initialize the values.
	 */
	public ReadsyEntryElement() {
		this.date = "";
		this.read = false;
		this.heading = "";
		this.text = "";
	}


	/**
	 * Get the date.
	 *
	 * @return date attribute of this entry element.
	 */
	public String getDate() {
		return date;
	}


	/**
	 * Set the date.
	 * If the date is in yyyyMMDD format, the yyyy portion will be removed.
	 *
	 * @param date the date attribute of this entry element.
	 */
	public void setDate(String date) {
		this.date = date;
	}


	/**
	 * Get the read flag.
	 * <p>Only used to read version 1 files. Not used for version 2.</p>
	 *
	 * @return true if this entry has been read.
	 */
	public boolean isRead() {
		return read;
	}


	/**
	 * Set the read flag.
	 * <p>Only used to read version 1 files. Not used for version 2.</p>
	 *
	 * @param read the status of the read flag.
	 */
	public void setRead(boolean read) {
		this.read = read;
	}


	/**
	 * Get heading.
	 *
	 * @return value of the heading element.
	 */
	public String getHeading() {
		return heading;
	}


	/**
	 * Set heading.
	 *
	 * @param heading the value of the heading element.
	 */
	public void setHeading(String heading) {
		this.heading = StringEscapeUtils.unescapeXml(heading);
	}


	/**
	 * Get text.
	 *
	 * @return value of the text element.
	 */
	public String getText() {
		return text;
	}


	/**
	 * Set text.
	 *
	 * @param text the value to set as the text element.
	 */
	public void setText(String text) {
		this.text = StringEscapeUtils.unescapeXml(text);
	}


	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(this.getClass().getName());
		sb.append("date='").append(date).append('\'');
		sb.append(", read=").append(read);
		sb.append(", heading='").append(heading).append('\'');
		sb.append(", text='").append(text.length()).append("chars'");
		sb.append('}');
		return sb.toString();
	}
}
