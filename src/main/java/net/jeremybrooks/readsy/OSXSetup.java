/*
 * readsy - read something new every day
 *
 *     Copyright (C) 2103  Jeremy Brooks
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
 *     You may contact the program's author at jeremyb@whirljack.net
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
