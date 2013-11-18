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

package net.jeremybrooks.readsy.parser;

import net.jeremybrooks.readsy.bo.ReadsyEntryElement;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Parse a readsy XML file, locating an element for a specific date.
 *
 * @author Jeremy Brooks
 */
public class EntryElementParseHandler extends DefaultHandler {

	/* Logging. */
	private Logger logger = Logger.getLogger(EntryElementParseHandler.class);

	/* Object representing the entry element. */
	private ReadsyEntryElement entry;

	/* The date we are looking for in yyyyMMdd format. */
	private String targetDate;

	/* Flag indicating that we found the correct element. */
	private boolean foundDate = false;

	/* Flag indicating that we are in the heading. */
	private boolean inHeading = false;

	/* Flag indicating that we are in the text. */
	private boolean inText = false;

	/* Holds character data. */
	private StringBuffer buf = new StringBuffer();

	/*
	 * Default constructor is private.
	 */
	private EntryElementParseHandler() {
	}


	/**
	 * @param date the date to search for.
	 */
	public EntryElementParseHandler(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("MMdd");
		this.targetDate = format.format(date);
		this.logger.debug("Searching for date " + targetDate);

		this.entry = new ReadsyEntryElement();
		this.entry.setHeading("No data found for " + targetDate);
	}


	/**
	 * Handle an end element event.
	 * <p/>
	 * If we have not found the correct date yet, nothing happens.
	 * If we have found the correct date:
	 * If the end of "heading" has been reached, the inHeading flag is
	 * unset, the entry heading attribute is filled with the contents
	 * of the StringBuffer, and the StringBuffer is cleared.
	 * <p/>
	 * If the end of "test" has been reached, the inText flag is
	 * unset, the entry text attribute is filled with the contents
	 * of the StringBuffer, and the StringBuffer is cleared.
	 * <p/>
	 * If the end of "entry" has been reached, we set the foundDate
	 * flag to false.  This prevents the now correct data in the
	 * entry object from being overwritten, and allows us to skip some time
	 * consuming processing while parsing the rest of the XML file.
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// IF WE ARE IN THE CORRECT ENTRY, SET HEADING AND TEXT  AT THE
		// END OF THE ELEMENTS
		if (this.foundDate) {
			switch (qName) {
				case "heading":
					this.inHeading = false;
					this.entry.setHeading(this.buf.toString());
					this.buf.delete(0, this.buf.length());

					break;
				case "text":
					this.inText = false;
					this.entry.setText(this.buf.toString());
					this.buf.delete(0, this.buf.length());

					break;
				case "entry":
					this.foundDate = false;
					break;
			}
		}

	}


	/**
	 * Handle a start of element event.
	 * <p/>
	 * If the date has been found, and we are in "heading" or "text",
	 * the flag is set so that we can gather character data.
	 * <p/>
	 * If the date has not been found, and we are at an "entry" element,
	 * the date attribute of the entry element is checked against our target
	 * date (taking into consideration the ignoreYear flag).  If the dates
	 * match, we set the foundDate flag, set the read flag in the entry object,
	 * and set the date in the entry object.
	 */
	@Override
	public void startElement(
			String uri,
			String localName,
			String qName, org.xml.sax.Attributes attributes)
			throws SAXException {
		if (this.foundDate) {
			if (qName.equals("heading")) {
				this.inHeading = true;
			} else if (qName.equals("text")) {
				this.inText = true;
			}
		} else {
			if (qName.equals("entry")) {
				String date = attributes.getValue("date");
				if (date.equals(this.targetDate)) {
					this.foundDate = true;
					this.entry.setDate(attributes.getValue("date"));
					String value = attributes.getValue("read");
					if (value != null) {
						this.entry.setRead(Boolean.parseBoolean(value));
					}
					this.logger.debug("Found target date " + targetDate);
				}
			}
		}
	}


	/**
	 * Handle character data.
	 */
	@Override
	public void characters(char ch[], int start, int length)
			throws SAXException {
		if (this.inText || this.inHeading) {
			for (int i = start; i < start + length; i++) {
				this.buf.append(ch[i]);
			}
		}
	}

	/**
	 * Get the entry element object.
	 *
	 * @return entry element object for the requested date.
	 */
	public ReadsyEntryElement getReadsyEntryElement() {
		return this.entry;
	}
}
