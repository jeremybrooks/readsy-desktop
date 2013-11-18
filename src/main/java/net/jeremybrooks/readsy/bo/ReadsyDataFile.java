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

import net.jeremybrooks.readsy.FileUtil;
import net.jeremybrooks.readsy.parser.XMLParser;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates an XML data file for readsy.
 *
 * @author Jeremy Brooks
 */
public class ReadsyDataFile {

	/**
	 * The XML element for the output file.
	 */
	private static final String XML_ELEMENT = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

	/**
	 * Logging.
	 */
	private Logger logger = Logger.getLogger(ReadsyDataFile.class);

	/**
	 * Object representing the root element of our file.
	 */
	private ReadsyRootElement root;

	/**
	 * List of objects representing entry elements in our file.
	 */
	private List<ReadsyEntryElement> entryList = new ArrayList<>();

	/**
	 * Map dates to entries.
	 */
	private Map<String, ReadsyEntryElement> dateMap = new HashMap<>();

	/**
	 * The current element we are looking at.
	 */
	private int index = 0;

	/**
	 * Do date formatting to/from MMDD format.
	 */
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("MMdd");


	/* Require usage of the public constructor. */
	private ReadsyDataFile() {
	}


	/**
	 * Creates a new instance of ReadsyDataFile.
	 * Use this constructor when you want an empty file to put data into.
	 * <p/>
	 * The file will be created and the data written to file.  However, all the
	 * entry's will be empty to begin with.
	 *
	 * @param year             the year that the data is good for.
	 * @param file             the filename to create.
	 * @param description      the description attribute of the root element.
	 * @param shortDescription the shortDescription attribute of the root element.
	 * @param ignoreYear       the ignoreYear attribute of the root element.
	 * @throws IOException if there is an error writing the file.
	 */
	public ReadsyDataFile(int year, File file, String description, String shortDescription, boolean ignoreYear)
			throws IOException {

		// CREATE AND INITIALIZE THE ROOT ELEMENT OBJECT
		this.root = new ReadsyRootElement();
		this.root.setFile(file);
		this.root.setDescription(description);
		this.root.setShortDescription(shortDescription);
		this.root.setIgnoreYear(ignoreYear);

		// if data file should be for ANY year, set it to a year that is not a leap year
		if (year == 0) {
			year = 2013;
		}

		// CREATE A CALENDAR TO LOOP THROUGH THE DAYS
		Calendar cal = new GregorianCalendar();
		cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
		cal.set(GregorianCalendar.MONTH, 0);
		cal.set(GregorianCalendar.YEAR, year);

		// INITIALIZE ENTRY OBJECTS, ONE FOR EACH DAY
		while ((cal.get(GregorianCalendar.YEAR)) == year) {

			ReadsyEntryElement entry = new ReadsyEntryElement();
			entry.setDate(this.dateFormatter.format(cal.getTime()));
			this.entryList.add(entry);
			cal.add(GregorianCalendar.DAY_OF_MONTH, 1);
		}

		// CREATE THE FILE
		write();
	}


	/**
	 * Creates a new instance of ReadsyDataFile.
	 * Use this constructor when you want to read the contents of a file
	 * that already exists.
	 *
	 * @param file the file to read data from.
	 * @throws Exception if the file does not exist, or if the file cannot
	 *                   be parsed.
	 */
	public ReadsyDataFile(File file) throws Exception {
		this.root = XMLParser.getInstance().parseRootElement(file);
		this.entryList = XMLParser.getInstance().parseEntryElementsToList(file);

		// if the year is Integer.MAX_VALUE, the content was read from a version 1 file
		// and we need to set the year by parsing yyyy from the first entry element
		if (this.root.getYear() == Integer.MAX_VALUE) {
			this.root.setYear(Integer.parseInt(this.entryList.get(0).getDate().substring(0, 4)));
		}
		for (ReadsyEntryElement element : this.entryList) {
			// convert yyyyMMdd dates to MMdd
			if (element.getDate().length() == 8) {
				element.setDate(element.getDate().substring(4));
			}
			this.dateMap.put(element.getDate(), element);
		}
	}


	/**
	 * Move the pointer to the next entry in the list.
	 * If the end of the list has been reached, the index will be pointing to
	 * the first object in the list.
	 */
	public void incrementIndex() {
		this.index++;
		if (this.index == this.entryList.size()) {
			this.index = 0;
		}
	}


	/**
	 * Move the pointer to the previous entry in the list.
	 * When the pointer is moved past the first object in the list, it will
	 * automatically wrap around to the last object in the list.
	 */
	public void decrementIndex() {
		this.index--;
		if (this.index < 0) {
			this.index = this.entryList.size() - 1;
		}
	}


	/**
	 * Get the current entry, based on the index.
	 *
	 * @return currently indexed entry.
	 */
	public ReadsyEntryElement getCurrentEntry() {
		return this.entryList.get(index);
	}


	/**
	 * Get the object representing the root element.
	 *
	 * @return object representing the root element.
	 */
	public ReadsyRootElement getReadsyRootElement() {
		return this.root;
	}


	/**
	 * Write the file.
	 * The current contents of this object will be written to the file specified
	 * when the object was created.  If the file already exists, it will be
	 * replaced.
	 *
	 * @throws IOException if any errors occur.
	 */
	public void write() throws IOException {
		// force version 1, which is the version this method writes
		this.root.setVersion(1);

		File f = this.root.getFile();
		BufferedWriter out = null;
		logger.debug("File is " + f.getAbsolutePath());
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));

			// THE FIRST PART OF THE FILE
			out.write(XML_ELEMENT);
			out.write('\n');
			out.write("<document description=\"");
			out.write(this.root.getDescription());
			out.write("\" shortDescription=\"");
			out.write(this.root.getShortDescription());
			out.write("\" year=\"");
			out.write((new Integer(this.root.getYear())).toString());
			out.write("\" version=\"");
			out.write((new Integer(this.root.getVersion())).toString());
			out.write("\">");
			out.write('\n');
			logger.debug("Ready to write " + this.entryList.size() + " records.");
			for (ReadsyEntryElement entry : this.entryList) {
				out.write("<entry date=\"");
				out.write(entry.getDate());
				out.write("\">");
				out.write('\n');
				out.write("<heading>");
				out.write(StringEscapeUtils.escapeXml(entry.getHeading()));
				out.write("</heading>");
				out.write('\n');
				out.write("<text>");
				out.write(StringEscapeUtils.escapeXml(entry.getText()));
				out.write("</text>");
				out.write('\n');
				out.write("</entry>");
				out.write('\n');
			}

			logger.debug("Wrote entry list successfully.");

			out.write("</document>");
			out.write('\n');

			out.flush();

		} catch (Exception e) {
			logger.error("ERROR WRITING FILE " + f.getAbsolutePath(), e);
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			throw new IOException("Error writing file " + f.getAbsolutePath()
					+ sw.toString());

		} finally {
			FileUtil.close(out);
		}
	}

	public List<ReadsyEntryElement> getEntryList() {
		return this.entryList;
	}
}
