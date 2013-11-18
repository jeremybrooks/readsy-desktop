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
