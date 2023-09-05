package com.spotify.Example.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.drive.model.File;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

import com.google.api.services.drive.Drive;
import java.security.GeneralSecurityException;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;

import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;


@Service
public class GDriveService {

    /**
     * Application name.
     */
    private static final String APPLICATION_NAME = "SpotifyApp";

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /**
     * Directory to store authorization tokens for this application.
     */
    private static final String TOKENS_DIRECTORY_PATH = "C:\\Users\\dani\\tokenDriveApi";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES =
            Collections.singletonList(DriveScopes.DRIVE_FILE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static final String folderId = "1qY9cyvxch_tRDDjByK27FMn7uDEhd0Ml";



    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = GDriveService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setHost("localhost").setPort(8089).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("daniel98bm@gmail.com");
    }


    public Drive getInstance() throws GeneralSecurityException, IOException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        return service;
    }


    public String uploadFile(List<MultipartFile> multipartFiles) throws IOException, GeneralSecurityException {


        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        try {
            for(MultipartFile songsToAdd : multipartFiles) {
                System.out.println(songsToAdd.getOriginalFilename());
                if (null != songsToAdd) {
                    File fileMetadata = new File();
                    fileMetadata.setParents(Collections.singletonList(folderId));
                    fileMetadata.setName(songsToAdd.getOriginalFilename());
                    File uploadFile = getInstance()
                            .files()
                            .create(fileMetadata, new InputStreamContent(
                                    songsToAdd.getContentType(),
                                    new ByteArrayInputStream(songsToAdd.getBytes()))
                            )
                            .setFields("id").execute();
                    System.out.println(uploadFile);

                }
            }
            return "success";
        } catch (Exception e) {
            System.out.printf("Error: "+ e);
        }
        return "sucess";
    }


    public  List<File> listFolderContent() throws GeneralSecurityException, IOException {
        String query = "'" + folderId + "' in parents";
        FileList result = getInstance().files().list()
                .setQ(query)
                .setPageSize(10)
                .setFields("nextPageToken, files(id, name)")
                .execute();
        return result.getFiles();
    }



    public String getLastFilesAdded(int songsToAdd) throws IOException, GeneralSecurityException {

        List<MultipartFile> songsToUpload = new ArrayList<>();

        // Specify the path to the folder
        String folderPath = "D:\\songsFormatMP3";

        // Create a File object for the folder
        java.io.File folder = new java.io.File(folderPath);
        java.io.File[] files = folder.listFiles();

        if (files != null && files.length >= songsToAdd) {
            // Sort the files by last modified timestamp in descending order
            Arrays.sort(files, Comparator.comparingLong(java.io.File::lastModified).reversed());

            //List to save the last songs added
            List<java.io.File> songsAdded = new ArrayList<>();

            for(int i = 0; i < songsToAdd; i++) {
                songsAdded.add(files[i]);
            }

            if (songsAdded != null && !songsAdded.isEmpty()) {

                for (java.io.File song : songsAdded) {
                    FileInputStream input = new FileInputStream(song);
                    MultipartFile multipartFile = new MockMultipartFile("file",
                            song.getName(), "text/plain", IOUtils.toByteArray(input));
                    songsToUpload.add(multipartFile);
                }
            }
        } else {
            System.out.println("There are not enough files in the folder.");
        }

        return uploadFile(songsToUpload);
    }
}
