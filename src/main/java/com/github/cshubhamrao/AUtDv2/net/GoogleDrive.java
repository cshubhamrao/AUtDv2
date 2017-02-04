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
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for handling various Google Drive related tasks.
 *
 * @author Shubham Rao (cshubhamrao@gmail.com)
 */
public abstract class GoogleDrive {

    private static Logger logger = Log.logger;

    private static MemoryDataStoreFactory DS_FACTORY;

    private static HttpTransport HTTP_TRANSPORT;
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();
    private static Credential credential;

    GoogleDrive() {
        logger.log(Level.INFO, "Setting up DS Factory & HTTP Transport");
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DS_FACTORY = new MemoryDataStoreFactory();
        } catch (GeneralSecurityException | IOException ex) {
            logger.log(Level.SEVERE, "Error in initialization", ex);
        }
    }

    public static void invalidate() {
        credential = null;
    }

    private void authorize() {
        logger.log(Level.INFO, "Attempting authorization");
        GoogleAuthorizationCodeFlow flow = null;
        try {
            InputStreamReader jsonFile = new InputStreamReader(
                    GoogleDrive.class.
                            getResourceAsStream("/net/client_secret.json"));
            GoogleClientSecrets clientSecrets =
                    GoogleClientSecrets.load(JSON_FACTORY, jsonFile);
            List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE_FILE);
            flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT,
                    JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(DS_FACTORY)
                    .setAccessType("offline")
                    .build();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Unable to build auth flow", ex);
        }
        try {
            credential = new AuthorizationCodeInstalledApp(
                    flow, new LocalServerReceiver()).authorize("user");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Unable to authorize user", ex);
        }
    }

    Drive getService() {
        if (credential == null) {
            System.out.println("NO CREDS");
            authorize();
        }
        logger.log(Level.INFO, "Attempting to get Drive Service");
        String APP_NAME = "AUtDv2";
        Drive service =
                new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APP_NAME).build();
        return service;
    }

}
