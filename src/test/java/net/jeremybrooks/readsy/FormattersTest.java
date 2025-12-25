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

import static org.junit.Assert.assertEquals;

public class FormattersTest {

    @Test
    public void testMmddFormatter() {
        LocalDate jan01 = LocalDate.of(2024, 1, 1);
        assertEquals("01-01", Formatters.mmddFormatter.format(jan01));
    }

    @Test
    public void testISOFormatter() {
        LocalDate jan01 = LocalDate.of(2024, 1, 1);
        assertEquals("2024-01-01", Formatters.shortISOFormatter.format(jan01));
    }

    @Test
    public void testLongMonthAndDayFormatter() {
        LocalDate jan01 = LocalDate.of(2024, 1, 1);
        assertEquals("January 1", Formatters.longMonthAndDayFormatter.format(jan01));
    }

}