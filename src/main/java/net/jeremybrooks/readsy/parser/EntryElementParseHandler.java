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
