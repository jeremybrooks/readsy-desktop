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

package net.jeremybrooks.readsy;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent;
import com.apple.eawt.Application;
import com.apple.eawt.PreferencesHandler;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;
import net.jeremybrooks.readsy.gui.AboutDialog;
import net.jeremybrooks.readsy.gui.MainWindow;
import net.jeremybrooks.readsy.gui.PreferencesDialog;

import javax.swing.JFrame;

/**
 * @author Jeremy Brooks
 */
public class OSXSetup {

	public OSXSetup() {
		Application app = Application.getApplication();

		app.setAboutHandler(new AboutHandler() {

			@Override
			public void handleAbout(AppEvent.AboutEvent ae) {
				new AboutDialog(new JFrame(), true).setVisible(true);
			}

		});

		app.setQuitHandler(new QuitHandler() {

			@Override
			public void handleQuitRequestWith(AppEvent.QuitEvent qe, QuitResponse qr) {
				qr.performQuit();
			}

		});

		app.setPreferencesHandler(new PreferencesHandler() {
			@Override
			public void handlePreferences(AppEvent.PreferencesEvent pe) {
				new PreferencesDialog(MainWindow.instance, true).setVisible(true);
			}
		});
	}
}
