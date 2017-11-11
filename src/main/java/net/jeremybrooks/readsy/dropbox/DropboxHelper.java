/*
 * readsy - read something new every day <http://jeremybrooks.net/readsy>
 *
 * Copyright (c) 2013-2017  Jeremy Brooks
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

package net.jeremybrooks.readsy.dropbox;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.WriteMode;
import net.jeremybrooks.readsy.PropertyManager;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Jeremy Brooks
 */
public class DropboxHelper {
	private Logger logger = Logger.getLogger(DropboxHelper.class);
	private DbxClientV2 client;
	private static DropboxHelper instance;
	private static DbxWebAuth webAuth;


  private DropboxHelper() throws DbxException {
		String accessToken = PropertyManager.getInstance().getProperty(PropertyManager.DROPBOX_ACCESS_TOKEN);
		if (accessToken == null) {
			throw new DbxException("Dropbox access has not been configured.");
		}
		DbxRequestConfig requestConfig = new DbxRequestConfig("readsy/1.0");
		client = new DbxClientV2(requestConfig, accessToken);
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
		client.files().createFolderV2(remotePath);
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

		try (FileInputStream fis = new FileInputStream(file)) {
			logger.debug("Attempting to upload file " + file.getAbsolutePath() + " to " + remotePath);
			client.files().uploadBuilder(remotePath)
          .withMode(WriteMode.OVERWRITE)
          .withAutorename(false)
          .start()
          .uploadAndFinish(fis);
			logger.debug("Upload successful.");
		} catch (DbxException de) {
			throw de;
		} catch (Exception e) {
			throw new DbxException("Unable to upload file due to exception.", e);
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
			try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
				client.files().uploadBuilder(remotePath)
            .withMode(WriteMode.OVERWRITE)
            .withAutorename(false)
            .start()
            .uploadAndFinish(in);
			} catch (DbxException de) {
				throw de;
			} catch (Exception e) {
				throw new DbxException("Unable to upload file due to exception.", e);
			}
		}


	/**
	 * Save a Properties object to a remote path.
	 *
	 * @param remotePath remote destination.
	 * @param data bytes to save.
	 * @throws DbxException if there are any errors.
	 */
	public void uploadPropertiesData(String remotePath, byte[] data) throws DbxException {
		remotePath = normalizePath(remotePath);

		logger.debug("Attempting to save property data to " + remotePath);
		try {
			uploadFile(remotePath, data);
		} catch (DbxException de) {
			throw de;
		} catch (Exception e) {
			throw new DbxException("Unable to upload properties due to exception.", e);
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
		client.files().deleteV2(remotePath);
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
		boolean exists = true;
		try {
      client.files().listFolder(remotePath);
    } catch (ListFolderErrorException lfee) {
		  exists = false;
    }
    return exists;
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

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			client.files().downloadBuilder(remotePath)
          .start()
          .download(out);
			properties.load(new ByteArrayInputStream(out.toByteArray()));
		} catch (DbxException de) {
			throw de;
		} catch (Exception e) {
			throw new DbxException("There was an error while getting file " + remotePath, e);
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
		byte[] data;
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			client.files().downloadBuilder(remotePath)
          .start()
          .download(out);
			data = out.toByteArray();
		} catch (DbxException de) {
			throw de;
		} catch (Exception e) {
			throw new DbxException("Error while reading file from " + remotePath, e);
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

		// root directory should be specified as "" rather than "/"
		if (remotePath.equals("/")) {
		  remotePath = "";
    }
		logger.debug("Getting folders at path " + remotePath);
		ListFolderResult folderResult = null;
		List<Metadata> metadataList = new ArrayList<>();
		do {
		  if (folderResult == null) {
		    folderResult = client.files().listFolder(remotePath);
      } else {
		    folderResult = client.files().listFolderContinue(folderResult.getCursor());
      }
      metadataList.addAll(folderResult.getEntries());
    } while (folderResult.getHasMore());

		List<String> list = new ArrayList<>();
		for (Metadata metadata : metadataList) {
      if (metadata instanceof FolderMetadata) {
        list.add(metadata.getName());
      }
    }
		return list;
	}


	public static String startWebAuth() throws Exception {
	  InputStream in = DropboxHelper.class.getResourceAsStream("/secret.json");
      DbxAppInfo appInfo = DbxAppInfo.Reader.readFully(in);
      DbxRequestConfig requestConfig = new DbxRequestConfig("readsy/1.0");
      webAuth = new DbxWebAuth(requestConfig, appInfo);
      DbxWebAuth.Request webAuthRequest = DbxWebAuth.newRequestBuilder()
          .withNoRedirect()
          .build();

      return webAuth.authorize(webAuthRequest);
	}

	public static String finishWebAuth(String authorizationCode) throws DbxException {
		if (webAuth == null) {
			return null;
		}

    DbxAuthFinish authFinish = webAuth.finishFromCode(authorizationCode.trim());
    webAuth = null;
    return authFinish.getAccessToken();
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
