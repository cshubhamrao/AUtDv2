/*
 * The MIT License
 *
 * Copyright 2016 Shubham Rao.
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
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A "Driver" for all the Google Drive Stuff.
 *
 * @author Shubham Rao <cshubhamrao@gmail.com>
 */
public class GoogleDriver {

    private static Logger logger = Log.logger;

    private Drive service;
    private String fileId;

    private Credential creds;

    /**
     * Spawns a new thread to undertake user authorization.
     *
     */
    public void authorize() {
        new Thread(() -> service = new GDrive().getDriveService()).start();
    }

    /**
     * Uploads the file to Google Drive.
     *
     * MESSY CODE. FIX THIS.
     *
     * @param localFile File to upload
     * @return
     */
    public String upload(java.io.File localFile) {
        try {
            if (localFile.length() == 0) {
                Thread.sleep(1000);
            }
            if (!localFile.exists()) {
                return "";
            }
            while (service == null) {
                Thread.sleep(1000);
                logger.log(Level.INFO, "Waiting for auth");
            }
            while (isFileChanging(localFile)) {
                Thread.sleep(500);
                logger.log(Level.FINE, "Waiting for file");
            }
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, "No sleep for our poor thread :(", ex);
        }

        File fileMetadata = new File();

        fileMetadata.setName(localFile.getName());
        fileMetadata.setDescription(
                "Database dump of DB " + localFile.getName().split("."));
        FileContent fileContent = new FileContent("text/plain", localFile);
        Thread uploadThread = new Thread(() -> {
            try {
                File uledFile;
                uledFile = service.files().create(fileMetadata, fileContent)
                        .setFields("id").execute();
                logger.log(Level.INFO, "Creating file with id {0}", uledFile.getId());
                this.creds = null;
                uledFile = service.files().get(uledFile.getId()).setFields("webViewLink,id")
                        .execute();
                logger.log(Level.INFO, "File available at link {0}", uledFile.getWebViewLink());
                logger.log(Level.INFO, "Upload finished");
                this.fileId = uledFile.getId();
            } catch (java.net.SocketTimeoutException | java.net.UnknownHostException ex) {
                try {
                    logger.log(Level.SEVERE, "Network Error", ex);
                    Thread.sleep(5000);
                    upload(localFile);
                } catch (InterruptedException ex1) {
                    logger.log(Level.SEVERE, "No sleep for our thread", ex1.getCause());
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Error Uploading", ex);
            }
        }, "File Upload thread");

        Executors.newSingleThreadExecutor()
                .submit(uploadThread);
        return this.fileId;
    }

    private boolean isFileChanging(java.io.File localFile) {
        long oldSize = localFile.length();
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, "No sleep for our poor thread :(", ex);
        }
        long newSize = localFile.length();
        return oldSize != newSize;
    }
}
