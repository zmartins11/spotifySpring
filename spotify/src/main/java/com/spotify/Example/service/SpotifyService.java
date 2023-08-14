package com.spotify.Example.service;

import com.spotify.Example.model.SavedTrackEntity;
import com.spotify.Example.model.SessionEntity;
import com.spotify.Example.repository.SavedTrackRepository;
import com.spotify.Example.repository.SessionRepository;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.data.library.GetUsersSavedTracksRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpotifyService {

    SavedTrackRepository savedTrackRepository;
    SessionRepository sessionRepository;

    @Autowired
    public SpotifyService(SavedTrackRepository savedTrackRepository, SessionRepository sessionRepository) {
        this.savedTrackRepository = savedTrackRepository;
        this.sessionRepository = sessionRepository;
    }

    public SavedTrack[] getUserTracks(SpotifyApi spotifyApi) throws IOException, ParseException, SpotifyWebApiException {
        int offset = 0;
        List<SavedTrack> allSavedTracks = new ArrayList<>();

        while(true) {
            final GetUsersSavedTracksRequest getUsersSavedTracksRequest = spotifyApi.getUsersSavedTracks()
                    .offset(offset)
                    .build();

            final Paging<SavedTrack> savedTrackPaging = getUsersSavedTracksRequest.execute();
            SavedTrack[] savedTracks = savedTrackPaging.getItems();

            if(savedTracks.length == 0) {
                break;
            }

            allSavedTracks.addAll(Arrays.asList(savedTracks));
            offset += savedTracks.length;
        }
        return allSavedTracks.toArray(new SavedTrack[0]);
    }


    public int getNumberOfSavedTracks(SpotifyApi spotifyApi) throws IOException, ParseException, SpotifyWebApiException {
        int offset = 0;
        int totalCount = 0;

        while (true) {
            final GetUsersSavedTracksRequest getUsersSavedTracksRequest = spotifyApi.getUsersSavedTracks()
                    .offset(offset)
                    .build();
        try {
            final Paging<SavedTrack> savedTrackPaging = getUsersSavedTracksRequest.execute();
            SavedTrack[] savedTracks = savedTrackPaging.getItems();

            if (savedTracks.length == 0) {
                break;
            }

            totalCount += savedTracks.length;
            offset += savedTracks.length;
        } catch (Exception e) {
            //token expired
            break;
            }
        }

        return totalCount;
    }

//    public int getNumberDbTracks() {
//
//    }


    public void saveTracks(SavedTrack[] savedTracks) {
        for(SavedTrack track : savedTracks) {
            String song = track.getTrack().getName();
            String firstArtist = Arrays.stream(track.getTrack().getArtists())
                    .findFirst()
                    .map(ArtistSimplified::getName)
                    .orElse("");

            SavedTrackEntity trackEntity = new SavedTrackEntity();
            trackEntity.setSongName(song);
            trackEntity.setArtist(firstArtist);

            savedTrackRepository.save(trackEntity);
        }
    }

    private Artist[] fetchUserTopArtists(SpotifyApi spotifyApi) {
        final GetUsersTopArtistsRequest getUsersTopArtistsRequest = spotifyApi.getUsersTopArtists()
                .time_range("medium_term")
                .limit(10)
                .offset(5)
                .build();

        try {
            final Paging<Artist> artistPaging = getUsersTopArtistsRequest.execute();

            return artistPaging.getItems();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Artist[0];
    }

    public void formatMp4() {
        String inputFolder = "D:\\codex-cuphead";
		String outpuFolder = "D:\\songsFormatMP3";
        String ffmpegPath = "C:\\ffmpeg\\bin\\ffmpeg.exe";

        File inputDir = new File(inputFolder);
        File [] videoFiles = inputDir.listFiles((dir, name) -> name.endsWith(".mp4"));


        if (videoFiles != null) {
            for (File videoFile : videoFiles) {
                String inputFilePath = videoFile.getAbsolutePath();
                String outputFilePath = outpuFolder + "\\" + videoFile.getName().replace(".mp4", ".mp3");

                try {
                    ProcessBuilder processBuilder = new ProcessBuilder(ffmpegPath, "-i", inputFilePath, outputFilePath);
                    Process process = processBuilder.start();
                    int exitCode = process.waitFor();

                    if(exitCode == 0) {
                        System.out.println("Convertion of " + videoFile.getName() + " completed successfully");
                    } else {
                        System.out.println("Convertion of " + videoFile.getName() + " failed with exit code: " + exitCode);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
         }

    }

    public void storeSession(SpotifyApi spotifyApi) {
        SessionEntity sessionEntity = new SessionEntity();

        sessionRepository.deleteAll();

        sessionEntity.setAccessToken(spotifyApi.getAccessToken());
        sessionEntity.setRefreshToken(spotifyApi.getRefreshToken());

        sessionRepository.save(sessionEntity);
    }

    public SessionEntity getSession() {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        SessionEntity session  = (SessionEntity) sessionRepository.findAll(sort).stream().findFirst().orElse(null);
        return session;
    }

    public SpotifyApi handleTokenExpired(SpotifyApi spotifyApi) throws IOException, ParseException, SpotifyWebApiException {
        AuthorizationCodeRefreshRequest refreshRequest = spotifyApi.authorizationCodeRefresh()
                .refresh_token(spotifyApi.getRefreshToken())
                .build();

        try {
            final AuthorizationCodeCredentials credentials = refreshRequest.execute();
            String newAccessToken = credentials.getAccessToken();
            String newRefreshToken = credentials.getRefreshToken();

            spotifyApi.setAccessToken(newAccessToken);
            spotifyApi.setRefreshToken(newRefreshToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return spotifyApi;
    }

    public int getNumberDbTracks() {
        return (int) savedTrackRepository.count();
    }
}
