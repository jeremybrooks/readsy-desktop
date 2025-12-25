/*
 * readsy - read something new every day <http://jeremybrooks.net/readsy>
 *
 * Copyright (c) 2013-2021  Jeremy Brooks
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

package net.jeremybrooks.readsy.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.BufferedReader;
import java.io.StringReader;

/**
 * Encapsulates the heading and text for an entry.
 *
 * @author Jeremy Brooks
 */
@JsonPropertyOrder({"heading", "text"})
public class Entry {
  private String heading;
  private String text;

  public Entry() {}

  /**
   * Create an entry from data read from file.
   *
   * @param content the content read from file.
   * @throws Exception if the data can't be converted into an Entry.
   */
  public Entry(String content) throws Exception {
    try (BufferedReader in = new BufferedReader(new StringReader(content))) {
      setHeading(in.readLine());
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = in.readLine()) != null) {
        sb.append(line).append('\n');
      }
      setText(sb.toString());
    }
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getHeading() {
    return heading;
  }

  public void setHeading(String heading) {
    this.heading = heading;
  }
}
