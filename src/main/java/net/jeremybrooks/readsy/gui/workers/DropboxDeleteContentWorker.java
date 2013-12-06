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

import net.jeremybrooks.common.gui.WorkerDialog;
import net.jeremybrooks.readsy.PropertyManager;
import net.jeremybrooks.readsy.dropbox.DropboxHelper;

import javax.swing.SwingWorker;
import java.util.ResourceBundle;

/**
 * @author Jeremy Brooks
 */
public class DropboxDeleteContentWorker extends SwingWorker<Void, Void> {
	private Exception exception;

	@Override
	protected Void doInBackground() throws Exception {
		ResourceBundle bundle = ResourceBundle.getBundle("localization.worker");

		try {
			firePropertyChange(WorkerDialog.EVENT_DIALOG_MESSAGE, "", bundle.getString("ddcw.messageGettingList"));
			for (String path : DropboxHelper.getInstance().getFoldersAtPath("/")) {
				firePropertyChange(WorkerDialog.EVENT_DIALOG_MESSAGE, "", bundle.getString("ddcw.messageDeleting") + " " + path);
				DropboxHelper.getInstance().delete(path);
			}
		} catch (Exception e) {
			this.exception = e;
		} finally {
			PropertyManager.getInstance().deleteProperty(PropertyManager.DROPBOX_ACCESS_TOKEN);
			PropertyManager.getInstance().setProperty(PropertyManager.DROPBOX_ENABLED, "false");
		}
		return null;
	}

	public Exception getException() {
		return this.exception;
	}
}
