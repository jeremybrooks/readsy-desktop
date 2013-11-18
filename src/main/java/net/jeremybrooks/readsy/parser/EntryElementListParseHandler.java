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
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * Parse a readsy XML file, building a List of objects representing
 * the Entry elements in the file.
 *
 * @author Jeremy Brooks
 */
public class EntryElementListParseHandler extends DefaultHandler {

	/**
	 * Object representing one entry element.
	 */
	private ReadsyEntryElement entry;

	/**
	 * The list of ReadsyEntryElement objects.
	 */
	private List<ReadsyEntryElement> entryList;

	/**
	 * Flag indicating that we are currently in the Heading element.
	 */
	private boolean inHeading = false;

	/**
	 * Flag indicating that we are currently in the Text element.
	 */
	private boolean inText = false;

	/**
	 * Holds character data.
	 */
	private StringBuffer buf = new StringBuffer();


	/**
	 * Creates a new instance of DataFileSummaryHandler.
	 */
	public EntryElementListParseHandler() {
		this.entryList = new ArrayList<>();

		this.entry = new ReadsyEntryElement();
	}

	/**
	 * Handle an end element event.
	 * At the end of the "heading" element, we unset the inHeading flag, put
	 * the contents of the StringBuffer in the heading attribute of our
	 * entry object, and clear the StringBuffer.
	 * <p/>
	 * At the end of the "text" element, we unset the inText flag, put
	 * the contents of the StringBuffer in the text attribute of our
	 * entry object, and clear the StringBuffer.
	 * <p/>
	 * At the end of the "entry" element, we add our entry object to the
	 * entry list, and create a new, empty element object.
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

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
				this.entryList.add(this.entry);
				this.entry = new ReadsyEntryElement();
				break;
		}


	}

	/**
	 * Handle a start element event.
	 * <p/>
	 * At the start of an entry element, we set the date and read attributes
	 * in our Entry object.
	 * <p/>
	 * At the start of a "heading" element, we set the inHeading flag.
	 * At the start of a "text" element, we set the inText flag.
	 */
	@Override
	public void startElement(
			String uri,
			String localName,
			String qName, org.xml.sax.Attributes attributes)
			throws SAXException {

		switch (qName) {
			case "entry":
				this.entry.setDate(attributes.getValue("date"));
				String value = attributes.getValue("read");
				this.entry.setRead(Boolean.parseBoolean(value));
				break;
			case "heading":
				this.inHeading = true;
				break;
			case "text":
				this.inText = true;
				break;
		}
	}


	/**
	 * Handle character data.
	 * <p/>
	 * The incoming character data is placed in the StringBuffer buf, if we
	 * are currently in either the "heading" or "text" elements.
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
	 * Get the list of entry elements that has been built.
	 *
	 * @return list of Entry objects.
	 */
	public List<ReadsyEntryElement> getEntryElementList() {
		return this.entryList;
	}
}
