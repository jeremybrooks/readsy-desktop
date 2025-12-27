/*
 * readsy - read something new every day <http://jeremybrooks.net/readsy>
 *
 * Copyright (c) 2013-2025  Jeremy Brooks
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

import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.File;
import java.util.Objects;

public class Constants {
  private Constants() {}

  /** Property that stores the location of books. */
  public static final String READSY_CONFIG_DIR = System.getProperty("user.home") + File.separator + ".readsy";
  public static final String READSY_CONFIG_FILE = READSY_CONFIG_DIR + File.separator + "ReadsyConfig.json";

  public static final String HOME_PAGE = "https://jeremybrooks.net/readsy";
  public static final String VERSION_URL = HOME_PAGE + "/VERSION";
  public static final String READSY_DOWNLOAD_PAGE = HOME_PAGE + "/download.html";

    /* OLD */
  public static final Image WINDOW_IMAGE = (new ImageIcon(Objects.requireNonNull(Readsy.class.getResource("/images/icon-light-16.png"))).getImage());
  public static final String NOTHING_READ = "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
}
