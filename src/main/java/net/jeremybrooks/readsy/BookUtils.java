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

import net.jeremybrooks.readsy.model.Book;

import java.time.LocalDate;

public class BookUtils {
    public static int getDayOfReadingYear(Book book) {
        return getDayOfReadingYear(LocalDate.parse(book.getReadingStartDate(), Formatters.shortISOFormatter),
                book.getPageDate());
    }

    /**
     * Get the day of the reading year for the given startDate.
     * <p>
     * This will calculate the day of the reading year, that is, the year beginning on the
     * date the book reading started. If a book was started on February 1st, and the
     * current date is February 3rd, this will return 3.
     *
     * @param currentDate the current date to use.
     * @param startDate the date reading started.
     * @return day of the reading year relative to the current date.
     */
    public static int getDayOfReadingYear(LocalDate startDate, LocalDate currentDate) {
        int readingStartDay = startDate.getDayOfYear();
        int dayOfCalendarYear = currentDate.getDayOfYear();
        int dayOfReadingYear;
        if (currentDate.getYear() == startDate.getYear()) {
            dayOfReadingYear = dayOfCalendarYear - readingStartDay;
        } else {
            // year has wrapped around,
            // so calculate days from reading start date to end of that year
            LocalDate endOfYear = LocalDate.of(startDate.getYear(), 12, 31);
            dayOfReadingYear = endOfYear.getDayOfYear() - readingStartDay;

            // now add days of the next year
            dayOfReadingYear += dayOfCalendarYear;
        }
        return dayOfReadingYear + 1;
    }

    /**
     * Determine if the book is valid for the current year.
     * <p>
     * A book is valid if the validYear parameter is 0 (meaning any year),
     * or if the validYear matches the current year.
     *
     * @param validYear the validYear data for the book.
     * @return true if the book is valid, false otherwise.
     */
    public static boolean isBookValid(int validYear) {
        return validYear == 0 ||
                validYear == LocalDate.now().getYear();
    }


    /**
     * Determine if the given page date is within the reading dates.
     *
     * @param pageDate the current page date.
     * @param startDate the reading start date.
     * @param endDate the reading end date.
     * @return true if the page date is equal to startDate or endDate, or if
     * the page date is after startDate and before endDate.
     */
    public static boolean isPageDateInReadingRange(LocalDate pageDate, LocalDate startDate, LocalDate endDate) {
        return pageDate.isEqual(startDate) ||
                pageDate.isEqual(endDate) ||
                (pageDate.isAfter(startDate) && pageDate.isBefore(endDate));
    }

    public static boolean isPageDateInReadingRange(Book book) {
        return isPageDateInReadingRange(book.getPageDate(),
                LocalDate.parse(book.getReadingStartDate(), Formatters.shortISOFormatter),
                LocalDate.parse(book.getReadingEndDate(), Formatters.shortISOFormatter));
    }
}
