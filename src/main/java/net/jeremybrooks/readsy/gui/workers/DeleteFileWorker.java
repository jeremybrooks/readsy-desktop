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

import net.jeremybrooks.readsy.DataAccess;
import net.jeremybrooks.readsy.gui.WorkerDialog;
import org.apache.log4j.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * @author Jeremy Brooks
 */
public class DeleteFileWorker extends SwingWorker<Void, Void> {
	private Logger logger = Logger.getLogger(DeleteFileWorker.class);
	private Component c;
	private Exception error;
	private boolean userCancelled = false;


	public DeleteFileWorker(Component c) {
		this.c = c;
	}

	@Override
	protected Void doInBackground() throws Exception {
		ResourceBundle bundle = ResourceBundle.getBundle("localization.worker");

		firePropertyChange(WorkerDialog.EVENT_DIALOG_MESSAGE, "", bundle.getString("dfw.messageGetting"));
		List<String> list = new ArrayList<>();
		for (String dir : DataAccess.getDataDirectoryNames()) {
			try {
				Properties p = DataAccess.getMetadata(dir);
				String description = p.getProperty("description");
				list.add(dir + " - " + description);
			} catch (Exception e) {
				logger.warn("Error getting metadata for dir " + dir, e);
				list.add(dir + " - " + bundle.getString("dfw.noDescription"));
			}
		}
		Object selected = JOptionPane.showInputDialog(c, bundle.getString("dfw.joption.inputMessage"), bundle.getString("dfw.joption.inputTitle"),
				JOptionPane.QUESTION_MESSAGE, null, list.toArray(), null);

		if (selected == null) {
			userCancelled = true;
		} else {
			try {
				String name = (String) selected;
				name = name.substring(0, name.indexOf('-')).trim();
				firePropertyChange(WorkerDialog.EVENT_DIALOG_MESSAGE, "", bundle.getString("dfw.messageDeleting") + " " + name);
				DataAccess.deletePath(name);
			} catch (Exception e) {
				this.error = e;
			}
		}
		return null;
	}

	public Exception getError() {
		return this.error;
	}

	public boolean isUserCancelled() {
		return this.userCancelled;
	}
}
