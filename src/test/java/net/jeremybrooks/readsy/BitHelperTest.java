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

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;


/**
 * @author Jeremy Brooks
 */
public class BitHelperTest {
  private static final String testInit = "000102030405060708090a0b0c0d0e0f000102030405060708090a0b0c0d0e0f7772bc98aaff12450000dddbce65";

  private static final String testNothingRead = "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

  @Test
  public void testNew() {
    BitHelper bitHelper = new BitHelper(testNothingRead);
    assertEquals(1, bitHelper.getUnreadItemCount(
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 1, 1)));
    assertEquals(31, bitHelper.getUnreadItemCount(
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 1, 31)));
  }

  @Test
          public void testnew1() {
    // this bit pattern is all days read up to dec 22 for a non-leap year.
    // so from jan 1 to dec 31 on a non-leap year there should be 9 unread days.
    BitHelper bitHelper = new BitHelper("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff0f00");
    assertEquals(9,
            bitHelper.getUnreadItemCount(
                    LocalDate.of(2021, 1, 1),
                    LocalDate.of(2021, 12, 31)
            ));
    // for a leap year, there should be 10 unread days
    assertEquals(10,
            bitHelper.getUnreadItemCount(
                    LocalDate.of(2020, 1, 1),
                    LocalDate.of(2020, 12, 31)
            ));
  }

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
  public void testStrings() throws Exception {
    String expectedJan1 = "01000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

    BitHelper bitHelper = new BitHelper(testNothingRead);
    bitHelper.setRead(1, true);
    assertEquals(expectedJan1, bitHelper.toString());

    bitHelper = new BitHelper(testInit);
    assertEquals(testInit, bitHelper.toString());
  }

  @Test
  public void testAllRead() throws Exception {
    String all = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
    BitHelper bitHelper = new BitHelper(all);
    for (int i = 1; i < 366; i++) {
      assertTrue(bitHelper.isRead(i));
    }
  }

  @Test
  public void testNoneRead() throws Exception {
    String none = "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
    BitHelper bitHelper = new BitHelper(none);
    for (int i = 1; i < 366; i++) {
      assertFalse(bitHelper.isRead(i));
    }
  }

  @Test
  public void testGetUnreadItemCount() throws Exception {
    // this string has unread items at Nov 23, and Nov 25 to end of the year for year 2013
    String read = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffbf0000000000";
    BitHelper bitHelper = new BitHelper(read);
    LocalDate readingStartDate = LocalDate.of(2013, Month.JANUARY, 1);
    LocalDate currentDate = LocalDate.of(2013, Month.NOVEMBER, 25);
    int count = bitHelper.getUnreadItemCount(readingStartDate, currentDate);
    assertEquals(2, count);
  }

  @Test
  public void getUnreadItemCountEndOfYear() throws Exception {
    // this bit pattern is all days read up to dec 22, so there should be 9 unread days
    BitHelper bitHelper = new BitHelper("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff0f00");

    LocalDate readingStartDate = LocalDate.of(2017, Month.JANUARY, 1);
    LocalDate currentDate = LocalDate.of(2017, Month.DECEMBER, 31);
    assertEquals(9, bitHelper.getUnreadItemCount(readingStartDate, currentDate));
  }
}
