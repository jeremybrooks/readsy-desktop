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

import java.time.format.DateTimeFormatter;

public class Formatters {
    public static final DateTimeFormatter mmddFormatter = DateTimeFormatter.ofPattern("MM-dd");
    public static final DateTimeFormatter shortISOFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter longMonthAndDayFormatter = DateTimeFormatter.ofPattern("MMMM d");
    public static final DateTimeFormatter longMonthAndDayAndYearFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
    public static final DateTimeFormatter fullDateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
}
