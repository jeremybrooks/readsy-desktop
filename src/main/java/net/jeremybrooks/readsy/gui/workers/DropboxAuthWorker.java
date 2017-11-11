/*
 * readsy - read something new every day <http://jeremybrooks.net/readsy>
 *
 * Copyright (c) 2013-2017  Jeremy Brooks
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
import net.jeremybrooks.readsy.PropertyManager;
import net.jeremybrooks.readsy.dropbox.DropboxHelper;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import java.awt.Component;
import java.awt.Desktop;
import java.net.URI;
import java.util.ResourceBundle;

/**
 * @author Jeremy Brooks
 */
public class DropboxAuthWorker extends SwingWorker<Void, Void> {

	private Component c;
	private Exception e;


	public void setComponent(Component c) {
		this.c = c;
	}

	@Override
	protected Void doInBackground() throws Exception {
		ResourceBundle bundle = ResourceBundle.getBundle("localization.worker");
		try {
			String authUrl = DropboxHelper.startWebAuth();
			Desktop.getDesktop().browse(new URI(authUrl));

			String authorizationCode = JOptionPane.showInputDialog(c,
					bundle.getString("daw.joption.inputMessage"),
					bundle.getString("daw.joption.inputTitle"),
					JOptionPane.QUESTION_MESSAGE);
			if (authorizationCode == null) {
				throw new Exception("Authorization cancelled.");
			} else {
				firePropertyChange(WorkerDialog.EVENT_DIALOG_MESSAGE, "", bundle.getString("daw.messageGettingToken"));
				String accessToken = DropboxHelper.finishWebAuth(authorizationCode);
				if (accessToken == null) {
					throw new Exception("Dropbox authorization failed: access token was null.");
				}
				PropertyManager.getInstance().setProperty(PropertyManager.DROPBOX_ACCESS_TOKEN, accessToken);
			}
		} catch (Exception e) {
			this.e = e;
		}
		return null;
	}

	public Exception getException() {
		return this.e;
	}
}
