/*
 * The MIT License
 *
 * Copyright 2016-17 Shubham Rao <cshubhamrao@gmail.com>.
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
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author "Shubham Rao (cshubhamrao@gmail.com)"
 */
public class UploadTask extends GoogleDrive implements Callable<String> {

    private static Logger logger = Log.logger;

    private final java.io.File localFile;
    private final String description;

//    Drive service = getService();
    private Drive service;

    /**
     * Creates an UploadTask to upload {@code localFile}
     *
     * @param localFile file to upload.
     * @param description file description
     */
    public UploadTask(java.io.File localFile, String description) {
        this.service = super.getService();
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
     * @return fileId of file uploaded, null on error.
     * @throws Exception
     */
    @Override
    public String call() throws Exception {
        String fileId = null;
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
        content = new FileContent(Files.probeContentType(localFile.toPath()),
                                  localFile);
        try {
            logger.log(Level.INFO, "Begin upload");
            long then = System.currentTimeMillis();
            File uploadedFile = service.files().create(fileMetadata, content)
                    .setUseContentAsIndexableText(Boolean.FALSE)
                    .setFields("id,webViewLink")
                    .execute();
            long now = System.currentTimeMillis();
            logger.log(Level.INFO, "File {0} available at link {1}",
                       new String[]{uploadedFile.getId(),
                                    uploadedFile.getWebViewLink()});
            logger.log(Level.INFO, "Upload finished, took {0}s",
                       (now - then) / 1.0e3);
            fileId = uploadedFile.getId();
        } catch (java.net.SocketTimeoutException |
                 java.net.UnknownHostException ex) {
            logger.log(Level.WARNING, "Network Error", ex);
            Thread.sleep(5000);
            fileId = this.call();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error Uploading", ex);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error", ex);
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
