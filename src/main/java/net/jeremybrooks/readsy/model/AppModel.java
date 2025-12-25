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

package net.jeremybrooks.readsy.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.Stage;
import net.jeremybrooks.readsy.ActiveState;

import java.nio.file.Path;

public class AppModel {
    private final ObjectProperty<ActiveState> activeState = new SimpleObjectProperty<>(ActiveState.BOOKS);
    private String version;
    private Configuration configuration;
    private Stage stage;
    private Path editorBookDirectory;
    private Book book;

    public String getVersion() {
        return version == null ? "unknown" : version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public ActiveState getActiveState() {
        return activeState.get();
    }
    public ObjectProperty<ActiveState> activeStateProperty() {
        return activeState;
    }
    public void setActiveState(ActiveState activeState) {
        this.activeState.set(activeState);
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Path getEditorBookDirectory() {
        return editorBookDirectory;
    }

    public void setEditorBookDirectory(Path editorBookDirectory) {
        this.editorBookDirectory = editorBookDirectory;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
