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

import net.jeremybrooks.readsy.DirectoryFileFilter;
import net.jeremybrooks.readsy.Readsy;
import net.jeremybrooks.readsy.dropbox.DropboxHelper;
import net.jeremybrooks.readsy.gui.WorkerDialog;
import org.apache.log4j.Logger;

import javax.swing.SwingWorker;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author Jeremy Brooks
 */
public class DropboxCopyWorker extends SwingWorker<Void, Void> {

	private Logger logger = Logger.getLogger(DropboxCopyWorker.class);

	private Exception exception;

	@Override
	protected Void doInBackground() throws Exception {
		ResourceBundle bundle = ResourceBundle.getBundle("localization.worker");
		try {
			File[] sources = Readsy.getContentDir().listFiles(new DirectoryFileFilter());
			if (sources != null) {
				DropboxHelper dbx = DropboxHelper.getInstance();
				Map<String, Integer> countMap = new HashMap<>();

				// count files
				int total = 0;
				double count = 0;
				for (File source : sources) {
					File[] files = source.listFiles();
					if (files != null) {
						countMap.put(source.getName(), files.length);
						total += files.length;
					}
				}

				// upload files
				for (File source : sources) {
					firePropertyChange(WorkerDialog.EVENT_DIALOG_TITLE, "", bundle.getString("dcw.titleCopying") + " " + source.getName());
					String dbxDir = "/" + source.getName();
					if (dbx.pathExists(dbxDir)) {
						count += countMap.get(source.getName());
						firePropertyChange(WorkerDialog.EVENT_DIALOG_MESSAGE, "", dbxDir + " " + bundle.getString("dcw.messageExists"));
						setProgress((int) (count / total * 100));
					} else {
						firePropertyChange(WorkerDialog.EVENT_DIALOG_MESSAGE, "", bundle.getString("dcw.messageCreating") + " " + dbxDir);
						dbx.createFolder(dbxDir);

						String dbxPath = dbxDir + "/metadata";
						dbx.uploadFile(dbxPath, new File(source, "metadata"));
						count++;
						setProgress((int) (count / total * 100));

						for (File file : source.listFiles()) {
							dbxPath = dbxDir + "/" + file.getName();
							logger.debug("Uploading " + dbxPath);
							firePropertyChange(WorkerDialog.EVENT_DIALOG_MESSAGE, "", bundle.getString("dcw.messageUploading") + " " + dbxPath);
							dbx.uploadFile(dbxPath, file);
							count++;
							setProgress((int) (count / total * 100));
						}
					}
				}
			}
		} catch (Exception e) {
			this.exception = e;
		}

		return null;
	}

	public Exception getException() {
		return this.exception;
	}
}
