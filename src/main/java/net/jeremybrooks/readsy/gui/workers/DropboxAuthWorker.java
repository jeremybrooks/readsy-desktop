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

import net.jeremybrooks.readsy.PropertyManager;
import net.jeremybrooks.readsy.dropbox.DropboxHelper;
import net.jeremybrooks.readsy.gui.WorkerDialog;

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
				PropertyManager.getInstance().setProperty(PropertyManager.DROPBOX_ENABLED, "true");
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
