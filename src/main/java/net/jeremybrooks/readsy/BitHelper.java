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

	public int getUnreadItemCount(Date stopDate, String year) {
		int count = 0;

		Calendar stopHere = new GregorianCalendar();
		stopHere.setTime(stopDate);
		stopHere.set(Calendar.HOUR_OF_DAY, 0);
		stopHere.set(Calendar.MINUTE, 0);
		stopHere.set(Calendar.SECOND, 0);

		Calendar calendar = new GregorianCalendar();
		if (!year.equals("0")) {
			calendar.set(Calendar.YEAR, Integer.parseInt(year));
		}
		calendar.set(Calendar.DAY_OF_YEAR, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		do {
			if (!calendar.after(stopHere)) {    // count up to and including today
				if (!this.isRead(calendar.getTime())) {
					count++;
				}
			}
			calendar.add(Calendar.DAY_OF_YEAR, 1);
		} while (calendar.get(Calendar.DAY_OF_YEAR) != 1);
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
		for (int i = 0; i < this.bytes.length; i++) {
			sb.append(String.format("%02x", this.bytes[i]));
		}
		return sb.toString();
	}
}
