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

import net.jeremybrooks.readsy.bo.ReadsyRootElement;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;


/**
 * Parse a readsy XML file, finding the root element.
 *
 * @author Jeremy Brooks
 */
public class RootElementParseHandler extends DefaultHandler {

	/* Object representing the root element. */
	private ReadsyRootElement root = null;
	/* The file to parse. */
	private File file = null;

	private RootElementParseHandler() {
	}

	/**
	 * Creates a new instance of RootElementParseHandler.
	 *
	 * @param filename the file to parse.
	 */
	public RootElementParseHandler(File filename) {
		this.file = filename;
	}

	/**
	 * Handle a start element event.
	 * We only care about the "document" element.  When it is found,
	 * we set the attributes in our root element object, and that's it.
	 */
	@Override
	public void startElement(
			String uri,
			String localName,
			String qName, org.xml.sax.Attributes attributes)
			throws org.xml.sax.SAXException {

		if (qName.equals("document")) {
			this.root = new ReadsyRootElement();
			this.root.setFile(this.file);
			this.root.setDescription(attributes.getValue("description"));
			this.root.setShortDescription(attributes.getValue("shortDescription"));

			// if this is a scrip du jour data file, the ignoreYear attribute will exist
			// if this attribute exists, and the value is "true", set year to zero
			// if this attribute exists, and the value is "false", set year to Integer.MAX_VALUE
			// Integer.MAX_VALUE will cause the year to get parsed from the date attribute in an entry element.
			String value = attributes.getValue("ignoreYear");
			if (value != null) {
				if (Boolean.parseBoolean(value)) {
					this.root.setYear(0);
				} else {
					this.root.setYear(Integer.MAX_VALUE);    // flag used later in the process
				}
			}

			// year and version are attributes in readsy data files, so check that they exist before setting the value.
			value = attributes.getValue("year");
			if (value != null) {
				this.root.setYear(Integer.parseInt(value));
			}

			value = attributes.getValue("version");
			if (value != null) {
				this.root.setVersion(Integer.parseInt(value));
			}
		}
	}

	/**
	 * Get the object representing the root element.
	 *
	 * @return object representing the root element.
	 */
	public ReadsyRootElement getReadsyRootElement() {
		return this.root;
	}
}
