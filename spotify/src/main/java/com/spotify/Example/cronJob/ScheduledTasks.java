package com.spotify.Example.cronJob;

import com.spotify.Example.enums.KeysEnum;
import com.spotify.Example.model.SessionEntity;
import com.spotify.Example.repository.SessionRepository;
import com.spotify.Example.service.ApiService;
import com.spotify.Example.service.GDriveService;
import com.spotify.Example.service.SpotifyService;
import org.apache.hc.core5.http.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.SavedTrack;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class ScheduledTasks {

    Logger logger = LogManager.getLogger(ScheduledTasks.class);

    private SessionRepository sessionRepository;
    private SpotifyService spotifyService;
    private ApiService apiService;
    private GDriveService gDriveService;


    @Autowired
    public ScheduledTasks(SessionRepository sessionRepository, SpotifyService spotifyService, ApiService apiService, GDriveService gDriveService) {
        this.sessionRepository = sessionRepository;
        this.spotifyService = spotifyService;
        this.apiService = apiService;
        this.gDriveService = gDriveService;
    }


    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void scheduleFixedDelayTask() throws IOException, ParseException, SpotifyWebApiException, GeneralSecurityException {

        SessionEntity session = spotifyService.getSession();
        long tokenTime = session.getTimestamp();

        if (session != null) {
            SpotifyApi spotifyApi = new SpotifyApi.Builder()
                    .setClientId(KeysEnum.CLIENT_ID.getKey())
                    .setClientSecret(KeysEnum.CLIENT_SECRET.getKey())
                    .setAccessToken(session.getAccessToken())
                    .setRefreshToken(session.getRefreshToken())
                    .build();

            SavedTrack[] savedTrackList = null;
            int numberTracks = 0;

            try {
                savedTrackList = spotifyService.getUserTracks(spotifyApi);
            } catch (Exception e) {
                System.out.println("The access token expired");
                logger.info("The access token expired");
            }

            if(savedTrackList == null || savedTrackList.length == 0) {
                System.out.println("getting refreshToken");
                SpotifyApi newSpotifyApi = spotifyService.handleTokenExpired(spotifyApi, tokenTime, session.getRefreshToken());
                savedTrackList = spotifyService.getUserTracks(newSpotifyApi);
            }

            numberTracks = savedTrackList.length;

            //logic to compare currentTracks and saved tracks
            System.out.println("tracks in playlist: " + numberTracks);
            logger.info("tracks in playlist: " + numberTracks);

            //get number of tracks in database
            int savedDbTracks = spotifyService.getNumberDbTracks();
            System.out.println("tracks in database : " + savedDbTracks);
            logger.info("tracks in database : " + savedDbTracks);

            if(savedDbTracks < numberTracks) {
                int newSongs = numberTracks - savedDbTracks;
                System.out.println("number of new songs:" + newSongs);
                logger.info("number of new songs:" + newSongs);
                SavedTrack[] newSongsToAdd = Arrays.stream(savedTrackList).limit(newSongs).collect(Collectors.toList()).toArray(new SavedTrack[newSongs]);
                spotifyService.saveTracks(newSongsToAdd);
                apiService.callFlaskApi(newSongsToAdd);
                gDriveService.getLastFilesAdded(newSongs);
            }

        }

    }
}
