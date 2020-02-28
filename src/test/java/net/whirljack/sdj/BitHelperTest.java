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

package net.whirljack.sdj;

import net.jeremybrooks.readsy.BitHelper;
import org.junit.Test;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * @author Jeremy Brooks
 */
public class BitHelperTest {
  private static final String testInit = "000102030405060708090a0b0c0d0e0f000102030405060708090a0b0c0d0e0f7772bc98aaff12450000dddbce65";

  private static final String testNothingRead = "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

  @Test
  public void test() throws Exception {
    SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
    Calendar cal = new GregorianCalendar();
    cal.set(Calendar.MONTH, 0);
    cal.set(Calendar.DAY_OF_MONTH, 1);
    cal.set(Calendar.YEAR, 2016);
    while (cal.get(Calendar.YEAR) == 2016) {
      cal.add(Calendar.DAY_OF_MONTH, 1);
    }

  }

  @Test
  public void testWhichByte() throws Exception {
    // expected is an array with 8 0's, 8 1's, etc.
    int[] expected = new int[368];
    int x = 0;
    for (int i = 0; i < expected.length; i += 8) {
      for (int j = 0; j < 8; j++) {
        expected[i + j] = x;
      }
      x++;
    }
    BitHelper bitHelper = new BitHelper(testInit);
    for (int i = 1; i < 367; i++) {
      assertEquals(expected[i - 1], bitHelper.whichByte(i));
    }
  }


  @Test
  public void testRandomBits() throws Exception {
    SecureRandom random = new SecureRandom();
    BitHelper bitHelper = new BitHelper(testNothingRead);
    boolean flag = true;
    Calendar cal = new GregorianCalendar();
    for (int i = 0; i < 2000; i++) {
      int dayOfYear = random.nextInt(365) + 1;
      cal.set(Calendar.DAY_OF_YEAR, dayOfYear);
      bitHelper.setRead(cal.getTime(), flag);
      assertEquals(flag, bitHelper.isRead(cal.getTime()));
      flag = !flag;
    }
  }

  @Test
  public void testStrings() throws Exception {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    String expectedJan1 = "01000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

    BitHelper bitHelper = new BitHelper(testNothingRead);
    bitHelper.setRead(sdf.parse("20130101"), true);
    assertEquals(expectedJan1, bitHelper.toString());

    bitHelper = new BitHelper(testInit);
    assertEquals(testInit, bitHelper.toString());
  }

  @Test
  public void testAllRead() throws Exception {
    String all = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
    BitHelper bitHelper = new BitHelper(all);
    Calendar cal = new GregorianCalendar();
    for (int i = 1; i < 366; i++) {
      cal.set(Calendar.DAY_OF_YEAR, i);
      assertTrue(bitHelper.isRead(cal.getTime()));
    }
  }

  @Test
  public void testNoneRead() throws Exception {
    String none = "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
    BitHelper bitHelper = new BitHelper(none);
    Calendar cal = new GregorianCalendar();
    for (int i = 1; i < 366; i++) {
      cal.set(Calendar.DAY_OF_YEAR, i);
      assertFalse(bitHelper.isRead(cal.getTime()));
    }
  }

  /*
   * Creates a read string for a known number of read days. Used to test other platform compatibility.
   */
  @Test
  public void createTestString() throws Exception {
    int[] days = {3, 9, 35, 76, 138, 166, 210, 248, 299, 309, 333, 365};
    String none = "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
    BitHelper bitHelper = new BitHelper(none);
    Calendar cal = new GregorianCalendar();
    for (int i : days) {
      cal.set(Calendar.DAY_OF_YEAR, i);
      bitHelper.setRead(cal.getTime(), true);
    }
  }

  @Test
  public void testGetUnreadItemCount() throws Exception {
    // this string has unread items at Nov 23, and Nov 25 to end of the year for year 2013
    String read = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffbf0000000000";
    BitHelper bitHelper = new BitHelper(read);
    Calendar stopDate = new GregorianCalendar();
    stopDate.set(Calendar.YEAR, 2013);
    stopDate.set(Calendar.MONTH, 10);
    stopDate.set(Calendar.DAY_OF_MONTH, 25);
    int count = bitHelper.getUnreadItemCount(stopDate.getTime(), "2013");
    assertEquals(2, count);
    count = bitHelper.getUnreadItemCount(stopDate.getTime(), "0");
    assertEquals(2, count);
  }

  @Test
  public void testGetUnreadItemCountInvalidYear() throws Exception {
    // this string has unread items at Nov 23, and Nov 25 to end of the year for year 2013
    String read = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffbf0000000000";
    BitHelper bitHelper = new BitHelper(read);
    Calendar stopDate = new GregorianCalendar();
    stopDate.set(Calendar.YEAR, 2012);
    stopDate.set(Calendar.MONTH, 10);
    stopDate.set(Calendar.DAY_OF_MONTH, 25);
    int count = bitHelper.getUnreadItemCount(stopDate.getTime(), "2013");
    assertEquals(0, count);
  }

  @Test
  public void getUnreadItemCountEndOfYear() throws Exception {
    // this bit pattern is all days read up to dec 22, so there should be 9 unread days
    BitHelper bitHelper = new BitHelper("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff0f00");

    // make a calendar for the last day of the year
    GregorianCalendar dec31 = new GregorianCalendar();
    dec31.set(GregorianCalendar.YEAR, 2017);
    int currentDayOfYear = dec31.get(Calendar.DAY_OF_YEAR);
    dec31.set(GregorianCalendar.MONTH, GregorianCalendar.DECEMBER);
    dec31.set(GregorianCalendar.DAY_OF_MONTH, 31);
    assertEquals(9, bitHelper.getUnreadItemCount(dec31.getTime(), "2017"));
  }
}
