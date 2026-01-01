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

package net.jeremybrooks.readsy.workers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.ObservableList;
import net.jeremybrooks.readsy.MapperFactory;
import net.jeremybrooks.readsy.model.AppModel;
import net.jeremybrooks.readsy.model.Book;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class RefreshBooksWorker implements Runnable {

    private final static Logger logger = LogManager.getLogger();
    private final ObservableList<Book> list;
    private final AppModel appModel;

    public RefreshBooksWorker(ObservableList<Book> list, AppModel appModel) {
        this.list = list;
        this.appModel = appModel;
    }

    @Override
    public void run() {
        Map<String, Book> existingBookMap = new HashMap<>();
        list.forEach(book -> existingBookMap.put(book.getShortTitle(), book));

        ObjectMapper mapper = MapperFactory.getObjectMapper();
        try {
            try (Stream<Path> pathStream =
                         Files.find(Paths.get(appModel.getConfiguration().getBookDirectory()),
                                 Integer.MAX_VALUE,
                                 (filePath, fileAttr) -> fileAttr.isRegularFile())) {
                pathStream.sorted()
                        .filter(path -> path.endsWith("book.json"))
                        .forEach(path -> {
                            logger.info("Found book at {}", path);
                            try (InputStream in = Files.newInputStream(path)) {
                                Book b = mapper.readValue(in, Book.class);
                                b.setBookPath(path.toString());
                                // if the newly loaded book was already in the book list,
                                // preserve the page date and remove the old one from the list
                                if (existingBookMap.containsKey(b.getShortTitle())) {
                                    b.setPageDate(existingBookMap.get(b.getShortTitle()).getPageDate());
                                }
                                list.add(b);
                            } catch (Exception e) {
                                logger.error("Error reading book from {}", path, e);
                            }
                        });
            }
        } catch (Exception e) {
            logger.error("Error while searching for books.", e);
        }
        list.removeAll(existingBookMap.values());
    }
}
