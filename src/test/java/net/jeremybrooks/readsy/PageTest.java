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
import net.jeremybrooks.readsy.model.Page;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PageTest {
    @Test
    public void test() throws Exception {
        Page page = new Page();
        page.setHeading("This is the heading");
        page.setText("""
        This is the text of the page.
        
        It can have newlines
        and "quotes" and 'singlequotes'""");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(page);
        System.out.println(json);

        Page pageFromJson = mapper.readValue(json, Page.class);

        assertEquals(page.getHeading(), pageFromJson.getHeading());
        assertEquals(page.getText(), pageFromJson.getText());
    }
}
