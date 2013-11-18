/*
 * Copyright (c) 2013, Jeremy Brooks
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
package net.jeremybrooks.readsy.gui;

import javax.swing.filechooser.FileFilter;

/**
 * An implementation of FileFilter for use in JFileChoosers.
 * This implementation allows files that end with "*.xml".
 *
 * @author Jeremy Brooks
 */
public class SwingXMLFileFilter extends FileFilter {

	/**
	 * The extension to allow.
	 */
	private final String extension = ".xml";

	/**
	 * Creates a new instance of SwingXMLFileFilter
	 */
	public SwingXMLFileFilter() {
	}


	/**
	 * Implement the accept method.
	 *
	 * @param f the file to compare.
	 * @return
	 */
	public boolean accept(java.io.File f) {
		boolean ret = false;
		if (f != null) {
			if (f.isDirectory()) {
				ret = true;
			} else if (f.getName().toLowerCase().endsWith(this.extension)) {
				ret = true;
			}
		}

		return ret;
	}


	/**
	 * Get description of allowed files.
	 *
	 * @return description.
	 */
	public String getDescription() {
		return "readsy files (*.xml)";
	}
}
