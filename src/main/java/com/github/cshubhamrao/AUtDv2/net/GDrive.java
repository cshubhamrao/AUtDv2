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
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shubham Rao <cshubhamrao@gmail.com>
 */
public class GDrive {

    private static final Logger logger = Log.logger;

    private static final String APP_NAME = "AUtDv2";
    private static final File DATA_DIR = new File(".AUtDv2-creds");
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE_FILE);
    private static FileDataStoreFactory DS_FACTORY;
    private static HttpTransport HTTP_TRANSPORT;

    static {
        logger.log(Level.INFO, "Setting up DS Factory & HTTP Transport");

        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DS_FACTORY = new FileDataStoreFactory(DATA_DIR);
        }
        catch (GeneralSecurityException | IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private static Credential authorize() throws IOException {
        logger.log(Level.INFO, "Attempting authorization");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(GDrive.class.getResourceAsStream("/net/client_secret.json")));
        GoogleAuthorizationCodeFlow flow = 
                new GoogleAuthorizationCodeFlow.Builder
                                               (HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DS_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential;
        LocalServerReceiver receiver = new LocalServerReceiver();
        credential = new AuthorizationCodeInstalledApp(flow, receiver)
                .authorize("user");
        logger.log(Level.CONFIG, "Saved credentials to {0}", DATA_DIR.getPath());
        return credential;
    }
    
    public static Drive getDriveService() {
        Drive service = null;
        logger.log(Level.INFO, "Attempting to get Drive Service");
        try {
            Credential cred = authorize();
            service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, cred)
                    .setApplicationName(APP_NAME)
                    .build();
        }
        catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return service;
    }
}
