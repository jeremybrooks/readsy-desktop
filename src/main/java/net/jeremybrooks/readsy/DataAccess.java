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

import net.jeremybrooks.readsy.bo.Entry;
import net.jeremybrooks.readsy.dropbox.DropboxHelper;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Jeremy Brooks
 */
public class DataAccess {
	private static Logger logger = Logger.getLogger(DataAccess.class);

	private static SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
	private static SimpleDateFormat MMdd = new SimpleDateFormat("MMdd");

	private static Map<String, String> fileCache = new HashMap<>();

	private static boolean useDropbox() {
		return PropertyManager.getInstance().getPropertyAsBoolean(PropertyManager.DROPBOX_ENABLED);
	}

	/**
	 * Returns a List of directories that contain readsy data.
	 * <p>This method will look in Dropbox if Dropbox sync is configured; otherwise it will look in
	 * the content directory.</p>
	 *
	 * @return sorted list of data directories.
	 */
	public static List<String> getDataDirectoryNames() {
		logger.debug("Getting data directory names; useDropbox=" + useDropbox());
		List<String> names = new ArrayList<>();
		try {
			if (useDropbox()) {
				names.addAll(DropboxHelper.getInstance().getFoldersAtPath("/"));
			} else {
				File[] files = Readsy.getContentDir().listFiles(new DirectoryFileFilter());
				if (files != null) {
					for (File file : files) {
						names.add(file.getName());
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error getting data directory names.", e);
		}
		Collections.sort(names);
		return names;
	}

	public static Properties getMetadata(String contentDirectory) throws Exception {
		logger.debug("Getting metadata for " + contentDirectory + ",useDropbox=" + useDropbox());
		Properties properties = new Properties();
		if (useDropbox()) {
			DropboxHelper.getInstance().loadPropertiesFromFile(properties, contentDirectory + "/metadata");
		} else {
			File dir = new File(Readsy.getContentDir(), contentDirectory);
			File metadataFile = new File(dir, "metadata");
			if (metadataFile.exists()) {
				properties.load(new FileInputStream(metadataFile));
			}
		}
		return properties;
	}

	public static void saveMetadata(String contentDirectory, Properties metadata) throws Exception {
		logger.debug("Saving metadata. contentDirectory=" + contentDirectory + ",metadata=" + metadata + ",useDropbox=" + useDropbox());
		if (useDropbox()) {
			DropboxHelper.getInstance().uploadProperties(contentDirectory + "/metadata", metadata);
		} else {
			OutputStream out = null;
			File dir = new File(Readsy.getContentDir(), contentDirectory);
			File metadataFile = new File(dir, "metadata");
			try {
				out = new FileOutputStream(metadataFile);
				metadata.store(out, "");
			} finally {
				FileUtil.close(out);
			}
		}
	}

	/**
	 * Get the entry for the specified date.
	 * <p>If there are any errors, they will be logged and null returned.</p>
	 * <p>If there is no entry for the specified date, null is returned.</p>
	 *
	 * @param date
	 * @param metadata
	 * @param contentDirectory
	 * @return
	 */
	public static Entry getEntryForDateFromDirectory(Date date, Properties metadata, String contentDirectory) {
		String filename = MMdd.format(date);
		logger.debug("Getting entry; date=" + date + ",contentDirectory=" + contentDirectory + ",filename=" + filename + ",useDropbox=" + useDropbox());
		Entry entry = null;

		// only get the entry if the file is for any year or the current year
		if (metadata.getProperty("year").equals("0") || metadata.getProperty("year").equals(yyyy.format(date))) {
			try {
				if (useDropbox()) {
					String path = contentDirectory + "/" + filename;
					String content = null;
					if (fileCache.containsKey(path)) {
						logger.debug("Content was in cache.");
						content = fileCache.get(path);
					} else {
						logger.debug("Content not in cache, loading from Dropbox.");
						byte[] data = DropboxHelper.getInstance().getFile(path);
						if (data != null) {
							content = new String(data, "UTF-8");
						}
						fileCache.put(path, content);
					}
					entry = entryFromReader(new StringReader(content));
				} else {
					File dir = new File(Readsy.getContentDir(), contentDirectory);
					entry = entryFromReader(new FileReader(new File(dir, filename)));
				}
			} catch (Exception e) {
				entry = null;
				logger.error("Error getting entry for date.", e);
			}
		}

		return entry;
	}

	private static Entry entryFromReader(Reader reader) throws Exception {
		BufferedReader in = null;
		Entry entry = new Entry();
		try {
			in = new BufferedReader(reader);
			entry.setHeading(in.readLine());
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line).append('\n');
			}
			entry.setText(sb.toString());
		} finally {
			FileUtil.close(in);
		}
		return entry;
	}

	public static boolean directoryExists(String contentDirectory) throws Exception {
		logger.debug("Checking for directory " + contentDirectory + ",useDropbox=" + useDropbox());
		boolean exists;
		if (useDropbox()) {
			exists = DropboxHelper.getInstance().pathExists(contentDirectory);
		} else {
			exists = new File(Readsy.getContentDir(), contentDirectory).exists();
		}
		return exists;
	}

	public static void createDirectory(String contentDirectory) throws Exception {
		logger.debug("Creating directory " + contentDirectory + ",useDropbox=" + useDropbox());
		if (useDropbox()) {
			DropboxHelper.getInstance().createFolder(contentDirectory);
		} else {
			File f = new File(Readsy.getContentDir(), contentDirectory);
			if (!f.mkdirs()) {
				throw new Exception("Unable to create directory " + contentDirectory);
			}
		}
	}

	public static void saveFile(String path, byte[] data) throws Exception {
		logger.debug("Saving file " + path + "," + data.length + " bytes,useDropbox=" + useDropbox());
		if (useDropbox()) {
			DropboxHelper.getInstance().uploadFile(path, data);
		} else {
			OutputStream out = null;
			File f = new File(Readsy.getContentDir(), path);
			try {
				out = new FileOutputStream(f);
				out.write(data);
				out.flush();
			} finally {
				FileUtil.close(out);
			}
		}
	}


	public static void deletePath(String path) throws Exception {
		logger.debug("Deleting path " + path + ",useDropbox=" + useDropbox());
		if (useDropbox()) {
			DropboxHelper.getInstance().delete(path);
		} else {
			File file = new File(Readsy.getContentDir(), path);
			if (file.isFile()) {
				Files.delete(file.toPath());
			} else if (file.isDirectory()) {
				Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {
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
			} else {
				throw new IOException("Cannot delete " + file.getAbsolutePath() + " - this is not a file or directory.");
			}
		}
	}
}