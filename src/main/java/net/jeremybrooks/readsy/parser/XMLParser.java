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
