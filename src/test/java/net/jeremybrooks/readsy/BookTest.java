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

import com.fasterxml.jackson.databind.ObjectMapper;
import net.jeremybrooks.readsy.model.Book;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BookTest {
    @Test
    public void test() throws Exception {
        int year = 2024;
        String description = "War and Peace, Super Boring Edition";
        String author = "Leo Tolstoy";
        String shortDescription = "WaP";
        String read = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff1f000000";
        String readingStartDate = "2025-01-01";
        String readingEndDate = "2025-12-31";

        Book book = new Book();
        book.setValidYear(year);
        book.setShortTitle(shortDescription);
        book.setStatusFlags(read);
        book.setTitle(description);
        book.setAuthor(author);
        book.setReadingStartDate(readingStartDate);
        book.setReadingEndDate(readingEndDate);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(book);
        Book bookFromJson = mapper.readValue(json, Book.class);

        assertEquals(year, bookFromJson.getValidYear());
        assertEquals(read, bookFromJson.getStatusFlags());
        assertEquals(description, bookFromJson.getTitle());
        assertEquals(shortDescription, bookFromJson.getShortTitle());
        assertEquals(author, bookFromJson.getAuthor());
        assertEquals(readingStartDate, book.getReadingStartDate());
        assertEquals(readingEndDate, book.getReadingEndDate());
    }
}
