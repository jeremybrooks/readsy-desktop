/*
 * readsy - read something new every day
 *
 *     Copyright (C) 2013  Jeremy Brooks
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     You may contact the programs author at jeremyb@whirljack.net
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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;


/**
 * Some handy static to manipulate files and streams.
 *
 * @author Jeremy Brooks
 */
public class FileUtil {

	private static Logger logger = Logger.getLogger(FileUtil.class);

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

	public static void deleteLocalDirectory(File directory) throws Exception {
		if (!directory.isDirectory()) {
			throw new Exception("File " + directory.getAbsolutePath() + " is not a directory.");
		}
		logger.debug("Deleting directory " + directory.getAbsolutePath());
		Files.walkFileTree(directory.toPath(), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
					throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException e)
					throws IOException {
				if (e == null) {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				} else {
					// directory iteration failed
					throw e;
				}
			}
		});
	}
}
