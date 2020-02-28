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

package net.jeremybrooks.readsy;

import net.jeremybrooks.readsy.gui.AboutDialog;
import net.jeremybrooks.readsy.gui.MainWindow;
import net.jeremybrooks.readsy.gui.PreferencesDialog;

import java.awt.Desktop;

public class MacOSSetup {

  public MacOSSetup() {

    Desktop.getDesktop().setAboutHandler(ae -> new AboutDialog(null, true).setVisible(true));

    Desktop.getDesktop().setQuitHandler((qe, qr) -> qr.performQuit());

    Desktop.getDesktop().setPreferencesHandler(pe ->
        new PreferencesDialog(MainWindow.instance, true).setVisible(true));
  }
}
