/*
 * readsy - read something new every day <http://jeremybrooks.net/readsy>
 *
 * Copyright (c) 2013-2021  Jeremy Brooks
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

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * @author Jeremy Brooks
 */
public class VersionCheckerTest {

	@Test
	public void testUpdated() throws Exception {
		VersionChecker vc = new VersionChecker();

		assertFalse(vc.updated(null, null));
		assertFalse(vc.updated(null, ""));
		assertFalse(vc.updated(null, "1.0"));
		assertFalse(vc.updated("", null));
		assertFalse(vc.updated("1.0", null));
		assertFalse(vc.updated("1.0.0", "1.0.0"));
		assertFalse(vc.updated("1", "1"));
		assertFalse(vc.updated("not.a.number", "1.0.0"));
		assertFalse(vc.updated("oops", "slsllsls"));
		assertFalse(vc.updated("1.0.0.3.4.33.3", "1.0"));
		assertFalse(vc.updated("1.5.0", "1.4.0"));

		assertTrue(vc.updated("1.0", "1.1"));
		assertTrue(vc.updated("1.0.8", "1.0.10"));
		assertTrue(vc.updated("1.0.8", "2.0"));

	}
}
