package net.silvaniax.plugin.spigotgbackup.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import net.silvaniax.plugin.spigotgbackup.abstracts.GBackupLogger;

import java.io.*;

import java.security.GeneralSecurityException;

import java.util.Collections;
import java.util.List;

public class DriveManager implements GBackupLogger {

    private static final String APPLICATION_NAME = "Spigot GBackup";
    private static final String CREDENTIALS_FILE_PATH = "./plugins/GBackup/client_secret.json";
    private static final JsonFactory JSON_FACTORY = new GsonFactory();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_METADATA_READONLY);
    private static final String BACKUP_DIRECTORY_PATH = "spigot_minecraft_backups";


    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        java.io.File credentialsFile = new java.io.File(CREDENTIALS_FILE_PATH);
        FileInputStream in = new FileInputStream(credentialsFile);

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(BACKUP_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void uploadBackup(java.io.File filePath) {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            File fileMetadata = new File();
            fileMetadata.setName(filePath.getName());
            FileContent mediaContent = new FileContent("application/zip", filePath);

            try {
                File file = service.files().create(fileMetadata, mediaContent)
                        .setFields("id")
                        .execute();

                LOGGER.info("Successfully uploaded to Drive with ID: " + file.getId());
            } catch (GoogleJsonResponseException e) {
                LOGGER.warning("Unable to upload file!");
                e.printStackTrace();
            }
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }


    }

}
