/*
 * readsy - read something new every day <http://jeremybrooks.net/readsy>
 *
 * Copyright (c) 2013-2024  Jeremy Brooks
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

package net.jeremybrooks.readsy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;

@JsonPropertyOrder({"shortTitle", "title", "author", "version", "validYear", "statusFlags", "readingStartDate", "readingEndDate"})
public class Book {


    private String title;
    private String shortTitle;
    private String author;
    private String version;
    private int validYear;
    private String statusFlags;
    private String readingStartDate;
    private String readingEndDate;

    @JsonIgnore
    private String bookPath;

    @JsonIgnore
    private LocalDate pageDate = LocalDate.now();

    public LocalDate getPageDate() {
        return pageDate;
    }

    public void setPageDate(LocalDate pageDate) {
        this.pageDate = pageDate;
    }


    public String getBookPath() {
        return bookPath;
    }

    public void setBookPath(String bookPath) {
        this.bookPath = bookPath;
    }

    public String getReadingStartDate() {
        return readingStartDate;
    }

    public void setReadingStartDate(String readingStartDate) {
        this.readingStartDate = readingStartDate;
    }

    public String getReadingEndDate() {
        return readingEndDate;
    }

    public void setReadingEndDate(String readingEndDate) {
        this.readingEndDate = readingEndDate;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


    public int getValidYear() {
        return validYear;
    }

    public void setValidYear(int validYear) {
        this.validYear = validYear;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatusFlags() {
        return statusFlags;
    }

    public void setStatusFlags(String statusFlags) {
        this.statusFlags = statusFlags;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }



}
