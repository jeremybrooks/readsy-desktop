/*
 * readsy - read something new every day <http://jeremybrooks.net/readsy>
 *
 * Copyright (c) 2013-2020  Jeremy Brooks
 *
 * This file is part of readsy.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.jeremybrooks.readsy.gui.workers;

import net.jeremybrooks.common.gui.WorkerDialog;
import net.jeremybrooks.readsy.DataAccess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	private Logger logger = LogManager.getLogger(DeleteFileWorker.class);
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
