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
package net.jeremybrooks.readsy;


import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;


/**
 * Some handy static to manipulate files and streams.
 *
 * @author Jeremy Brooks
 */
public class FileUtil {

	private static Logger logger = Logger.getLogger(FileUtil.class);
	private static SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
	private static SimpleDateFormat MMdd = new SimpleDateFormat("MMdd");

	/**
	 * Private default constructor.
	 * All methods in this class should be public static.
	 */
	private FileUtil() {
	}


	/**
	 * Close an InputStream.
	 * <p/>
	 * <p>Errors will be logged.</p>
	 *
	 * @param in the InputStream instance to close.
	 */
	public static void close(InputStream in) {
		if (in != null) {
			try {
				in.close();
			} catch (Exception e) {
				logger.warn("ERROR CLOSING INPUT STREAM.", e);
			}
		}
	}


	/**
	 * Close an OutputStream.
	 * <p/>
	 * <p>Errors will be logged.</p>
	 *
	 * @param out the OutputStream to close.
	 */
	public static void close(OutputStream out) {
		if (out != null) {
			try {
				out.close();
			} catch (Exception e) {
				logger.warn("ERROR CLOSING OUTPUT STREAM.", e);
			}
		}
	}


	/**
	 * Close a Writer.
	 * <p/>
	 * <p>Errors will be logged.</p>
	 *
	 * @param w the writer to close.
	 */
	public static void close(Writer w) {
		if (w != null) {
			try {
				w.close();
			} catch (Exception e) {
				logger.warn("ERROR CLOSING WRITER.", e);
			}
		}
	}

	public static void flush(Writer w) {
		if (w != null) {
			try {
				w.flush();
			} catch (Exception e) {
				logger.warn("ERROR FLUSHING WRITER.", e);
			}
		}
	}


	/**
	 * Close a Reader.
	 * <p/>
	 * <p>Errors will be logged.</p>
	 *
	 * @param r the reader to close.
	 */
	public static void close(Reader r) {
		if (r != null) {
			try {
				r.close();
			} catch (Exception e) {
				logger.warn("ERROR CLOSING WRITER.", e);
			}
		}
	}


	/**
	 * Copy a text file.
	 * <p/>
	 * Encoding is UTF-8.
	 *
	 * @param source the source file.
	 * @param dest   the destination file.
	 * @throws IOException if any errors occur during the copy.
	 */
	public static void copy(File source, File dest) throws IOException {
		BufferedReader in = null;
		BufferedWriter out = null;
		String line;

		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(source), "UTF-8"));
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dest), "UTF-8"));

			while ((line = in.readLine()) != null) {
				out.write(line);
				out.write('\n');
			}

			out.flush();
		} finally {
			FileUtil.close(in);
			FileUtil.close(out);
		}
	}
}
