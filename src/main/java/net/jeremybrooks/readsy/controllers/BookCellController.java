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

package net.jeremybrooks.readsy.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import net.jeremybrooks.readsy.BitHelper;
import net.jeremybrooks.readsy.BookUtils;
import net.jeremybrooks.readsy.model.Book;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

public class BookCellController {
    @FXML
    private ImageView coverImage;

    @FXML private Label title;
    @FXML private Label author;
    @FXML private Label status;

    private static final Logger logger = LogManager.getLogger();

    public void setBook(Book book) {
        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        if (BookUtils.isBookValid(book.getValidYear())) {
            BitHelper bitHelper = new BitHelper(book.getStatusFlags());
            status.setText("Unread: " + bitHelper.getUnreadItemCount(
                    LocalDate.parse(book.getReadingStartDate()), LocalDate.now()));
        } else {
            status.setText("Valid for the year " + book.getValidYear());
        }
        Image image = null;
        Path bookPath = Paths.get(book.getBookPath());
        Path imagePath = Paths.get(bookPath.getParent().toString(), "cover.png");
        if (Files.exists(imagePath)) {
            try (InputStream in = Files.newInputStream(imagePath)) {
                image = new Image(in);
            } catch (Exception e) {
                logger.error("Error loading image from {} for book {}", imagePath, book.getBookPath(), e);
            }
        } else {
            try {
                image = new Image("/images/emptycover.png");
            } catch (Exception e) {
                logger.error("Could not find image at /images/emptycover.png", e);
            }
        }
        coverImage.setImage(image);
    }
}
