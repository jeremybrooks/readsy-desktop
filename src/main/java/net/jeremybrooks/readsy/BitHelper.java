/*
 * readsy - read something new every day <http://jeremybrooks.net/readsy>
 *
 * Copyright (c) 2013-2019  Jeremy Brooks
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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Jeremy Brooks
 */
public class BitHelper {

  private byte[] bytes;
  private GregorianCalendar calendar;
  private static final String EMPTY_BITSET = "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

  /**
   * Create an instance of BitHelper with enough space to store a years worth of flags.
   */
  public BitHelper() {
    this(EMPTY_BITSET);
  }


  /**
   * Create a new instance of BitHelper with the specified hex string.
   *
   * @param hexBytes the bytes that will be managed by this instance of BitHelper.
   */
  public BitHelper(String hexBytes) {
    int len = hexBytes.length();
    bytes = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      bytes[i / 2] = (byte) ((Character.digit(hexBytes.charAt(i), 16) << 4)
          + Character.digit(hexBytes.charAt(i + 1), 16));
    }
    this.calendar = new GregorianCalendar();
  }

  /**
   * Determine if the flag is true or false for the specified date.
   *
   * @param date date to look up.
   * @return true if the flag is 1, false otherwise.
   */
  public boolean isRead(Date date) {
    int dayOfYear = this.getDayOfYear(date);
    int mask = (int) Math.pow(2, (dayOfYear - 1) % 8);            // adjust to zero-based index
    return ((this.bytes[whichByte(dayOfYear)]) & mask) == mask;
  }


  /**
   * Set the value of the flag for the specified date.
   * <p>If the flag is already set to the specified value, nothing happens.</p>
   *
   * @param date the date to set the flag for.
   * @param read what the flag should be set to.
   */
  public void setRead(Date date, boolean read) {
    if (this.isRead(date) != read) {
      int dayOfYear = this.getDayOfYear(date);
      int result = ((int) this.bytes[whichByte(dayOfYear)]) ^ (int) Math.pow(2, (dayOfYear - 1) % 8);
      this.bytes[whichByte(dayOfYear)] = (byte) result;
    }
  }

  /**
   * Get the byte position that contains this day of the year.
   * Years start at 1, but the array starts at zero. This is taken into account.
   *
   * @param dayOfYear which day of the year. Years start at 1.
   * @return which byte position contains the day.
   */
  public int whichByte(int dayOfYear) {
    return this.bytes.length - (((this.bytes.length * 8) - dayOfYear) / 8) - 1;
  }


  /**
   * Get the ordinal day of the year for the date.
   *
   * @param date the date.
   * @return ordinal day of the year for the date.
   */
  public int getDayOfYear(Date date) {
    this.calendar.setTime(date);
    return calendar.get(Calendar.DAY_OF_YEAR);
  }


  /**
   * Count how many unread items there are from the beginning of the year until
   * stopDate, inclusive.
   *
   * @param stopDate the date to stop looking for unread items, inclusive.
   * @param year     the year that the data file is good for, "0" for all years.
   * @return number of unread items from Jan 1 to stopDate.
   */
  public int getUnreadItemCount(Date stopDate, String year) {
    int count = 0;

    Calendar cal = new GregorianCalendar();
    cal.setTime(stopDate);
    int stopDay = cal.get(Calendar.DAY_OF_YEAR);
    if (year.equals("0") || year.equals(Integer.toString(cal.get(Calendar.YEAR)))) {
      Calendar theDay = new GregorianCalendar();
      theDay.set(Calendar.DAY_OF_YEAR, 1);
      do {
        if (!isRead(theDay.getTime())) {
          count++;
        }
        theDay.add(Calendar.DAY_OF_YEAR, 1);
      }
      while (theDay.get(Calendar.DAY_OF_YEAR) <= stopDay && theDay.get(Calendar.DAY_OF_YEAR) != 1);
      // note: the != 1 ensures that, if stop date is 12/31, we stop after one time through the year
    }
    return count;
  }

  /**
   * Return the byte values as a hex string.
   *
   * @return current byte values as a hex string.
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (byte aByte : this.bytes) {
      sb.append(String.format("%02x", aByte));
    }
    return sb.toString();
  }
}
