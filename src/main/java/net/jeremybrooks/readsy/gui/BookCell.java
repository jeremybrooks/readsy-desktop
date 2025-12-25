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

package net.jeremybrooks.readsy.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import net.jeremybrooks.readsy.controllers.BookCellController;
import net.jeremybrooks.readsy.model.Book;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class BookCell extends ListCell<Book> {
    private final Parent root;
    private final BookCellController controller;

    private static final Logger logger = LogManager.getLogger();

    public BookCell() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BookCell.fxml"));
            root = loader.load();
            controller = loader.getController() ;
        } catch (IOException e) {
            logger.error("Error loading BookCell.fxml", e);
            throw new RuntimeException(e);
        }
    }
    @Override
    protected void updateItem(Book item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            controller.setBook(item);
            setGraphic(root);
        }
    }
}
