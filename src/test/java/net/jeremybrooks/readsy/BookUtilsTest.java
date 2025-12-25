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

import java.time.LocalDate;

import static org.junit.Assert.*;

public class BookUtilsTest {

    @Test
    public void testGetDayOfReadingYear() {
        assertEquals(3, BookUtils.getDayOfReadingYear(
                LocalDate.of(2024, 2, 1),
        LocalDate.of(2024, 2, 3)
                ));
        // leap year
        assertEquals(60,
                BookUtils.getDayOfReadingYear(
                        LocalDate.of(2020, 1, 1),
                        LocalDate.of(2020, 2,29)
                ));
        // not leap year
        assertEquals(60,
                BookUtils.getDayOfReadingYear(
                        LocalDate.of(2021, 1, 1),
                        LocalDate.of(2021, 3,1)
                ));

        // start december 1, roll over to new year
        assertEquals(91,
                BookUtils.getDayOfReadingYear(
                        LocalDate.of(2020, 12, 1),
                        LocalDate.of(2021, 3, 1)
                ));

        // start 2025-01-05, end 2026-01-04, should be day 365
        assertEquals(365,
                BookUtils.getDayOfReadingYear(
                        LocalDate.of(2025, 1, 5),
                        LocalDate.of(2026, 1, 4)));
    }

    @Test
    public void testIsBookValid() {
        int currentYear = LocalDate.now().getYear();
        assertTrue(BookUtils.isBookValid(0));
        assertTrue(BookUtils.isBookValid(currentYear));
    }

    @Test
    public void testIsPageDateInReadingRange() {
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2020, 12, 31);
        assertTrue(BookUtils.isPageDateInReadingRange(LocalDate.of(2020, 1, 1), startDate, endDate));
        assertTrue(BookUtils.isPageDateInReadingRange(LocalDate.of(2020, 12, 31), startDate, endDate));
        assertTrue(BookUtils.isPageDateInReadingRange(LocalDate.of(2020, 3, 31), startDate, endDate));

        assertFalse(BookUtils.isPageDateInReadingRange(LocalDate.of(2019, 3, 31), startDate, endDate));

        startDate = LocalDate.of(2019, 6, 1);
        endDate = LocalDate.of(2020, 5, 31);
        assertTrue(BookUtils.isPageDateInReadingRange(LocalDate.of(2019, 8, 4), startDate, endDate));
        assertTrue(BookUtils.isPageDateInReadingRange(LocalDate.of(2020, 2, 29), startDate, endDate));
        assertFalse(BookUtils.isPageDateInReadingRange(LocalDate.of(2019, 5, 4), startDate, endDate));
    }
}