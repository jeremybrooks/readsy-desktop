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
import net.jeremybrooks.readsy.bo.ReadsyRootElement;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;


/**
 * XML Parsing class.
 * <p/>
 * This class is implemented as a singleton.  It provides methods to parse
 * specific data from an XML file.
 *
 * @author Jeremy Brooks
 */
public class XMLParser extends DefaultHandler {

	/**
	 * Logging object.
	 */
	private static Logger logger = Logger.getLogger(XMLParser.class);

	/**
	 * XML reader.
	 */
	private XMLReader xr;

	/**
	 * Instance of this class.
	 */
	private static XMLParser instance = null;


	/**
	 * Creates a new instance of XMLParser.
	 *
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	private XMLParser() throws ParserConfigurationException, SAXException {
		super();

		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();
		this.xr = sp.getXMLReader();
	}


	/**
	 * Get a reference to the only instance of this class.
	 * If there is an error creating the XML parser, the error will be logged,
	 * and this method will return null.  Therefore, it is a good idea to call
	 * this method during program startup, and check for a null return.
	 *
	 * @return instance of the XMLParser class.
	 */
	public static XMLParser getInstance() {
		if (instance == null) {
			try {
				instance = new XMLParser();
			} catch (Exception e) {
				logger.error("COULD NOT CREATE XML PARSER!", e);
				instance = null;
			}
		}

		return instance;
	}


	/**
	 * Parse the root element from a readsy XML file.
	 * <p/>
	 * This method sets the content handler and delegates the work to the
	 * parse method.
	 *
	 * @param file the file to parse.
	 * @return object representing the root element.
	 * @throws Exception if any errors occur.
	 */
	public ReadsyRootElement parseRootElement(File file) throws Exception {
		RootElementParseHandler handler = new RootElementParseHandler(file);
		this.xr.setContentHandler(handler);
		this.parse(file);

		return handler.getReadsyRootElement();
	}


	/**
	 * Parse a readsy XML file, returning a list of objects representing
	 * all the entry elements in the file.
	 * <p/>
	 * This method sets the content handler and delegates the work to the
	 * parse method.
	 *
	 * @param file the file to parse.
	 * @return List of objects representing the entry elements in the file.
	 * @throws Exception if any errors occur.
	 */
	public List<ReadsyEntryElement> parseEntryElementsToList(File file) throws Exception {
		EntryElementListParseHandler handler = new EntryElementListParseHandler();
		this.xr.setContentHandler(handler);
		this.parse(file);
		return handler.getEntryElementList();
	}


	/**
	 * Do the actual work.
	 * Any errors will be logged.
	 *
	 * @param file the file to parse.
	 * @throws Exception if any errors occur.
	 */
	private void parse(File file) throws Exception {
		this.xr.setErrorHandler(this);
		InputSource inputSource = new InputSource(new FileInputStream(file));
		this.xr.parse(inputSource);

	}


	@Override
	public void warning(SAXParseException sAXParseException) throws SAXException {
		logger.warn("Got a warning during parsing.", sAXParseException);
	}


	@Override
	public void error(SAXParseException sAXParseException) throws SAXException {
		logger.error("Got an error during parsing.", sAXParseException);
	}


	@Override
	public void fatalError(SAXParseException sAXParseException) throws SAXException {
		logger.fatal("Got a fatal error during parsing.", sAXParseException);
	}
}
