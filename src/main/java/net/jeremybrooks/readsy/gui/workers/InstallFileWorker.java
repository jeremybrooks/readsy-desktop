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

package net.jeremybrooks.readsy.gui.workers;

import net.jeremybrooks.readsy.BitHelper;
import net.jeremybrooks.readsy.DataAccess;
import net.jeremybrooks.readsy.bo.ReadsyDataFile;
import net.jeremybrooks.readsy.bo.ReadsyEntryElement;
import net.jeremybrooks.readsy.gui.WorkerDialog;
import org.apache.log4j.Logger;

import javax.swing.SwingWorker;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * @author Jeremy Brooks
 */
public class InstallFileWorker extends SwingWorker<Void, Void> {
	private Logger logger = Logger.getLogger(InstallFileWorker.class);
	private File file;
	private Exception error;

	public InstallFileWorker(File file) {
		this.file = file;
	}


	@Override
	protected Void doInBackground() throws Exception {
		ResourceBundle bundle = ResourceBundle.getBundle("localization.worker");

		try {
			logger.debug("Installing data file " + file.getAbsolutePath());
			SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

			ReadsyDataFile dataFile = new ReadsyDataFile(file);
			String contentDirectory = dataFile.getReadsyRootElement().getShortDescription();
			if (DataAccess.directoryExists(contentDirectory)) {
				throw new Exception("It appears that the data file has already been installed.");
			}
			DataAccess.createDirectory(contentDirectory);

			int year = dataFile.getReadsyRootElement().getYear();
			BitHelper bitHelper = new BitHelper();
			String calYear;
			int count = 0;
			if (year == 0) {
				// set to a non-leap year; the file will have 365 entries
				calYear = "2013";
			} else {
				// set to the specified year
				calYear = Integer.toString(year);
			}
			for (ReadsyEntryElement entry : dataFile.getEntryList()) {
				setProgress((int)(count/366.0*100));
				bitHelper.setRead(yyyyMMdd.parse(calYear + entry.getDate()), entry.isRead());
				StringBuilder sb = new StringBuilder(entry.getHeading());
				sb.append('\n');
				sb.append(entry.getText());
				String path = contentDirectory + "/" + entry.getDate();

				firePropertyChange(WorkerDialog.EVENT_DIALOG_MESSAGE, "", bundle.getString("ifw.messageCopying") + " " + path);

				DataAccess.saveFile(path, sb.toString().getBytes("UTF-8"));
				count++;
			}

			Properties metadata = new Properties();
			metadata.setProperty("year", Integer.toString(year));
			metadata.setProperty("description", dataFile.getReadsyRootElement().getDescription());
			metadata.setProperty("shortDescription", dataFile.getReadsyRootElement().getShortDescription());
			metadata.setProperty("version", Integer.toString(dataFile.getReadsyRootElement().getVersion()));
			metadata.setProperty("read", bitHelper.toString());
			firePropertyChange(WorkerDialog.EVENT_DIALOG_MESSAGE, "", bundle.getString("ifw.messageCopyingMetadata"));
			DataAccess.saveMetadata(contentDirectory, metadata);
		} catch (Exception e) {
			logger.error("Error installing file " + file.getAbsolutePath(), e);
			this.error = e;
		}
		return null;
	}

	public Exception getError() {
		return this.error;
	}
}
