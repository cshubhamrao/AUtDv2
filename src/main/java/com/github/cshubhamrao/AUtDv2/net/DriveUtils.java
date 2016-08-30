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
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shubham Rao <cshubhamrao@gmail.com>
 */
public class DriveUtils {

    private static Logger logger = Log.logger;

    private static Drive service;
    public static File fileMetadata = new File();
    public static FileContent fileContent;
    public static Thread t;

    static {
        t = new Thread(() ->
        service = GDrive.getDriveService()
        );
        t.start();
    }

    public static void upload(java.io.File localFile) {
        fileMetadata.setName(localFile.getName());
        fileContent = new FileContent("text/plain", localFile);
        new Thread(() -> {
            try {
                File uledFile = new File();
                uledFile = service.files().create(fileMetadata, fileContent)
                        .setFields("id").execute();
                System.out.println(uledFile.getId());
                uledFile = service.files().get(uledFile.getId()).setFields("webViewLink,mimeType")
                        .execute();
                System.out.println(uledFile.getWebViewLink() +"\n"+  uledFile.getMimeType());
                logger.log(Level.INFO,"Upload finished");
            }
            catch (java.net.SocketTimeoutException | java.net.UnknownHostException ex) {
                try {
                    logger.log(Level.SEVERE, null, ex.getCause());
                    Thread.sleep(500);
                    upload(localFile);
                } catch (InterruptedException ex1) {
                    logger.log(Level.SEVERE, null, ex1.getCause());
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }, "File Upload thread").start();
    }
}