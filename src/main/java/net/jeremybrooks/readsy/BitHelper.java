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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Jeremy Brooks
 */
public class BitHelper {

	private byte[] bytes;
	private GregorianCalendar calendar;

	/**
	 * Create an instance of BitHelper with enough space to store a years worth of flags.
	 *
	 */
	public BitHelper() {
		this("00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
	}


	/**
	 * Create a new instance of BitHelper with the specified hex string.
	 *
	 * @param hexBytes the bytes that will be managed by this instance of BitHelper.
	 */
	public BitHelper(String hexBytes) {
		int len = hexBytes.length();
		bytes = new byte[len/2];
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
		int mask = (int) Math.pow(2, (dayOfYear - 1) % 8);			// adjust to zero-based index
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
			int result = ((int)this.bytes[whichByte(dayOfYear)]) ^ (int) Math.pow(2, (dayOfYear - 1) % 8);
			this.bytes[whichByte(dayOfYear)] = (byte)result;
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
