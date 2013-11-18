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
