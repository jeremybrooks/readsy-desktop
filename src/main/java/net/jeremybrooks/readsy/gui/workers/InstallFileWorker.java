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
