/*
 * The MIT License
 *
 * Copyright 2016 Shubham Rao <cshubhamrao@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.cshubhamrao.AUtDv2.net;

import com.github.cshubhamrao.AUtDv2.util.Log;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for handling various Google Drive related tasks.
 *
 * @author Shubham Rao (cshubhamrao@gmail.com)
 */
public class GoogleDriveTask {

    private static Logger logger = Log.logger;

    private static Credential credential;

    private static Drive service;

    static private final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE_FILE);
    static private final String APP_NAME = "AUtDv2";

    static private HttpTransport HTTP_TRANSPORT;
    static private MemoryDataStoreFactory DS_FACTORY;
//    static private final java.io.File DATA_DIR = new java.io.File(".AUtDv2-creds");

    static private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static void init() {
        try {
        } catch (SecurityException ex) {
            logger.log(Level.WARNING, "Error in setting up credential store", ex);
        }

        logger.log(Level.INFO, "Setting up DS Factory & HTTP Transport");

        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DS_FACTORY = new MemoryDataStoreFactory();
        } catch (GeneralSecurityException | IOException ex) {
            logger.log(Level.SEVERE, "Error in initialization", ex);
        }

        logger.log(Level.INFO, "Attempting authorization");
        GoogleClientSecrets clientSecrets;
        GoogleAuthorizationCodeFlow flow = null;
        try {
            clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                    new InputStreamReader(GoogleDriveTask.class
                            .getResourceAsStream("/net/client_secret.json")));
            flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                    clientSecrets, SCOPES)
                    .setDataStoreFactory(DS_FACTORY)
                    .setAccessType("offline")
                    .build();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Unable to build auth flow", ex);
        }
        try {
            credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver())
                    .authorize("user");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Unable to authorize user", ex);
        }
    }

    private static void authorize() {
        init();
        if (credential != null) {
            logger.log(Level.INFO, "Attempting to get Drive Service");
            service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(APP_NAME)
                    .build();
        }
    }

    /**
     * An "Upload" to Google Drive task.
     *
     * Uploads a localFile to GoogleDrive
     */
    public class UploadTask implements Callable<String> {

        private final java.io.File localFile;
        private final String description;

        /**
         * Creates an UploadTask to upload {@code localFile}
         *
         * @param localFile file to upload.
         * @param description file description
         */
        public UploadTask(java.io.File localFile, String description) {
            this.localFile = localFile;
            this.description = description;
        }

        /**
         * Creates an UploadTask to upload {@code localFile} with description.
         *
         * @param localFile
         */
        public UploadTask(java.io.File localFile) {
            this(localFile, "");
        }

        /**
         *
         * @return fileId of file uploaded, null if authorization fails.
         * @throws Exception
         */
        @Override
        public String call() throws Exception {
            String fileId = "";
            authorize();
            if (credential == null) {
                return null;
            }
            try {
                while (isFileChanging()) {
                    logger.log(Level.INFO, "Waiting for file");
                    Thread.sleep(250);
                }
            } catch (InterruptedException ex) {
                logger.log(Level.WARNING, "Sleep Interrupted", ex);
            }
            File fileMetadata = new File();
            FileContent content;
            fileMetadata.setName(localFile.getName());
            fileMetadata.setDescription(description);
            content = new FileContent(Files.probeContentType(localFile.toPath()), localFile);
            try {
                File uploadedFile = service.files().create(fileMetadata, content)
                        .setUseContentAsIndexableText(Boolean.FALSE)
                        .setFields("id")
                        .execute();
                uploadedFile = service.files().get(uploadedFile.getId()).setFields("webViewLink,id")
                        .execute();
                logger.log(Level.INFO, "File {0} available at link {1}",
                        new String[]{uploadedFile.getId(), uploadedFile.getWebViewLink()});
                logger.log(Level.INFO, "Upload finished");
                fileId = uploadedFile.getId();
            } catch (java.net.SocketTimeoutException | java.net.UnknownHostException ex) {
                logger.log(Level.WARNING, "Network Error", ex);
                Thread.sleep(5000);
                fileId = this.call();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Error Uploading", ex);
            } finally {
                return fileId;
            }
        }

        private boolean isFileChanging() throws InterruptedException {
            long oldSize = localFile.length();
            Thread.sleep(750);
            long newSize = localFile.length();
            return oldSize != newSize;
        }
    }

    /**
     * A "Download" task.
     *
     * NOT YET IMPLEMENTED.
     */
    public class DownloadTask implements Callable<String> {

        /**
         *
         * @return @throws Exception
         */
        @Override
        public String call() throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
