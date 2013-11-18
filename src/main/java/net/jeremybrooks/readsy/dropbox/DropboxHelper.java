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

package net.jeremybrooks.readsy.dropbox;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.dropbox.core.DbxWriteMode;
import net.jeremybrooks.readsy.FileUtil;
import net.jeremybrooks.readsy.PropertyManager;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * @author Jeremy Brooks
 */
public class DropboxHelper {
	private Logger logger = Logger.getLogger(DropboxHelper.class);
	private DbxClient client;
	private static DropboxHelper instance;
	private static DbxWebAuthNoRedirect webAuth;


	private DropboxHelper() throws DbxException {
		String accessToken = PropertyManager.getInstance().getProperty(PropertyManager.DROPBOX_ACCESS_TOKEN);
		if (accessToken == null) {
			throw new DbxException("Dropbox access has not been configured.");
		}
		DbxRequestConfig requestConfig = new DbxRequestConfig("readsy/1.0", Locale.getDefault().toString());
		client = new DbxClient(requestConfig, accessToken);
	}

	public static DropboxHelper getInstance() throws DbxException {
		if (instance == null) {
			instance = new DropboxHelper();
		}
		return instance;
	}

	/**
	 * Create a folder at the specified remote path.
	 *
	 * @param remotePath the remote path to create a folder.
	 * @throws DbxException if there are any errors.
	 */
	public void createFolder(String remotePath) throws DbxException {
		remotePath = normalizePath(remotePath);
		logger.debug("Creating " + remotePath);
		client.createFolder(remotePath);
	}


	/**
	 * Upload the specified file to the specified remote path.
	 *
	 * @param remotePath remote destination.
	 * @param file local file to upload.
	 * @throws DbxException if there are any errors.
	 */
	public void uploadFile(String remotePath, File file) throws DbxException {
		remotePath = normalizePath(remotePath);

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			logger.debug("Attempting to upload file " + file.getAbsolutePath() + " to " + remotePath);
			client.uploadFile(remotePath, DbxWriteMode.force(), file.length(), fis);
			logger.debug("Upload successful.");
		} catch (DbxException de) {
			throw de;
		} catch (Exception e) {
			throw new DbxException("Unable to upload file due to exception.", e);
		} finally {
			FileUtil.close(fis);
		}
	}


	/**
	 * Upload data to a remote path.
	 *
	 * @param remotePath remote destination.
	 * @param data data to upload.
	 * @throws DbxException if there are any errors.
	 */
	public void uploadFile(String remotePath, byte[] data) throws DbxException {
		remotePath = normalizePath(remotePath);

			logger.debug("Attempting to upload data to " + remotePath);
			DbxClient.Uploader uploader = null;
			try {
				uploader = client.startUploadFile(remotePath, DbxWriteMode.force(), data.length);
				uploader.getBody().write(data);
				uploader.finish();
			} catch (DbxException de) {
				throw de;
			} catch (Exception e) {
				throw new DbxException("Unable to upload file due to exception.", e);
			} finally {
				if (uploader != null) {
					uploader.close();
				}
			}
		}


	/**
	 * Save a Properties object to a remote path.
	 *
	 * @param remotePath remote destination.
	 * @param properties object to save.
	 * @throws DbxException if there are any errors.
	 */
	public void uploadProperties(String remotePath, Properties properties) throws DbxException {
		remotePath = normalizePath(remotePath);

		logger.debug("Attempting to save properties " + properties + " to " + remotePath);
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			properties.store(baos, "");
			byte[] data = baos.toByteArray();
			uploadFile(remotePath, data);
		} catch (DbxException de) {
			throw de;
		} catch (Exception e) {
			throw new DbxException("Unable to upload properties due to exception.", e);
		} finally {
			FileUtil.close(baos);
		}
	}


	/**
	 * Delete file or folder at the remote path.
	 *
	 * @param remotePath file or folder to delete.
	 * @throws DbxException if there are any errors.
	 */
	public void delete(String remotePath) throws DbxException {
		remotePath = normalizePath(remotePath);

		logger.debug("Deleting remote path " + remotePath);
		client.delete(remotePath);
	}


	/**
	 * Determine if the specified remote path exists.
	 *
	 * @param remotePath remote path to look for.
	 * @return true if the remote path exists, false otherwise.
	 * @throws DbxException if there are any errors.
	 */
	public boolean pathExists(String remotePath) throws DbxException {
		remotePath = normalizePath(remotePath);

		return (client.getMetadata(remotePath) != null);
	}


	/**
	 * Load a Properties object from the specified file.
	 *
	 * @param properties the properties object to load.
	 * @param remotePath file to load the properties from.
	 * @throws DbxException if there are any errors.
	 */
	public void loadPropertiesFromFile(Properties properties, String remotePath) throws DbxException {
		remotePath = normalizePath(remotePath);

		DbxClient.Downloader downloader = null;
		try {
			downloader = this.client.startGetFile(remotePath, null);
			properties.load(downloader.body);
		} catch (DbxException de) {
			throw de;
		} catch (Exception e) {
			throw new DbxException("There was an error while getting file " + remotePath, e);
		} finally {
			if (downloader != null) {
				downloader.close();
			}
		}
		logger.debug("Loaded properties from Dropbox: " + properties);
	}


	/**
	 * Get data from a remote file.
	 *
	 * @param remotePath remote location of the data.
	 * @return data from the remote file.
	 * @throws DbxException if there are any errors.
	 */
	public byte[] getFile(String remotePath) throws DbxException {
		remotePath = normalizePath(remotePath);

		logger.debug("Getting file " + remotePath + " from Dropbox...");
		DbxClient.Downloader downloader = null;
		BufferedReader in = null;
		byte[] data = null;
		try {
			downloader = this.client.startGetFile(remotePath, null);
			data = new byte[(int)downloader.metadata.numBytes];
			downloader.body.read(data);
		} catch (DbxException de) {
			throw de;
		} catch (Exception e) {
			throw new DbxException("Error while reading file from " + remotePath, e);
		} finally {
			FileUtil.close(in);
			if (downloader != null) {
				downloader.close();
			}
		}
		return data;
	}


	/**
	 * Get a list of folders at the specified remote path.
	 *
	 * @param remotePath remote path to find folders in.
	 * @return list of folders at the remote path.
	 * @throws DbxException if there are any errors.
	 */
	public List<String> getFoldersAtPath(String remotePath) throws DbxException {
		remotePath = normalizePath(remotePath);

		logger.debug("Getting folders at path " + remotePath);
		DbxEntry.WithChildren withChildren = client.getMetadataWithChildren(remotePath);
		List<String> list = new ArrayList<>();
		if (withChildren != null) {
			for (DbxEntry entry : withChildren.children) {
				if (entry.isFolder()) {
					logger.debug("Found folder " + entry.path);
					list.add(entry.path);
				}
			}
		}
		return list;
	}


	public static String startWebAuth() {
		DbxAppInfo appInfo = new DbxAppInfo(PropertyManager.getInstance().getProperty(PropertyManager.DROPBOX_APP_KEY),
				PropertyManager.getInstance().getProperty(PropertyManager.DROPBOX_APP_SECRET));
		DbxRequestConfig requestConfig = new DbxRequestConfig("readsy/1.0", Locale.getDefault().toString());

		webAuth = new DbxWebAuthNoRedirect(requestConfig, appInfo);
		return webAuth.start();
	}

	public static String finishWebAuth(String authorizationCode) throws DbxException {
		if (webAuth == null) {
			return null;
		}
		String accessToken = webAuth.finish(authorizationCode).accessToken;
		webAuth = null;
		return accessToken;
	}


	/* Make sure the path starts with a leading slash. */
	private String normalizePath(String remotePath) {
		if (!remotePath.startsWith("/")) {
			return "/" + remotePath;
		} else {
			return remotePath;
		}
	}
}
