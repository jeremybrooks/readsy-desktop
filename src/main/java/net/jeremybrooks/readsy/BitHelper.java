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

import java.time.LocalDate;

/**
 * @author Jeremy Brooks
 */
public class BitHelper {

  private final byte[] bytes;
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
  }

  public boolean isRead(int dayOfReadingYear) {
    int mask = (int) Math.pow(2, (dayOfReadingYear - 1) % 8);            // adjust to zero-based index
    return ((this.bytes[whichByte(dayOfReadingYear)]) & mask) == mask;
  }


  public void setRead(int dayOfReadingYear, boolean read) {
    if (this.isRead(dayOfReadingYear) != read) {
      int result = ((int) this.bytes[whichByte(dayOfReadingYear)]) ^ (int) Math.pow(2, (dayOfReadingYear - 1) % 8);
      this.bytes[whichByte(dayOfReadingYear)] = (byte) result;
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
   * Count the number of unread items from a beginning date to the current date.
   *
   * @param readingStartDate the date reading started.
   * @param currentDate the current date.
   * @return number of unread items in the range.
   */
  public int getUnreadItemCount(LocalDate readingStartDate, LocalDate currentDate) {
    int count = 0;
    do {
      int dayOfReadingYear = BookUtils.getDayOfReadingYear(readingStartDate, currentDate);
      if (!isRead(dayOfReadingYear)) {
        count++;
      }
      readingStartDate = readingStartDate.plusDays(1);
    } while (! readingStartDate.isAfter(currentDate));
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
