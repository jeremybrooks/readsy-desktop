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
